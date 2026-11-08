
import { registerLocaleData } from '@angular/common';
import localePt from '@angular/common/locales/pt';

import { provideHttpClient } from '@angular/common/http';

import {
  ApplicationConfig,
  LOCALE_ID,
  provideBrowserGlobalErrorListeners
} from '@angular/core';

// Registra a moeda brasileira.
registerLocaleData(localePt);

/**
 * Configuracoes do Angular.
 */
export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideHttpClient(),
    {
      provide: LOCALE_ID,
      useValue: 'pt-BR'
    }
  ]
};
