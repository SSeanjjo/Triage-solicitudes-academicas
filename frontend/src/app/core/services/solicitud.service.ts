import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  Solicitud,
  CreateSolicitudRequest,
  ClasificarRequest,
  AtenderRequest,
  CierreRequest,
  SolicitudFilter,
  HistorialEntry,
} from '../models/solicitud.model';

@Injectable({ providedIn: 'root' })
export class SolicitudService {
  private http = inject(HttpClient);

  getSolicitudes(filter?: SolicitudFilter): Observable<Solicitud[]> {
    let params = new HttpParams();
    if (filter?.estado) params = params.set('estado', filter.estado);
    if (filter?.tipo) params = params.set('tipo', filter.tipo);
    if (filter?.prioridad) params = params.set('prioridad', filter.prioridad);
    if (filter?.responsableId) params = params.set('responsableId', String(filter.responsableId));
    return this.http.get<Solicitud[]>('/api/solicitudes', { params });
  }

  getSolicitud(id: number): Observable<Solicitud> {
    return this.http.get<Solicitud>(`/api/solicitudes/${id}`);
  }

  createSolicitud(data: CreateSolicitudRequest): Observable<Solicitud> {
    return this.http.post<Solicitud>('/api/solicitudes', data);
  }

  clasificar(id: number, data: ClasificarRequest): Observable<Solicitud> {
    return this.http.patch<Solicitud>(`/api/solicitudes/${id}/clasificar`, data);
  }

  asignar(id: number, responsableId: number): Observable<Solicitud> {
    return this.http.patch<Solicitud>(`/api/solicitudes/${id}/asignar`, { responsableId });
  }

  atender(id: number, data: AtenderRequest): Observable<Solicitud> {
    return this.http.patch<Solicitud>(`/api/solicitudes/${id}/atender`, data);
  }

  cerrar(id: number, data: CierreRequest): Observable<Solicitud> {
    return this.http.patch<Solicitud>(`/api/solicitudes/${id}/cerrar`, data);
  }

  getHistorial(id: number): Observable<HistorialEntry[]> {
    return this.http.get<HistorialEntry[]>(`/api/solicitudes/${id}/historial`);
  }
}
