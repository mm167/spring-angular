import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';

import { Jeton } from '../api';
import { authInterceptor } from './auth.interceptor';
import { SessionService } from './session.service';

const jeton: Jeton = { jetonAcces: 'a.b.c', expireDans: 900, roles: ['CLIENT'] };
const URL_RESERVATIONS = 'http://localhost:8080/api/reservations';
const URL_LOGIN = 'http://localhost:8080/api/auth/login';
const URL_EXTERNE = 'http://exemple.test/ressource';

describe('authInterceptor', () => {
  let http: HttpClient;
  let controleur: HttpTestingController;
  let session: SessionService;

  beforeEach(() => {
    sessionStorage.clear();
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
        provideRouter([]),
      ],
    });
    http = TestBed.inject(HttpClient);
    controleur = TestBed.inject(HttpTestingController);
    session = TestBed.inject(SessionService);
  });

  afterEach(() => {
    controleur.verify();
    sessionStorage.clear();
  });

  it('ajoute le jeton aux requêtes vers l’API', () => {
    session.ouvrir('durand', jeton);

    http.get(URL_RESERVATIONS).subscribe();

    const requete = controleur.expectOne(URL_RESERVATIONS);
    expect(requete.request.headers.get('Authorization')).toBe('Bearer a.b.c');
    requete.flush([]);
  });

  it('n’ajoute pas le jeton à la connexion ni aux autres adresses', () => {
    session.ouvrir('durand', jeton);

    http.post(URL_LOGIN, {}).subscribe();
    http.get(URL_EXTERNE).subscribe();

    const login = controleur.expectOne(URL_LOGIN);
    const externe = controleur.expectOne(URL_EXTERNE);
    expect(login.request.headers.has('Authorization')).toBe(false);
    expect(externe.request.headers.has('Authorization')).toBe(false);
    login.flush({});
    externe.flush({});
  });

  it('ferme la session et redirige sur un 401', () => {
    session.ouvrir('durand', jeton);
    const navigation = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);

    http.get(URL_RESERVATIONS).subscribe({ error: () => undefined });
    controleur
      .expectOne(URL_RESERVATIONS)
      .flush({ status: 401, message: 'Jeton expiré.' }, { status: 401, statusText: 'Unauthorized' });

    expect(session.connecte()).toBe(false);
    expect(navigation).toHaveBeenCalledWith(['/connexion'], { queryParams: { retour: '/' } });
  });
});
