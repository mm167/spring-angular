import { AsyncPipe, CurrencyPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { BehaviorSubject, map, switchMap, tap } from 'rxjs';

import { CategoriePipe } from '../categorie.pipe';
import { MaterielService } from '../materiel.service';

@Component({
  selector: 'app-liste-materiels',
  imports: [AsyncPipe, CurrencyPipe, CategoriePipe],
  templateUrl: './liste-materiels.html',
})
export class ListeMateriels {
  private readonly service = inject(MaterielService);
  private readonly rechargement$ = new BehaviorSubject<void>(undefined);

  protected readonly materiels$ = this.rechargement$.pipe(
    switchMap(() => this.service.lister()),
    tap((liste) => console.log('Reçu :', liste.length, 'matériel(s)')),
    map((liste) => [...liste].sort((a, b) => a.tarifJournalier - b.tarifJournalier)),
  );

  protected ajouter(): void {
    this.service
      .creer({ nom: 'Nacelle 18 m', categorie: 'NACELLE', tarifJournalier: 15 })
      .subscribe({
        next: (materiel) => {
          console.log('Créé :', materiel.id);
          this.rechargement$.next();
        },
      });
  }
}
