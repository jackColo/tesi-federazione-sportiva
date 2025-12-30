import { CompetitionType } from '../enums/competition-type.enum';
import { EventStatus } from '../enums/event-status.enum';
import { EventDTO } from './dtos';

export class Event {
  _id: string;
  _name: string;
  _location: string;
  _date: string;
  _registrationOpenDate: string;
  _registrationCloseDate: string;
  _status: EventStatus;
  _disciplines: CompetitionType[];

  constructor(data: EventDTO) {
    this._id = data.id;
    this._name = data.name;
    this._location = data.location;
    this._date = data.date;
    this._registrationOpenDate = data.registrationOpenDate;
    this._registrationCloseDate = data.registrationCloseDate;
    this._status = data.status;
    this._disciplines = data.disciplines;
  }

  get id(): string {
    return this._id;
  }

  get name(): string {
    return this._name;
  }

  set name(value: string) {
    this._name = value;
  }

  get location(): string {
    return this._location;
  }

  set location(value: string) {
    this._location = value;
  }

  get date(): string {
    return this._date;
  }

  set date(value: string) {
    this._date = value;
  }

  get registrationOpenDate(): string {
    return this._registrationOpenDate;
  }     
  set registrationOpenDate(value: string) {
    this._registrationOpenDate = value;
  }

  get registrationCloseDate(): string {
    return this._registrationCloseDate;
  }     
  set registrationCloseDate(value: string) {
    this._registrationCloseDate = value;
  }

  get status(): EventStatus {
    return this._status;
  }     
  set status(value: EventStatus) {
    this._status = value;
  }

  get disciplines(): CompetitionType[] {
    return this._disciplines;
  }     
  set disciplines(value: CompetitionType[]) {
    this._disciplines = value;
  }     
}
