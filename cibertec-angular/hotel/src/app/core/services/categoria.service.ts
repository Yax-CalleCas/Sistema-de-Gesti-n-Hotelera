import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, shareReplay } from 'rxjs';
import { ApiResponse } from '../models/ApiResponse';
import { Categoria } from '../../core/models/categoria.model';

@Injectable({ providedIn: 'root' })
export class CategoriaService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8081/api/categoria';
  private listarCache$?: Observable<ApiResponse<Categoria[]>>;

  listar(): Observable<ApiResponse<Categoria[]>> {
    if (!this.listarCache$) {
      this.listarCache$ = this.http.get<ApiResponse<Categoria[]>>(`${this.baseUrl}/listar`).pipe(shareReplay(1));
    }
    return this.listarCache$;
  }

  // Métodos de mutación permanecen igual para asegurar consistencia
  buscar(id: number): Observable<ApiResponse<Categoria>> {
    return this.http.get<ApiResponse<Categoria>>(`${this.baseUrl}/buscar/${id}`);
  }

  registrar(categoria: Categoria): Observable<ApiResponse<Categoria>> {
    return this.http.post<ApiResponse<Categoria>>(`${this.baseUrl}/registrar`, categoria);
  }

  actualizar(id: number, categoria: Categoria): Observable<ApiResponse<Categoria>> {
    return this.http.put<ApiResponse<Categoria>>(`${this.baseUrl}/actualizar/${id}`, categoria);
  }

  eliminar(id: number): Observable<ApiResponse<null>> {
    return this.http.delete<ApiResponse<null>>(`${this.baseUrl}/eliminar/${id}`);
  }
}
