import { AffiliationStatus } from '../enums/affiliation-status.enum';
import { ClubDTO } from './dtos';

export class Club {
  id: string;
  name: string;
  fiscalCode: string;
  legalAddress: string;
  affiliationStatus: AffiliationStatus;
  affiliationDate: Date;
  firstAffiliationDate: Date;
  managerIds: string[];
  athleteIds: string[];

  constructor(data: ClubDTO) {
    this.id = data.id;
    this.name = data.name;
    this.fiscalCode = data.fiscalCode;
    this.legalAddress = data.legalAddress;
    this.affiliationStatus = data.affiliationStatus;
    this.affiliationDate = new Date(data.affiliationDate);
    this.firstAffiliationDate = new Date(data.firstAffiliationDate);
    this.managerIds = data.managers;
    this.athleteIds = data.athletes;
  }
}
