import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/ApiResponse';
import { Piso } from '../../core/models/piso';

@Injectable({ providedIn: 'root' })
export class PisoService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8081/api/piso';

  listar(): Observable<ApiResponse<Piso[]>> {
    return this.http.get<ApiResponse<Piso[]>>(`${this.baseUrl}/listar`);
  }

  buscar(id: number): Observable<ApiResponse<Piso>> {
    return this.http.get<ApiResponse<Piso>>(`${this.baseUrl}/buscar/${id}`);
  }

  registrar(piso: Piso): Observable<ApiResponse<Piso>> {
    return this.http.post<ApiResponse<Piso>>(`${this.baseUrl}/registrar`, piso);
  }

  actualizar(id: number, piso: Piso): Observable<ApiResponse<Piso>> {
    return this.http.put<ApiResponse<Piso>>(`${this.baseUrl}/actualizar/${id}`, piso);
  }

  eliminar(id: number): Observable<ApiResponse<null>> {
    return this.http.delete<ApiResponse<null>>(`${this.baseUrl}/eliminar/${id}`);
  }
}
