import { AffiliationStatus } from '../enums/affiliation-status.enum';
import { Club } from './club.model';
import { AthleteDTO } from './dtos';
import { User } from './user.model';

export class Athlete extends User {
  birthDate: Date;
  club: Club;
  weight: number;
  height: number;
  experience: string;
  affiliationStatus: AffiliationStatus;
  affiliationDate: Date;
  firstAffiliationDate: Date;

  constructor(data: AthleteDTO) {
    super(data);
    this.birthDate = new Date(data.birthDate);
    this.club = new Club(data.club);
    this.weight = data.weight;
    this.height = data.height;
    this.experience = data.experience;
    this.affiliationStatus = data.affiliationStatus;
    this.affiliationDate = new Date(data.affiliationDate);
    this.firstAffiliationDate = new Date(data.firstAffiliationDate);
  }
}
