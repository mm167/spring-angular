import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {Hello} from './hello/hello';
import { ListeMateriels } from './materiel/liste-materiels/liste-materiels';

@Component({
  imports: [RouterOutlet, Hello,ListeMateriels],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {
  protected readonly title = signal('location-client');
}
