import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';
import { Rol } from './core/models/user.model';

export const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login.component').then(m => m.LoginComponent),
  },
  {
    path: 'registro',
    loadComponent: () => import('./pages/registro/registro.component').then(m => m.RegistroComponent),
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./pages/dashboard/dashboard.component').then(m => m.DashboardComponent),
    canActivate: [authGuard],
  },
  {
    path: 'solicitudes',
    canActivate: [authGuard],
    children: [
      {
        path: '',
        loadComponent: () => import('./pages/solicitudes/lista/lista.component').then(m => m.ListaComponent),
      },
      {
        path: 'nueva',
        loadComponent: () => import('./pages/solicitudes/nueva/nueva.component').then(m => m.NuevaComponent),
        canActivate: [roleGuard([Rol.ESTUDIANTE, Rol.ADMINISTRADOR])],
      },
      {
        path: ':id',
        loadComponent: () => import('./pages/solicitudes/detalle/detalle.component').then(m => m.DetalleComponent),
      },
    ],
  },
  {
    path: 'usuarios',
    loadComponent: () => import('./pages/usuarios/usuarios.component').then(m => m.UsuariosComponent),
    canActivate: [roleGuard([Rol.ADMINISTRADOR])],
  },
  { path: '**', redirectTo: '/dashboard' },
];
