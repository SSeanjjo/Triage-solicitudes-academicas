import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Usuario, Rol } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private http = inject(HttpClient);

  getUsuarios(activo?: boolean, rol?: Rol): Observable<Usuario[]> {
    let params = new HttpParams();
    if (activo !== undefined) params = params.set('activo', String(activo));
    if (rol) params = params.set('rol', rol);
    return this.http.get<Usuario[]>('/api/usuarios', { params });
  }

  getUsuario(id: number): Observable<Usuario> {
    return this.http.get<Usuario>(`/api/usuarios/${id}`);
  }

  updateUsuario(id: number, data: Partial<Usuario>): Observable<Usuario> {
    return this.http.put<Usuario>(`/api/usuarios/${id}`, data);
  }
}
