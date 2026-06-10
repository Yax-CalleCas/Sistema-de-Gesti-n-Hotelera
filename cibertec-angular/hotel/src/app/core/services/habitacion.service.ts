// src/app/core/services/habitacion.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/ApiResponse';
import { Habitacion } from '../models/Habitacion';

@Injectable({ providedIn: 'root' })
export class HabitacionService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8081/api/habitacion';

  listar(): Observable<ApiResponse<Habitacion[]>> {
    return this.http.get<ApiResponse<Habitacion[]>>(`${this.baseUrl}/listar`);
  }

  buscarPorId(id: number): Observable<ApiResponse<Habitacion>> {
    return this.http.get<ApiResponse<Habitacion>>(`${this.baseUrl}/buscar/${id}`);
  }

  guardar(dto: Habitacion): Observable<ApiResponse<Habitacion>> {
    return dto.idHabitacion
      ? this.http.put<ApiResponse<Habitacion>>(`${this.baseUrl}/actualizar/${dto.idHabitacion}`, dto)
      : this.http.post<ApiResponse<Habitacion>>(`${this.baseUrl}/registrar`, dto);
  }

  eliminar(id: number): Observable<ApiResponse<null>> {
    return this.http.delete<ApiResponse<null>>(`${this.baseUrl}/eliminar/${id}`);
  }
}
