import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/ApiResponse';
import { Recepcion } from '../models/recepcion.model';

@Injectable({ providedIn: 'root' })
export class RecepcionService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8081/api/recepcion';

  listar(): Observable<ApiResponse<Recepcion[]>> {
    return this.http.get<ApiResponse<Recepcion[]>>(`${this.baseUrl}/listar`);
  }

/*
  buscar(id: number): Observable<ApiResponse<Recepcion>> {
    return this.http.get<ApiResponse<Recepcion>>(`${this.baseUrl}/buscar/${id}`);
  }
*/
// Cambiado de 'buscar' a 'buscarPorId' para unificar con tus otros servicios
  buscarPorId(id: number): Observable<ApiResponse<Recepcion>> {
    return this.http.get<ApiResponse<Recepcion>>(`${this.baseUrl}/buscar/${id}`);
  }
  buscarActivaPorHabitacion(idHabitacion: number): Observable<ApiResponse<any>> {
    return this.http.get<ApiResponse<any>>(`${this.baseUrl}/habitacion-activa/${idHabitacion}`);
  }


  registrar(dto: any): Observable<ApiResponse<Recepcion>> {
    return this.http.post<ApiResponse<Recepcion>>(`${this.baseUrl}/registrar`, dto);
  }

  // MÉTODO NUEVO: Para el Check-Out / Salida de Habitación
  registrarSalida(data: {
    idRecepcion: number,
    idHabitacion: number,
    costoPenalidad: number,
    totalPagado: number
  }): Observable<ApiResponse<boolean>> {
    return this.http.post<ApiResponse<boolean>>(`${this.baseUrl}/registrar-salida`, data);
  }

  actualizar(id: number, dto: Recepcion): Observable<ApiResponse<Recepcion>> {
    return this.http.put<ApiResponse<Recepcion>>(`${this.baseUrl}/actualizar/${id}`, dto);
  }

  eliminar(id: number): Observable<ApiResponse<null>> {
    return this.http.delete<ApiResponse<null>>(`${this.baseUrl}/eliminar/${id}`);
  }
}
