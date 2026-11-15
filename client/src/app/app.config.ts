import {ApplicationConfig, LOCALE_ID, provideBrowserGlobalErrorListeners} from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { journalInterceptor } from './config/journal.interceptor';
import { authInterceptor } from './securite/auth.interceptor';

registerLocaleData(localeFr);

export const appConfig: ApplicationConfig = {
  providers: [
    { provide: LOCALE_ID, useValue: 'fr' },
    provideBrowserGlobalErrorListeners(),
    provideHttpClient(withInterceptors([authInterceptor, journalInterceptor])),
    provideRouter(routes)
  ]
};
