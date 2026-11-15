import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {Hello} from './hello/hello';
import { ListeMateriels } from './materiel/liste-materiels/liste-materiels';
import { ListeReservations } from './reservation/liste-reservations/liste-reservations';
import { Connexion } from './securite/connexion/connexion';

@Component({
  imports: [RouterOutlet, Hello,ListeMateriels, ListeReservations, Connexion],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {
  protected readonly title = signal('location-client');
}
