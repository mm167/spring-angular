import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { Observable } from 'rxjs';

import { Materiel, NouveauMateriel } from '../api';

const URL_MATERIELS = 'http://localhost:8080/api/materiels';

@Service()
export class MaterielService {
  private readonly http = inject(HttpClient);

  lister(): Observable<Materiel[]> {
    return this.http.get<Materiel[]>(URL_MATERIELS);
  }

  consulter(id: number): Observable<Materiel> {
    return this.http.get<Materiel>(`${URL_MATERIELS}/${id}`);
  }

  creer(nouveau: NouveauMateriel): Observable<Materiel> {
    return this.http.post<Materiel>(URL_MATERIELS, nouveau);
  }
}
