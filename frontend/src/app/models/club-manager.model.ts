import { ClubManagerDTO } from './dtos';
import { User } from './user.model';

export class ClubManager extends User {
  private _clubId: string;

  constructor(data: ClubManagerDTO) {
    super(data);
    this._clubId = data.clubId;
  }

  override get clubId(): string {
    return this._clubId;
  }

  override set clubId(value: string) {
    this._clubId = value;
  }
}
