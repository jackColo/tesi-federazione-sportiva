import { CompetitionType } from '../enums/competition-type.enum';
import { EventStatus } from '../enums/event-status.enum';
import { EventDTO } from './dtos';

export class Event {
  _id: string;
  _name: string;
  _description: string;
  _location: string;
  _date: string;
  _registrationOpenDate: string;
  _registrationCloseDate: string;
  _status: EventStatus;
  _disciplines: CompetitionType[];
  _enrolledCount: number;

  constructor(data: EventDTO) {
    this._id = data.id;
    this._name = data.name;
    this._description = data.description;
    this._location = data.location;
    this._date = data.date;
    this._registrationOpenDate = data.registrationOpenDate;
    this._registrationCloseDate = data.registrationCloseDate;
    this._status = data.status;
    this._disciplines = data.disciplines;
    this._enrolledCount = data.enrolledCount;
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

  get description(): string {
    return this._description;
  }

  set description(value: string) {
    this._description = value;
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

  get enrolledCount(): number {
    return this._enrolledCount;
  }

  set enrolledCount(value: number) {
    this._enrolledCount = value;
  }

  public toDTO(): EventDTO {
    const eventDTO: EventDTO = {
      id: this.id,
      name: this.name,
      description: this.description,
      location: this.location,
      date: this.date,
      registrationCloseDate: this.registrationCloseDate,
      registrationOpenDate: this.registrationOpenDate,
      status: this.status,
      disciplines: this.disciplines,
      enrolledCount: this.enrolledCount,
    };
    return eventDTO;
  }
}
