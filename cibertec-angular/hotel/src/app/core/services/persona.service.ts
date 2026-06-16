import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, shareReplay } from 'rxjs';
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
    return this.http.post<ApiResponse<Persona>>(`${this.baseUrl}/registrar`, persona);
  }

  actualizar(id: number, persona: Persona): Observable<ApiResponse<Persona>> {
    return this.http.put<ApiResponse<Persona>>(`${this.baseUrl}/actualizar/${id}`, persona);
  }

  eliminar(id: number): Observable<ApiResponse<null>> {
    return this.http.delete<ApiResponse<null>>(`${this.baseUrl}/eliminar/${id}`);
  }
}
