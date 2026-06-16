import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { timeout, catchError, map } from 'rxjs/operators';
import { DashboardStatsDto } from '../models/DashboardStatsDto';
import { ReporteProductoDto, ReporteVentaDto, ReporteHabitacionDto, ReporteCobroDto } from '../models/reporte.models';

interface ApiResponse<T> { success: boolean; data: T; message?: string; }

@Injectable({ providedIn: 'root' })
export class ReporteService {
  private readonly API_URL = 'http://localhost:8081/api/reportes';
  private readonly TIMEOUT_MS = 10000;

  constructor(private http: HttpClient) { }

  private request<T>(endpoint: string, params: HttpParams = new HttpParams()): Observable<T> {
    return this.http.get<ApiResponse<T>>(`${this.API_URL}/${endpoint}`, { params }).pipe(
      timeout(this.TIMEOUT_MS),
      map(res => res.data),
      catchError(err => {
        console.error('Error:', err);
        return throwError(() => new Error('Error al conectar con el servidor.'));
      })
    );
  }

  getProductosBajoStock(limite: number) { return this.request<ReporteProductoDto[]>('productos-bajo-stock', new HttpParams().set('limite', limite.toString())); }
  getVentas(inicio: string, fin: string) { return this.request<ReporteVentaDto[]>('ventas', new HttpParams().set('inicio', inicio).set('fin', fin)); }
  getOcupacion(inicio: string, fin: string) { return this.request<ReporteHabitacionDto[]>('ocupacion', new HttpParams().set('inicio', inicio).set('fin', fin)); }
  getCobros(inicio: string, fin: string) { return this.request<ReporteCobroDto[]>('cobros', new HttpParams().set('inicio', inicio).set('fin', fin)); }
  getDashboardStats() { return this.request<DashboardStatsDto>('dashboard'); }
}
