import { Component, inject, OnInit, ViewChild, signal } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SlicePipe } from '@angular/common';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatSortModule, MatSort } from '@angular/material/sort';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { AuthService } from '../../../core/services/auth.service';
import { SolicitudService } from '../../../core/services/solicitud.service';
import { Solicitud, EstadoSolicitud, TipoSolicitud, PrioridadSolicitud } from '../../../core/models/solicitud.model';
import { Rol } from '../../../core/models/user.model';

@Component({
  selector: 'app-lista',
  standalone: true,
  imports: [
    RouterModule, ReactiveFormsModule, NavbarComponent,
    SlicePipe,
    MatTableModule, MatPaginatorModule, MatSortModule,
    MatCardModule, MatFormFieldModule, MatInputModule, MatSelectModule,
    MatButtonModule, MatIconModule, MatChipsModule,
    MatProgressSpinnerModule, MatTooltipModule,
  ],
  templateUrl: './lista.component.html',
  styleUrl: './lista.component.scss',
})
export class ListaComponent implements OnInit {
  auth = inject(AuthService);
  private solicitudService = inject(SolicitudService);
  private fb = inject(FormBuilder);

  Rol = Rol;
  EstadoSolicitud = EstadoSolicitud;
  TipoSolicitud = TipoSolicitud;
  PrioridadSolicitud = PrioridadSolicitud;

  loading = signal(true);
  dataSource = new MatTableDataSource<Solicitud>([]);
  displayedColumns = ['id', 'titulo', 'tipo', 'estado', 'prioridad', 'solicitante', 'fechaCreacion', 'acciones'];

  estados = Object.values(EstadoSolicitud);
  tipos = Object.values(TipoSolicitud);
  prioridades = Object.values(PrioridadSolicitud);

  filterForm = this.fb.group({
    estado: [''],
    tipoSolicitud: [''],
    prioridad: [''],
  });

  @ViewChild(MatPaginator) set paginator(p: MatPaginator) { if (p) this.dataSource.paginator = p; }
  @ViewChild(MatSort) set sort(s: MatSort) { if (s) this.dataSource.sort = s; }

  ngOnInit(): void {
    this.loadSolicitudes();
  }

  loadSolicitudes(): void {
    this.loading.set(true);
    const f = this.filterForm.value;
    this.solicitudService.getSolicitudes({
      estado: f.estado as any || undefined,
      tipo: f.tipoSolicitud as any || undefined,
      prioridad: f.prioridad as any || undefined,
    }).subscribe({
      next: (data) => { this.dataSource.data = data; this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  applyFilter(): void {
    this.loadSolicitudes();
  }

  clearFilters(): void {
    this.filterForm.reset();
    this.loadSolicitudes();
  }

  getEstadoClass(estado: EstadoSolicitud): string {
    const map: Record<EstadoSolicitud, string> = {
      REGISTRADA: 'chip-registrada',
      CLASIFICADA: 'chip-clasificada',
      EN_ATENCION: 'chip-en-atencion',
      ATENDIDA: 'chip-atendida',
      CERRADA: 'chip-cerrada',
    };
    return map[estado] ?? '';
  }

  getPrioridadClass(p?: PrioridadSolicitud): string {
    if (!p) return 'chip-sin-prioridad';
    return { ALTA: 'chip-alta', MEDIA: 'chip-media', BAJA: 'chip-baja' }[p] ?? '';
  }

  formatTipo(tipo: TipoSolicitud): string {
    return tipo.replace(/_/g, ' ');
  }
}
