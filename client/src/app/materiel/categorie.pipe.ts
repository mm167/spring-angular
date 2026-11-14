import { Pipe, PipeTransform } from '@angular/core';

import { Categorie } from '../api';

const LIBELLES: Record<Categorie, string> = {
  MINI_PELLE: 'Mini-pelle',
  NACELLE: 'Nacelle',
  COMPACTEUR: 'Compacteur',
};

@Pipe({ name: 'categorie' })
export class CategoriePipe implements PipeTransform {
  transform(valeur: Categorie): string {
    return LIBELLES[valeur];
  }
}
