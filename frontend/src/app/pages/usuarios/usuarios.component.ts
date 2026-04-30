import { Component, inject, OnInit, ViewChild, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDividerModule } from '@angular/material/divider';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { UsuarioService } from '../../core/services/usuario.service';
import { Usuario, Rol } from '../../core/models/user.model';

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [
    ReactiveFormsModule, NavbarComponent,
    MatTableModule, MatPaginatorModule,
    MatCardModule, MatFormFieldModule, MatInputModule, MatSelectModule,
    MatButtonModule, MatIconModule, MatChipsModule,
    MatProgressSpinnerModule, MatSnackBarModule, MatTooltipModule,
    MatDividerModule, MatSlideToggleModule,
  ],
  templateUrl: './usuarios.component.html',
  styleUrl: './usuarios.component.scss',
})
export class UsuariosComponent implements OnInit {
  private usuarioService = inject(UsuarioService);
  private snackBar = inject(MatSnackBar);
  private fb = inject(FormBuilder);

  Rol = Rol;

  loading = signal(true);
  editingId = signal<number | null>(null);
  saveLoading = signal(false);

  dataSource = new MatTableDataSource<Usuario>([]);
  displayedColumns = ['id', 'nombre', 'correo', 'rol', 'activo', 'acciones'];

  roles = Object.values(Rol);

  filterForm = this.fb.group({
    rol: [''],
    activo: [''],
  });

  editForm = this.fb.group({
    nombre: ['', [Validators.required, Validators.minLength(2)]],
    correo: ['', [Validators.required, Validators.email]],
    rol: ['', Validators.required],
    activo: [true],
  });

  @ViewChild(MatPaginator) set paginator(p: MatPaginator) { if (p) this.dataSource.paginator = p; }

  ngOnInit(): void {
    this.loadUsuarios();
  }

  loadUsuarios(): void {
    this.loading.set(true);
    const f = this.filterForm.value;
    const activo = f.activo !== '' && f.activo !== null ? f.activo === 'true' : undefined;
    this.usuarioService.getUsuarios(activo, f.rol as Rol || undefined).subscribe({
      next: (data) => { this.dataSource.data = data; this.loading.set(false); },
      error: () => { this.loading.set(false); this.snackBar.open('Error al cargar usuarios.', 'Cerrar', { duration: 4000 }); },
    });
  }

  startEdit(usuario: Usuario): void {
    this.editingId.set(usuario.id);
    this.editForm.patchValue({
      nombre: usuario.nombre,
      correo: usuario.correo,
      rol: usuario.rol,
      activo: usuario.activo,
    });
  }

  cancelEdit(): void {
    this.editingId.set(null);
    this.editForm.reset();
  }

  saveEdit(id: number): void {
    if (this.editForm.invalid) { this.editForm.markAllAsTouched(); return; }
    this.saveLoading.set(true);
    this.usuarioService.updateUsuario(id, this.editForm.value as Partial<Usuario>).subscribe({
      next: (updated) => {
        const data = [...this.dataSource.data];
        const idx = data.findIndex(u => u.id === id);
        if (idx !== -1) data[idx] = updated;
        this.dataSource.data = data;
        this.saveLoading.set(false);
        this.editingId.set(null);
        this.snackBar.open('Usuario actualizado correctamente.', 'OK', { duration: 3000 });
      },
      error: (err) => {
        this.saveLoading.set(false);
        this.snackBar.open(err.error?.message || 'Error al actualizar usuario.', 'Cerrar', { duration: 4000 });
      },
    });
  }

  getRolClass(rol: Rol): string {
    return { ESTUDIANTE: 'chip-estudiante', RESPONSABLE: 'chip-responsable', ADMINISTRADOR: 'chip-admin' }[rol] ?? '';
  }

  getRolLabel(rol: Rol): string {
    return { ESTUDIANTE: 'Estudiante', RESPONSABLE: 'Responsable', ADMINISTRADOR: 'Administrador' }[rol] ?? rol;
  }
}
