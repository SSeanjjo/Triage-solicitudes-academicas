import { Component, inject } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
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
    MatButtonModule, MatIconModule,
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

  form = this.fb.group({
    nombre: ['', [Validators.required, Validators.minLength(3)]],
    correo: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  onSubmit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    const request = { ...this.form.value, rol: Rol.ESTUDIANTE };
    this.auth.registro(request as any).subscribe({
      next: () => {
        this.loading = false;
        this.snackBar.open('¡Registro exitoso! Ahora puedes iniciar sesión.', 'OK', { duration: 5000 });
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.loading = false;
        const errores = err.error?.errores;
        const msg = err.error?.mensaje
          || err.error?.message
          || (errores ? Object.values(errores).join(', ') : null)
          || 'Error al registrar el usuario. Inténtalo de nuevo.';
        this.snackBar.open(msg, 'Cerrar', { duration: 5000, panelClass: 'snack-error' });
      },
    });
  }
}
