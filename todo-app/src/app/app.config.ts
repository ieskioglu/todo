import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';

import { registerLocaleData } from '@angular/common';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import tr from '@angular/common/locales/tr';
import { FormsModule } from '@angular/forms';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideNzI18n, tr_TR } from 'ng-zorro-antd/i18n';
import { routes } from './app.routes';
import { provideNzIcons } from './icons-provider';
import { apiInterceptor } from './interceptors/api.interceptor';
import { jwtInterceptor } from './interceptors/jwt.interceptor';

registerLocaleData(tr);

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideNzIcons(),
    provideNzI18n(tr_TR),
    importProvidersFrom(FormsModule),
    provideAnimationsAsync(),
    provideHttpClient(withInterceptors([jwtInterceptor, apiInterceptor])),
  ],
};
