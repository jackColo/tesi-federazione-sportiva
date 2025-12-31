import { AffiliationStatus } from '../enums/affiliation-status.enum';
import { GenderEnum } from '../enums/gender.enum';
import { AthleteDTO } from './dtos';
import { User } from './user.model';

export class Athlete extends User {
  private _birthDate: string;
  private _clubId: string;
  private _weight: number;
  private _height: number;
  private _gender: GenderEnum;
  private _affiliationStatus: AffiliationStatus;
  private _affiliationDate: string;
  private _firstAffiliationDate: string;
  private _medicalCertificateNumber: string;
  private _medicalCertificateExpireDate: string;

  constructor(data: AthleteDTO) {
    super(data);

    this._birthDate = data.birthDate;
    this._clubId = data.clubId;
    this._weight = data.weight;
    this._height = data.height;
    this._gender = data.gender;
    this._affiliationStatus = data.affiliationStatus;
    this._affiliationDate = data.affiliationDate;
    this._firstAffiliationDate = data.firstAffiliationDate;
    this._medicalCertificateNumber = data.medicalCertificateNumber;
    this._medicalCertificateExpireDate = data.medicalCertificateExpireDate;
  }

  // Getter and Setter

  get birthDate(): string {
    return this._birthDate;
  }
  set birthDate(value: string) {
    this._birthDate = value;
  }

  override get clubId(): string {
    return this._clubId;
  }
  override set clubId(value: string) {
    this._clubId = value;
  }

  get weight(): number {
    return this._weight;
  }
  set weight(value: number) {
    if (value < 0) {
      console.warn('Il peso non può essere negativo');
      return;
    }
    this._weight = value;
  }

  get height(): number {
    return this._height;
  }
  set height(value: number) {
    if (value < 0) {
      console.warn("L'altezza non può essere negativa");
      return;
    }
    this._height = value;
  }

  get gender(): GenderEnum {
    return this._gender;
  }
  set gender(value: GenderEnum) {
    this._gender = value;
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

  get medicalCertificateNumber(): string {
    return this._medicalCertificateNumber;
  }
  set medicalCertificateNumber(value: string) {
    this._medicalCertificateNumber = value;
  }

  get medicalCertificateExpireDate(): string {
    return this._medicalCertificateExpireDate;
  }
  set medicalCertificateExpireDate(value: string) {
    this._medicalCertificateExpireDate = value;
  }

  // Additional Methods

  public isMedicalCertificateValid(): boolean {
    if (!this._medicalCertificateExpireDate) return false;
    const today = new Date();
    const expireDate = new Date(this._medicalCertificateExpireDate);
    return expireDate >= today;
  }
}
