import { AffiliationStatus } from "../enums/affiliation-status.enum";
import { CompetitionType } from "../enums/competition-type.enum";
import { EnrollmentStatus } from "../enums/enrollment-status.enum";
import { EnrollmentDTO } from "./dtos";

export class Enrollment {
  private _id: string;
  private _eventId: string;
  private _clubId: string;
  private _discipline: CompetitionType;
  private _enrollmentDate: string;
  private _status: EnrollmentStatus;

  private _athleteId: string;
  private _athleteFirstname: string;
  private _athleteLastname: string;
  private _athleteWeight: string;
  private _athleteHeight: string;
  private _athleteGender: string;
  private _athleteAffiliationStatus: AffiliationStatus;
  private _athleteMedicalCertificateExpireDate: string;

  constructor(data: EnrollmentDTO) {
    this._id = data.id;
    this._eventId = data.eventId;
    this._clubId = data.clubId;
    this._discipline = data.competitionType;
    this._enrollmentDate = data.enrollmentDate;
    this._status = data.status;

    this._athleteId = data.athleteId;
    this._athleteFirstname = data.athleteFirstname;
    this._athleteLastname = data.athleteLastname;
    this._athleteWeight = data.athleteWeight;
    this._athleteHeight = data.athleteHeight;
    this._athleteGender = data.athleteGender;
    this._athleteAffiliationStatus = data.athleteAffiliationStatus;
    this._athleteMedicalCertificateExpireDate = data.athleteMedicalCertificateExpireDate;
  }

  get id(): string {
    return this._id;
  }

  get eventId(): string {
    return this._eventId;
  }
  set eventId(value: string) {
    this._eventId = value;
  }

  get clubId(): string {
    return this._clubId;
  }
  set clubId(value: string) {
    this._clubId = value;
  }

  get discipline(): CompetitionType {
    return this._discipline;
  }
  set discipline(value: CompetitionType) {
    this._discipline = value;
  }

  get enrollmentDate(): string {
    return this._enrollmentDate;
  }
  set enrollmentDate(value: string) {
    this._enrollmentDate = value;
  }

  get status(): EnrollmentStatus {
    return this._status;
  }
  set status(value: EnrollmentStatus) {
    this._status = value;
  }

  get athleteFirstname(): string {
    return this._athleteFirstname;
  }
  set athleteFirstname(value: string) {
    this._athleteFirstname = value;
  }

  get athleteId(): string {
    return this._athleteId;
  }
  set athleteId(value: string) {
    this._athleteId = value;
  }

  get athleteLastname(): string {
    return this._athleteLastname;
  }
  set athleteLastname(value: string) {
    this._athleteLastname = value;
  }

  get athleteWeight(): string {
    return this._athleteWeight;
  }
  set athleteWeight(value: string) {
    this._athleteWeight = value;
  }

  get athleteHeight(): string {
    return this._athleteHeight;
  }
  set athleteHeight(value: string) {
    this._athleteHeight = value;
  }

  get athleteGender(): string {
    return this._athleteGender;
  }
  set athleteGender(value: string) {
    this._athleteGender = value;
  }

  get athleteAffiliationStatus(): AffiliationStatus {
    return this._athleteAffiliationStatus;
  }
  set athleteAffiliationStatus(value: AffiliationStatus) {
    this._athleteAffiliationStatus = value;
  }

  get athleteMedicalCertificateExpireDate(): string {
    return this._athleteMedicalCertificateExpireDate;
  }
  set athleteMedicalCertificateExpireDate(value: string) {
    this._athleteMedicalCertificateExpireDate = value;
  }

  public toDTO(): EnrollmentDTO {
    return {
      id: this.id,
      eventId: this.eventId,
      clubId: this.clubId,
      competitionType: this.discipline,
      enrollmentDate: this.enrollmentDate,
      status: this.status,
      athleteId: this.athleteId,
      athleteFirstname: this.athleteFirstname,
      athleteLastname: this.athleteLastname,
      athleteWeight: this.athleteWeight,
      athleteHeight: this.athleteHeight,
      athleteGender: this.athleteGender,
      athleteAffiliationStatus: this.athleteAffiliationStatus,
      athleteMedicalCertificateExpireDate: this.athleteMedicalCertificateExpireDate
    };
  }

  public isAthleteMedicalCertificateValid(): boolean {
    if (!this._athleteMedicalCertificateExpireDate) return false;
    const today = new Date();
    const expireDate = new Date(this._athleteMedicalCertificateExpireDate);
    return expireDate >= today;
  }

  public isAthleteAffiliationValid(): boolean {
    return this._athleteAffiliationStatus === AffiliationStatus.ACCEPTED
  }

  public isConfirmed(): boolean {
    return this._status === EnrollmentStatus.APPROVED; 
  }
}