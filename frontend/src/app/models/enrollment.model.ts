import { CompetitionType } from "../enums/competition-type.enum";
import { EnrollmentStatus } from "../enums/enrollment-status.enum";
import { EnrollmentDTO } from "./dtos";


export class Enrollment {
  private _id: string;
  private _eventName: string;
  private _eventDate: string;
  private _athleteName: string;
  private _athleteSurname: string;
  private _clubName: string;
  private _discipline: CompetitionType;
  private _category: string;
  private _status: EnrollmentStatus;

  constructor(data: EnrollmentDTO) {
    this._id = data.id;
    this._eventName = data.eventName;
    this._eventDate = data.eventDate;
    this._athleteName = data.athleteName;
    this._athleteSurname = data.athleteSurname;
    this._clubName = data.clubName;
    this._discipline = data.discipline;
    this._category = data.category;
    this._status = data.status;
  }

  // Getters and Setters
  get id(): string {
    return this._id;
  }

  get eventName(): string {
    return this._eventName;
  }

  set eventName(value: string) {
    if (!value || value.trim() === '') {
        throw new Error("Il nome dell'evento non può essere vuoto");
    }
    this._eventName = value;
  }

  get eventDate(): string {
    return this._eventDate;
  }

  set eventDate(value: string) {
    this._eventDate = value;
  }

  get athleteName(): string {
    return this._athleteName;
  }
  set athleteName(value: string) {
    this._athleteName = value;
  }

  get athleteSurname(): string {
    return this._athleteSurname;
  }
  set athleteSurname(value: string) {
    this._athleteSurname = value;
  }

  get clubName(): string {
    return this._clubName;
  }
  set clubName(value: string) {
    this._clubName = value;
  }

  get discipline(): CompetitionType {
    return this._discipline;
  }
  set discipline(value: CompetitionType) {
    this._discipline = value;
  }

  get category(): string {
    return this._category;
  }
  set category(value: string) {
    this._category = value;
  }

  get status(): EnrollmentStatus {
    return this._status;
  }
  set status(value: EnrollmentStatus) {
    this._status = value;
  }

  // Additional Methods

  public isConfirmed(): boolean {
    return this._status === EnrollmentStatus.APPROVED; 
  }
}