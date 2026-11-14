import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { journalInterceptor } from './journal.interceptor';

describe('journalInterceptor', () => {
  let http: HttpClient;
  let controleur: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([journalInterceptor])),
        provideHttpClientTesting(),
      ],
    });
    http = TestBed.inject(HttpClient);
    controleur = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    controleur.verify();
    vi.restoreAllMocks();
  });

  it('journalise une réponse réussie', () => {
    const journal = vi.spyOn(console, 'log').mockImplementation(() => undefined);

    http.get('/essai').subscribe();
    controleur.expectOne('/essai').flush({});

    expect(journal).toHaveBeenCalledWith(expect.stringMatching(/^GET \/essai -> 200 \(\d+ ms\)$/));
  });

  it('journalise une erreur', () => {
    const journal = vi.spyOn(console, 'log').mockImplementation(() => undefined);

    http.get('/essai').subscribe({ error: () => undefined });
    controleur
      .expectOne('/essai')
      .flush('absent', { status: 404, statusText: 'Not Found' });

    expect(journal).toHaveBeenCalledWith(
      expect.stringMatching(/^GET \/essai -> ERREUR 404 \(\d+ ms\)$/),
    );
  });
});
