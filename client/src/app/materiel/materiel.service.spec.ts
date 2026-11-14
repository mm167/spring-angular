import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { MaterielService } from './materiel.service';

const URL_MATERIELS = 'http://localhost:8080/api/materiels';

describe('MaterielService', () => {
  let service: MaterielService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(MaterielService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    http.verify();
  });

  it('lister envoie un GET et retourne les matériels', () => {
    let recus: unknown[] = [];
    service.lister().subscribe((materiels) => (recus = materiels));

    const requete = http.expectOne(URL_MATERIELS);
    expect(requete.request.method).toBe('GET');
    requete.flush([
      { id: 1, nom: 'Mini-pelle 1,5 t', categorie: 'MINI_PELLE', tarifJournalier: 180 },
    ]);

    expect(recus).toHaveLength(1);
  });

  it('creer envoie un POST avec le matériel dans le corps', () => {
    const nouveau = { nom: 'Nacelle 16 m', categorie: 'NACELLE' as const, tarifJournalier: 260 };
    service.creer(nouveau).subscribe();

    const requete = http.expectOne(URL_MATERIELS);
    expect(requete.request.method).toBe('POST');
    expect(requete.request.body).toEqual(nouveau);
    requete.flush({ id: 4, ...nouveau });
  });

  it('consulter transmet un 404 sur le canal d’erreur', () => {
    let statut = 0;
    service.consulter(99).subscribe({ error: (erreur) => (statut = erreur.status) });

    http.expectOne(`${URL_MATERIELS}/99`).flush(
      { status: 404, message: 'Matériel introuvable.' },
      { status: 404, statusText: 'Not Found' },
    );

    expect(statut).toBe(404);
  });
});
