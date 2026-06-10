// src/app/core/services/estado-habitacion.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/ApiResponse';
import { EstadoHabitacion } from '../models/estado-habitacion.model';

@Injectable({ providedIn: 'root' })
export class EstadoHabitacionService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8081/api/estadohabitacion';

  listar(): Observable<ApiResponse<EstadoHabitacion[]>> {
    return this.http.get<ApiResponse<EstadoHabitacion[]>>(`${this.baseUrl}/listar`);
  }
}
