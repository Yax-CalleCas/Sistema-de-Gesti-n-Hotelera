import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, shareReplay, tap } from 'rxjs';
import { Persona } from '../models/persona.model';
import { ApiResponse } from '../models/ApiResponse';

@Injectable({ providedIn: 'root' })
export class PersonaService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8081/api/persona';
  private listarCache$?: Observable<ApiResponse<Persona[]>>;

  listar(): Observable<ApiResponse<Persona[]>> {
    if (!this.listarCache$) {
      this.listarCache$ = this.http.get<ApiResponse<Persona[]>>(`${this.baseUrl}/listar`).pipe(
        shareReplay(1)
      );
    }
    return this.listarCache$;
  }

  obtenerPorId(id: number): Observable<ApiResponse<Persona>> {
    return this.http.get<ApiResponse<Persona>>(`${this.baseUrl}/buscar/${id}`);
  }

  crear(persona: Persona): Observable<ApiResponse<Persona>> {
    return this.http.post<ApiResponse<Persona>>(`${this.baseUrl}/registrar`, persona).pipe(
      tap(() => this.limpiarCache()) // Limpiamos caché al crear
    );
  }

  actualizar(id: number, persona: Persona): Observable<ApiResponse<Persona>> {
    return this.http.put<ApiResponse<Persona>>(`${this.baseUrl}/actualizar/${id}`, persona).pipe(
      tap(() => this.limpiarCache()) // Limpiamos caché al actualizar
    );
  }

  eliminar(id: number): Observable<ApiResponse<string>> {
    return this.http.delete<ApiResponse<string>>(`${this.baseUrl}/eliminar/${id}`).pipe(
      tap(() => this.limpiarCache()) // Limpiamos caché al eliminar
    );
  }

  private limpiarCache() {
    this.listarCache$ = undefined;
  }
}
