import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { AuthService } from '../../core/services/auth.service';
import { SolicitudService } from '../../core/services/solicitud.service';
import { Solicitud, EstadoSolicitud, PrioridadSolicitud } from '../../core/models/solicitud.model';
import { Rol } from '../../core/models/user.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    RouterModule, NavbarComponent,
    MatCardModule, MatButtonModule, MatIconModule,
    MatProgressSpinnerModule, MatDividerModule,
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent implements OnInit {
  auth = inject(AuthService);
  private solicitudService = inject(SolicitudService);

  Rol = Rol;
  EstadoSolicitud = EstadoSolicitud;
  PrioridadSolicitud = PrioridadSolicitud;

  solicitudes = signal<Solicitud[]>([]);
  loading = signal(true);

  stats = computed(() => {
    const s = this.solicitudes();
    return {
      total: s.length,
      registrada: s.filter(x => x.estado === EstadoSolicitud.REGISTRADA).length,
      clasificada: s.filter(x => x.estado === EstadoSolicitud.CLASIFICADA).length,
      enAtencion: s.filter(x => x.estado === EstadoSolicitud.EN_ATENCION).length,
      atendida: s.filter(x => x.estado === EstadoSolicitud.ATENDIDA).length,
      cerrada: s.filter(x => x.estado === EstadoSolicitud.CERRADA).length,
      alta: s.filter(x => x.prioridad === PrioridadSolicitud.ALTA).length,
      media: s.filter(x => x.prioridad === PrioridadSolicitud.MEDIA).length,
      baja: s.filter(x => x.prioridad === PrioridadSolicitud.BAJA).length,
    };
  });

  ngOnInit(): void {
    this.solicitudService.getSolicitudes().subscribe({
      next: (data) => { this.solicitudes.set(data); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }
}
