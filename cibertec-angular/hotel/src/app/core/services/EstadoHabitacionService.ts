import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, shareReplay } from 'rxjs';
import { ApiResponse } from '../models/ApiResponse';
import { EstadoHabitacion } from '../models/estado-habitacion.model';

@Injectable({ providedIn: 'root' })
export class EstadoHabitacionService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8081/api/estadohabitacion';
  private listarCache$?: Observable<ApiResponse<EstadoHabitacion[]>>;

  listar(): Observable<ApiResponse<EstadoHabitacion[]>> {
    if (!this.listarCache$) {
      this.listarCache$ = this.http.get<ApiResponse<EstadoHabitacion[]>>(`${this.baseUrl}/listar`).pipe(
        shareReplay(1)
      );
    }
    return this.listarCache$;
  }
}
