import { Club } from './club.model';
import { ClubManagerDTO } from './dtos';
import { User } from './user.model';

export class ClubManager extends User {
  club: Club;

  constructor(data: ClubManagerDTO) {
    super(data);
    this.club = new Club(data.club);
  }
}
