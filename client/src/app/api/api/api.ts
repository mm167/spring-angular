export * from './materiels.service';
import { MaterielsService } from './materiels.service';
export * from './reservations.service';
import { ReservationsService } from './reservations.service';
export const APIS = [MaterielsService, ReservationsService];
