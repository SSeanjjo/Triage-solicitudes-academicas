import { Component, inject, signal } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDividerModule } from '@angular/material/divider';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { AuthService } from '../../../core/services/auth.service';
import { SolicitudService } from '../../../core/services/solicitud.service';
import { IaService } from '../../../core/services/ia.service';
import { TipoSolicitud, CanalOrigen, IASugerencia } from '../../../core/models/solicitud.model';

@Component({
  selector: 'app-nueva',
  standalone: true,
  imports: [
    RouterModule, ReactiveFormsModule, NavbarComponent,
    MatCardModule, MatFormFieldModule, MatInputModule, MatSelectModule,
    MatButtonModule, MatIconModule, MatProgressSpinnerModule, MatSnackBarModule, MatDividerModule,
  ],
  templateUrl: './nueva.component.html',
  styleUrl: './nueva.component.scss',
})
export class NuevaComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private solicitudService = inject(SolicitudService);
  private iaService = inject(IaService);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);

  tipos = Object.values(TipoSolicitud);
  canales = Object.values(CanalOrigen);
  TipoSolicitud = TipoSolicitud;
  CanalOrigen = CanalOrigen;

  submitting = signal(false);
  aiLoading = signal(false);
  aiSugerencia = signal<IASugerencia | null>(null);

  form = this.fb.group({
    titulo: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(150)]],
    descripcion: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(1000)]],
    tipo: ['', Validators.required],
    canal: ['', Validators.required],
  });

  getSugerenciaIA(): void {
    const desc = this.form.get('descripcion')?.value;
    if (!desc || desc.trim().length < 10) {
      this.snackBar.open('Escribe al menos 10 caracteres en la descripción para obtener sugerencias.', 'OK', { duration: 4000 });
      return;
    }
    this.aiLoading.set(true);
    this.iaService.getSugerenciaClasificacion(desc).subscribe({
      next: (sugerencia) => {
        this.aiLoading.set(false);
        this.aiSugerencia.set(sugerencia);
        const tipoSugerido = sugerencia.tipoSugerido ?? sugerencia.tipo;
        if (tipoSugerido) {
          this.form.patchValue({ tipo: tipoSugerido });
          this.snackBar.open('Sugerencia de IA aplicada al tipo de solicitud.', 'OK', { duration: 3000 });
        }
      },
      error: (err) => {
        this.aiLoading.set(false);
        const msg = err.error?.message || 'No se pudo obtener la sugerencia de IA en este momento.';
        this.snackBar.open(msg, 'Cerrar', { duration: 5000 });
      },
    });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.submitting.set(true);
    const { titulo, descripcion, tipo, canal } = this.form.value;
    this.solicitudService.createSolicitud(
      { titulo: titulo!, descripcion: descripcion!, tipoSolicitud: tipo as TipoSolicitud, canalOrigen: canal as CanalOrigen }
    ).subscribe({
      next: (s) => {
        this.submitting.set(false);
        this.snackBar.open('Solicitud creada exitosamente.', 'OK', { duration: 3000 });
        this.router.navigate(['/solicitudes', s.id]);
      },
      error: (err) => {
        this.submitting.set(false);
        const msg = err.error?.message || 'Error al crear la solicitud.';
        this.snackBar.open(msg, 'Cerrar', { duration: 5000, panelClass: 'snack-error' });
      },
    });
  }

  formatTipo(t: TipoSolicitud): string {
    return t.replace(/_/g, ' ');
  }

  formatCanal(c: CanalOrigen): string {
    return { CSU: 'CSU', CORREO: 'Correo', SAC: 'SAC', TELEFONICO: 'Telefónico' }[c] ?? c;
  }
}
