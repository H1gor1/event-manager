import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import {AuthServiceTs} from './services/auth.service.ts';

export const AuthGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthServiceTs);
  const router = inject(Router);

  if (authService.isAuthenticated()) {
    return true;
  }
  router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
  return false;

};
