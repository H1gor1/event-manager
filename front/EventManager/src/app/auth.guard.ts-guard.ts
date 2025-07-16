import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const AuthGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);

  // Verificar se é uma rota pública
  const isPublicRoute = state.url.includes('/public/');

  // Se for uma rota pública, permite o acesso sem verificar autenticação
  if (isPublicRoute) {
    return true;
  }

  // Verifica se o usuário está autenticado
  const token = localStorage.getItem('auth_token');
  if (token) {
    return true;
  }

  // Se não estiver autenticado e não for rota pública, redireciona para login
  router.navigate(['/login']);
  return false;
};
