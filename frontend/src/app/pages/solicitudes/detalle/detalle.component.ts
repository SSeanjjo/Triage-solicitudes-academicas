import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatListModule } from '@angular/material/list';
import { DatePipe } from '@angular/common';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { AuthService } from '../../../core/services/auth.service';
import { SolicitudService } from '../../../core/services/solicitud.service';
import { UsuarioService } from '../../../core/services/usuario.service';
import { IaService } from '../../../core/services/ia.service';
import {
  Solicitud, HistorialEntry, EstadoSolicitud,
  TipoSolicitud, PrioridadSolicitud, CanalOrigen,
  AtenderRequest, CierreRequest,
} from '../../../core/models/solicitud.model';
import { Rol, Usuario } from '../../../core/models/user.model';

@Component({
  selector: 'app-detalle',
  standalone: true,
  imports: [
    RouterModule, ReactiveFormsModule, DatePipe, NavbarComponent,
    MatCardModule, MatButtonModule, MatIconModule, MatFormFieldModule, MatSelectModule,
    MatDividerModule, MatProgressSpinnerModule, MatExpansionModule,
    MatSnackBarModule, MatTooltipModule, MatListModule,
  ],
  templateUrl: './detalle.component.html',
  styleUrl: './detalle.component.scss',
})
export class DetalleComponent implements OnInit {
  auth = inject(AuthService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private solicitudService = inject(SolicitudService);
  private usuarioService = inject(UsuarioService);
  private iaService = inject(IaService);
  private snackBar = inject(MatSnackBar);
  private fb = inject(FormBuilder);

  Rol = Rol;
  EstadoSolicitud = EstadoSolicitud;
  TipoSolicitud = TipoSolicitud;
  PrioridadSolicitud = PrioridadSolicitud;
  CanalOrigen = CanalOrigen;

  loading = signal(true);
  actionLoading = signal(false);
  historialLoading = signal(false);
  aiLoading = signal(false);

  solicitud = signal<Solicitud | null>(null);
  historial = signal<HistorialEntry[]>([]);
  responsables = signal<Usuario[]>([]);
  aiResumen = signal<string | null>(null);

  showClasificarForm = false;
  showAsignarForm = false;

  tipos = Object.values(TipoSolicitud);
  prioridades = Object.values(PrioridadSolicitud);

  clasificarForm = this.fb.group({
    tipoSolicitud: ['', Validators.required],
    prioridad: ['', Validators.required],
    justificacionPrioridad: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(500)]],
  });

  atenderForm = this.fb.group({
    comentario: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(500)]],
  });

  cerrarForm = this.fb.group({
    comentarioCierre: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(500)]],
  });

  showAtenderForm = false;
  showCerrarForm = false;

  asignarForm = this.fb.group({
    responsableId: [null as number | null, Validators.required],
  });

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.loadSolicitud(id);
  }

  loadSolicitud(id: number): void {
    this.loading.set(true);
    this.solicitudService.getSolicitud(id).subscribe({
      next: (s) => { this.solicitud.set(s); this.loading.set(false); this.loadHistorial(id); },
      error: () => { this.loading.set(false); this.snackBar.open('Error al cargar la solicitud.', 'Cerrar', { duration: 4000 }); },
    });
  }

  loadHistorial(id: number): void {
    this.historialLoading.set(true);
    this.solicitudService.getHistorial(id).subscribe({
      next: (h) => { this.historial.set(h); this.historialLoading.set(false); },
      error: () => this.historialLoading.set(false),
    });
  }

  loadResponsables(): void {
    this.usuarioService.getUsuarios(true, Rol.RESPONSABLE).subscribe({
      next: (u) => this.responsables.set(u),
      error: () => this.snackBar.open('Error al cargar responsables.', 'Cerrar', { duration: 3000 }),
    });
  }

  openClasificarForm(): void {
    const s = this.solicitud();
    if (s) {
      this.clasificarForm.patchValue({ tipoSolicitud: s.tipoSolicitud ?? '', prioridad: s.prioridad ?? '' });
    }
    this.showClasificarForm = true;
    this.showAsignarForm = false;
    this.showAtenderForm = false;
    this.showCerrarForm = false;
  }

  openAsignarForm(): void {
    this.showAsignarForm = true;
    this.showClasificarForm = false;
    this.showAtenderForm = false;
    this.showCerrarForm = false;
    if (this.auth.hasRole(Rol.ADMINISTRADOR)) {
      this.loadResponsables();
    } else {
      this.asignarForm.patchValue({ responsableId: this.auth.getUserId() });
    }
  }

  openAtenderForm(): void {
    this.showAtenderForm = true;
    this.showClasificarForm = false;
    this.showAsignarForm = false;
    this.showCerrarForm = false;
  }

  openCerrarForm(): void {
    this.showCerrarForm = true;
    this.showClasificarForm = false;
    this.showAsignarForm = false;
    this.showAtenderForm = false;
  }

  cancelForms(): void {
    this.showClasificarForm = false;
    this.showAsignarForm = false;
    this.showAtenderForm = false;
    this.showCerrarForm = false;
  }

  onClasificar(): void {
    if (this.clasificarForm.invalid) { this.clasificarForm.markAllAsTouched(); return; }
    const s = this.solicitud();
    if (!s) return;
    this.actionLoading.set(true);
    const { tipoSolicitud, prioridad, justificacionPrioridad } = this.clasificarForm.value;
    this.solicitudService.clasificar(s.id, {
      tipoSolicitud: tipoSolicitud as TipoSolicitud,
      prioridad: prioridad as PrioridadSolicitud,
      justificacionPrioridad: justificacionPrioridad!,
    }).subscribe({
        next: (updated) => {
          this.solicitud.set(updated);
          this.actionLoading.set(false);
          this.showClasificarForm = false;
          this.loadHistorial(s.id);
          this.snackBar.open('Solicitud clasificada correctamente.', 'OK', { duration: 3000 });
        },
        error: (err) => {
          this.actionLoading.set(false);
          this.snackBar.open(err.error?.message || 'Error al clasificar.', 'Cerrar', { duration: 4000 });
        },
      });
  }

  onAsignar(): void {
    if (this.asignarForm.invalid) { this.asignarForm.markAllAsTouched(); return; }
    const s = this.solicitud();
    if (!s) return;
    this.actionLoading.set(true);
    const responsableId = this.asignarForm.value.responsableId!;
    this.solicitudService.asignar(s.id, responsableId)
      .subscribe({
        next: (updated) => {
          this.solicitud.set(updated);
          this.actionLoading.set(false);
          this.showAsignarForm = false;
          this.loadHistorial(s.id);
          this.snackBar.open('Responsable asignado correctamente.', 'OK', { duration: 3000 });
        },
        error: (err) => {
          this.actionLoading.set(false);
          this.snackBar.open(err.error?.message || 'Error al asignar.', 'Cerrar', { duration: 4000 });
        },
      });
  }

  onAtender(): void {
    if (this.atenderForm.invalid) { this.atenderForm.markAllAsTouched(); return; }
    const s = this.solicitud();
    if (!s) return;
    this.actionLoading.set(true);
    this.solicitudService.atender(s.id, { comentario: this.atenderForm.value.comentario! }).subscribe({
      next: (updated) => {
        this.solicitud.set(updated);
        this.actionLoading.set(false);
        this.showAtenderForm = false;
        this.loadHistorial(s.id);
        this.snackBar.open('Solicitud marcada como atendida.', 'OK', { duration: 3000 });
      },
      error: (err) => {
        this.actionLoading.set(false);
        this.snackBar.open(err.error?.message || 'Error al marcar como atendida.', 'Cerrar', { duration: 4000 });
      },
    });
  }

  onCerrar(): void {
    if (this.cerrarForm.invalid) { this.cerrarForm.markAllAsTouched(); return; }
    const s = this.solicitud();
    if (!s) return;
    this.actionLoading.set(true);
    this.solicitudService.cerrar(s.id, { comentarioCierre: this.cerrarForm.value.comentarioCierre! }).subscribe({
      next: (updated) => {
        this.solicitud.set(updated);
        this.actionLoading.set(false);
        this.showCerrarForm = false;
        this.loadHistorial(s.id);
        this.snackBar.open('Solicitud cerrada correctamente.', 'OK', { duration: 3000 });
      },
      error: (err) => {
        this.actionLoading.set(false);
        this.snackBar.open(err.error?.message || 'Error al cerrar solicitud.', 'Cerrar', { duration: 4000 });
      },
    });
  }

  generarResumenIA(): void {
    const s = this.solicitud();
    if (!s) return;
    this.aiLoading.set(true);
    this.iaService.getResumen(s.id).subscribe({
      next: (r) => {
        this.aiLoading.set(false);
        this.aiResumen.set(r.resumen ?? r.summary ?? r.texto ?? 'Resumen generado.');
      },
      error: (err) => {
        this.aiLoading.set(false);
        this.snackBar.open(err.error?.message || 'Error al generar resumen con IA.', 'Cerrar', { duration: 4000 });
      },
    });
  }

  canClasificar(): boolean {
    return this.solicitud()?.estado === EstadoSolicitud.REGISTRADA &&
      this.auth.hasRole(Rol.ADMINISTRADOR, Rol.RESPONSABLE);
  }

  canAsignar(): boolean {
    return this.solicitud()?.estado === EstadoSolicitud.CLASIFICADA &&
      this.auth.hasRole(Rol.ADMINISTRADOR, Rol.RESPONSABLE);
  }

  canAtender(): boolean {
    return this.solicitud()?.estado === EstadoSolicitud.EN_ATENCION &&
      this.auth.hasRole(Rol.ADMINISTRADOR, Rol.RESPONSABLE);
  }

  canCerrar(): boolean {
    return this.solicitud()?.estado === EstadoSolicitud.ATENDIDA &&
      this.auth.hasRole(Rol.ADMINISTRADOR, Rol.RESPONSABLE);
  }

  getEstadoClass(estado?: EstadoSolicitud): string {
    if (!estado) return '';
    const map: Record<EstadoSolicitud, string> = {
      REGISTRADA: 'chip-registrada', CLASIFICADA: 'chip-clasificada',
      EN_ATENCION: 'chip-en-atencion', ATENDIDA: 'chip-atendida', CERRADA: 'chip-cerrada',
    };
    return map[estado] ?? '';
  }

  getPrioridadClass(p?: PrioridadSolicitud): string {
    if (!p) return 'chip-sin-prioridad';
    return { ALTA: 'chip-alta', MEDIA: 'chip-media', BAJA: 'chip-baja' }[p] ?? '';
  }

  formatTipo(t?: TipoSolicitud): string {
    return t?.replace(/_/g, ' ') ?? '—';
  }

  formatCanal(c?: CanalOrigen): string {
    if (!c) return '—';
    return { CSU: 'CSU', CORREO: 'Correo', SAC: 'SAC', TELEFONICO: 'Telefónico' }[c] ?? c;
  }
}
