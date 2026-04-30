import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { IASugerencia, IAResumen } from '../models/solicitud.model';

@Injectable({ providedIn: 'root' })
export class IaService {
  private http = inject(HttpClient);

  getSugerenciaClasificacion(descripcion: string): Observable<IASugerencia> {
    return this.http.post<IASugerencia>('/api/ia/sugerencias/clasificacion', { descripcion });
  }

  getResumen(solicitudId: number): Observable<IAResumen> {
    return this.http.post<IAResumen>('/api/ia/resumen', { solicitudId });
  }
}
