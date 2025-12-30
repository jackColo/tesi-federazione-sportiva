import { AffiliationStatus } from '../enums/affiliation-status.enum';
import { ClubDTO } from './dtos';

export class Club {
  _id: string;
  _name: string;
  _fiscalCode: string;
  _legalAddress: string;
  _affiliationStatus: AffiliationStatus;
  _affiliationDate: string;
  _firstAffiliationDate: string;
  _managerIds: string[];
  _athleteIds: string[];

  constructor(data: ClubDTO) {
    this._id = data.id;
    this._name = data.name;
    this._fiscalCode = data.fiscalCode;
    this._legalAddress = data.legalAddress;
    this._affiliationStatus = data.affiliationStatus;
    this._affiliationDate = data.affiliationDate;
    this._firstAffiliationDate = data.firstAffiliationDate;
    this._managerIds = data.managers;
    this._athleteIds = data.athletes;
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

  get fiscalCode(): string {
    return this._fiscalCode;
  }
  set fiscalCode(value: string) {
    this._fiscalCode = value;
  }

  get legalAddress(): string {
    return this._legalAddress;
  }
  set legalAddress(value: string) {
    this._legalAddress = value;
  }

  get affiliationStatus(): AffiliationStatus {
    return this._affiliationStatus;
  }
  set affiliationStatus(value: AffiliationStatus) {
    this._affiliationStatus = value;
  }

  get affiliationDate(): string {
    return this._affiliationDate;
  }
  set affiliationDate(value: string) {
    this._affiliationDate = value;
  }
  get firstAffiliationDate(): string {
    return this._firstAffiliationDate;
  }
  set firstAffiliationDate(value: string) {
    this._firstAffiliationDate = value;
  }

  get managerIds(): string[] {
    return this._managerIds;
  }
  set managerIds(value: string[]) {
    this._managerIds = value;
  }

  get athleteIds(): string[] {
    return this._athleteIds;
  }
  set athleteIds(value: string[]) {
    this._athleteIds = value;
  }

  public isConfirmed(): boolean {
    return this._affiliationStatus === AffiliationStatus.ACCEPTED;
  }
}
