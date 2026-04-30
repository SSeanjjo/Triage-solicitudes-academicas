import { Component, inject } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../core/services/auth.service';
import { Rol } from '../../core/models/user.model';

@Component({
  selector: 'app-registro',
  standalone: true,
  imports: [
    RouterModule, ReactiveFormsModule,
    MatCardModule, MatFormFieldModule, MatInputModule,
    MatButtonModule, MatIconModule, MatSelectModule,
    MatProgressSpinnerModule, MatSnackBarModule,
  ],
  templateUrl: './registro.component.html',
  styleUrl: './registro.component.scss',
})
export class RegistroComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);

  loading = false;
  hidePassword = true;
  roles = [
    { value: Rol.ESTUDIANTE, label: 'Estudiante' },
    { value: Rol.RESPONSABLE, label: 'Responsable Académico' },
    { value: Rol.ADMINISTRADOR, label: 'Administrador' },
  ];

  form = this.fb.group({
    nombre: ['', [Validators.required, Validators.minLength(2)]],
    correo: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    rol: [Rol.ESTUDIANTE, Validators.required],
  });

  onSubmit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.auth.registro(this.form.value as any).subscribe({
      next: () => {
        this.loading = false;
        this.snackBar.open('¡Registro exitoso! Ahora puedes iniciar sesión.', 'OK', { duration: 5000 });
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.loading = false;
        const msg = err.error?.message || 'Error al registrar el usuario. Inténtalo de nuevo.';
        this.snackBar.open(msg, 'Cerrar', { duration: 5000, panelClass: 'snack-error' });
      },
    });
  }
}
