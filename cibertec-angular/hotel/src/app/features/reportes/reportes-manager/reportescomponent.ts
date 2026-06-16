import { Component, OnInit, OnDestroy, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subscription, finalize } from 'rxjs';
import { ReporteService } from '../../../core/services/ReporteService';

@Component({
  selector: 'app-reportes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reportes.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ReportesComponent implements OnInit, OnDestroy {
  fechaInicio = new Date(new Date().setHours(0, 0, 0, 0)).toISOString().slice(0, 16);
  fechaFin = new Date().toISOString().slice(0, 16);
  tipoReporte: 'ventas' | 'ocupacion' | 'cobros' = 'ventas';
  resultados: any[] = [];
  isLoading = false;
  errorMessage: string | null = null;
  totalGeneral = 0;
  private sub?: Subscription;

  constructor(private service: ReporteService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void { this.generarReporte(); }
  ngOnDestroy(): void { this.sub?.unsubscribe(); }

  generarReporte(): void {
    if (this.isLoading) return;
    this.isLoading = true;
    this.errorMessage = null;

    // Forzamos el tipado del observable como any[] para resolver el conflicto de unión
    const request$ = this.getServiceCall();

    this.sub?.unsubscribe();
    this.sub = request$.pipe(finalize(() => {
      this.isLoading = false;
      this.cdr.markForCheck();
    }))
    .subscribe({
      next: (data: any[]) => {
        this.resultados = data || [];
        this.calcularTotal();
      },
      error: (err: any) => {
        this.errorMessage = err.message;
        this.resultados = [];
      }
    });
  }

  private getServiceCall(): any {
    const p = [this.fechaInicio, this.fechaFin] as const;
    const calls: Record<string, any> = {
      ventas: this.service.getVentas(...p),
      ocupacion: this.service.getOcupacion(...p),
      cobros: this.service.getCobros(...p)
    };
    return calls[this.tipoReporte];
  }

  private calcularTotal(): void {
    const keys: Record<string, string> = { ventas: 'totalIngresado', cobros: 'totalPagado' };
    const key = keys[this.tipoReporte];
    this.totalGeneral = key ? this.resultados.reduce((acc, curr) => acc + Number(curr[key] || 0), 0) : 0;
  }
}
