import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { of, Subject } from 'rxjs';

import { App } from './app';
import { MaterielService } from './materiel/materiel.service';
import { ReservationService } from './reservation/reservation.service';
import { ReservationsEvenementsService } from './websocket/reservations-evenements.service';

import { AuthentificationService } from './api';

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        { provide: AuthentificationService, useValue: {} },
        { provide: MaterielService, useValue: { lister: () => of([]) } },
        { provide: ReservationService, useValue: { lister: () => of([]) } },
        {
          provide: ReservationsEvenementsService,
          useValue: { evenements$: new Subject(), connecte: signal(false) },
        },
      ],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should render title', async () => {
    const fixture = TestBed.createComponent(App);
    await fixture.whenStable();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h1')?.textContent).toContain('location-client');
  });
});
