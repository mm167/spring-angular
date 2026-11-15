import {Component, inject, signal} from '@angular/core';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { SessionService } from './securite/session.service';

@Component({
  imports: [RouterOutlet, RouterLink],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {
  protected readonly title = signal('location-client');
  protected readonly session = inject(SessionService);
  private readonly router = inject(Router);

  protected deconnecter(): void {
    this.session.fermer();
    void this.router.navigateByUrl('/connexion');
  }
}
