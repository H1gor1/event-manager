import { Routes } from '@angular/router';
import {Home} from './pages/home/home';
import {EventComponent} from './pages/event-component/event-component';
import {LoginComponent} from './pages/login-component/login-component';
import {AuthGuard} from './auth.guard.ts-guard';
import {RegisterComponent} from './pages/register-component/register-component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'home', loadComponent: () => import('./pages/home/home').then(m => m.Home),
    canActivate: [AuthGuard]
  },
  { path: 'home', component: Home },
  { path: 'event', component: EventComponent },
  { path: '**', redirectTo: 'home' }

];
