import { Component } from '@angular/core';
import {Router, RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {AuthServiceTs} from '../../services/auth.service.ts';
import {AsyncPipe, NgIf} from '@angular/common';

@Component({
  selector: 'app-header',
  imports: [
    RouterLink,
    RouterLinkActive,
    RouterOutlet,
    NgIf,
    AsyncPipe
  ],
  templateUrl: './header.html',
  styleUrl: './header.css'
})
export class Header {
  isLoggedIn$: any;
  constructor(
    private authService: AuthServiceTs,
    private router: Router
  ) {

    this.isLoggedIn$ = this.authService.isAuthenticated$;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
