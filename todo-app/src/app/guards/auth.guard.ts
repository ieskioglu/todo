import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const isLoggedIn = inject(AuthService).isLoggedIn();
  if (isLoggedIn && localStorage.getItem('auth_token')) {
    return true;
  }
  router.navigateByUrl('/sign-in');
  return false;
};
