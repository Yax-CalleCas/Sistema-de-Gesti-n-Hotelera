import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, shareReplay, tap } from 'rxjs';
import { ApiResponse } from '../models/ApiResponse';
import { Recepcion } from '../models/recepcion.model';

@Injectable({ providedIn: 'root' })
export class RecepcionService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8081/api/recepcion';
  private listarCache$?: Observable<ApiResponse<Recepcion[]>>;

  listar(): Observable<ApiResponse<Recepcion[]>> {
    if (!this.listarCache$) {
      this.listarCache$ = this.http.get<ApiResponse<Recepcion[]>>(`${this.baseUrl}/listar`).pipe(shareReplay(1));
    }
    return this.listarCache$;
  }

  private invalidateCache() { this.listarCache$ = undefined; }

  registrar(dto: any): Observable<ApiResponse<Recepcion>> {
    return this.http.post<ApiResponse<Recepcion>>(`${this.baseUrl}/registrar`, dto).pipe(tap(() => this.invalidateCache()));
  }

  registrarSalida(data: any): Observable<ApiResponse<boolean>> {
    return this.http.post<ApiResponse<boolean>>(`${this.baseUrl}/registrar-salida`, data).pipe(tap(() => this.invalidateCache()));
  }
  buscarPorId(id: number): Observable<ApiResponse<Recepcion>> {
    return this.http.get<ApiResponse<Recepcion>>(`${this.baseUrl}/buscar/${id}`);
  }
  buscarActivaPorHabitacion(idHabitacion: number): Observable<ApiResponse<any>> {
    return this.http.get<ApiResponse<any>>(`${this.baseUrl}/habitacion-activa/${idHabitacion}`);
  }



  actualizar(id: number, dto: Recepcion): Observable<ApiResponse<Recepcion>> {
    return this.http.put<ApiResponse<Recepcion>>(`${this.baseUrl}/actualizar/${id}`, dto);
  }

  eliminar(id: number): Observable<ApiResponse<null>> {
    return this.http.delete<ApiResponse<null>>(`${this.baseUrl}/eliminar/${id}`);
  }
}
