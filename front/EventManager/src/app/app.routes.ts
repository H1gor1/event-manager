import { Routes } from '@angular/router';
import {Home} from './pages/home/home';
import {EventComponent} from './pages/event-component/event-component';
import {LoginComponent} from './pages/login-component/login-component';
import {AuthGuard} from './auth.guard.ts-guard';
import {RegisterComponent} from './pages/register-component/register-component';
import {TableComponent} from './pages/table-component/table-component';
import {ConfirmationComponent} from './pages/confirmation-component/confirmation-component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  // Colocando a rota pública antes das rotas protegidas
  {
    path: 'events/public/confirm-guest',
    component: ConfirmationComponent
    // Note: sem canActivate: [AuthGuard]
  },
  { path: 'event/:id', component: EventComponent, canActivate: [AuthGuard] },
  { path: 'table/:id', component: TableComponent, canActivate: [AuthGuard] },
  { path: 'event/:eventId/table/:id', component: TableComponent, canActivate: [AuthGuard] },
  {
    path: 'home',
    loadComponent: () => import('./pages/home/home').then(m => m.Home),
    canActivate: [AuthGuard]
  },
  { path: 'event', component: EventComponent, canActivate: [AuthGuard] },
  { path: '**', redirectTo: 'home' }
];
