import { Component, signal } from '@angular/core';
import {RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {Footer} from './pages/footer/footer';
import {Header} from './pages/header/header';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Footer, RouterLink, RouterLinkActive, Header],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('EventManager');
}
