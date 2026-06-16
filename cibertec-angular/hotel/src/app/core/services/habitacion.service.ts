import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, shareReplay, tap } from 'rxjs';
import { ApiResponse } from '../models/ApiResponse';
import { Habitacion } from '../models/Habitacion';

@Injectable({ providedIn: 'root' })
export class HabitacionService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8081/api/habitacion';
  private listarCache$?: Observable<ApiResponse<Habitacion[]>>;

  listar(): Observable<ApiResponse<Habitacion[]>> {
    if (!this.listarCache$) {
      this.listarCache$ = this.http.get<ApiResponse<Habitacion[]>>(`${this.baseUrl}/listar`).pipe(
        shareReplay(1)
      );
    }
    return this.listarCache$;
  }

  // Método auxiliar para invalidar la caché tras una operación de escritura
  private invalidateCache(): void {
    this.listarCache$ = undefined;
  }

  buscarPorId(id: number): Observable<ApiResponse<Habitacion>> {
    return this.http.get<ApiResponse<Habitacion>>(`${this.baseUrl}/buscar/${id}`);
  }

  guardar(dto: Habitacion): Observable<ApiResponse<Habitacion>> {
    const obs = dto.idHabitacion
      ? this.http.put<ApiResponse<Habitacion>>(`${this.baseUrl}/actualizar/${dto.idHabitacion}`, dto)
      : this.http.post<ApiResponse<Habitacion>>(`${this.baseUrl}/registrar`, dto);

    return obs.pipe(tap(() => this.invalidateCache()));
  }

  eliminar(id: number): Observable<ApiResponse<null>> {
    return this.http.delete<ApiResponse<null>>(`${this.baseUrl}/eliminar/${id}`).pipe(
      tap(() => this.invalidateCache())
    );
  }
}
