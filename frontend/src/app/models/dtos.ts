import { AffiliationStatus } from '../enums/affiliation-status.enum';
import { CompetitionType } from '../enums/competition-type.enum';
import { EnrollmentStatus } from '../enums/enrollment-status.enum';
import { EventStatus } from '../enums/event-status.enum';
import { GenderEnum } from '../enums/gender.enum';
import { NotificationType } from '../enums/notification-type.enum';
import { Role } from '../enums/role.enum';

export interface JwtResponseDTO {
  token: string;
}

// USER DTOs

export interface LogUserDTO {
  email: string;
  password: string;
}

export interface BaseCreateUserDTO {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  role: Role;
}

export interface CreateFederationManagerDTO extends BaseCreateUserDTO {
  role: Role.FEDERATION_MANAGER;
}

export interface CreateClubManagerDTO extends BaseCreateUserDTO {
  role: Role.CLUB_MANAGER;
  clubId?: string;
}

export interface CreateAthleteDTO extends BaseCreateUserDTO {
  role: Role.ATHLETE;
  birthDate: string;
  weight: number;
  height: number;
  gender: GenderEnum;
  clubId: string;
  medicalCertificateNumber?: string;
  medicalCertificateExpireDate: string;
}

export type CreateUserDTO = CreateFederationManagerDTO | CreateClubManagerDTO | CreateAthleteDTO;

export interface BaseUserDTO {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  role: Role;
}

export interface FederationManagerDTO extends BaseUserDTO {
  role: Role.FEDERATION_MANAGER;
}

export interface ClubManagerDTO extends BaseUserDTO {
  role: Role.CLUB_MANAGER;
  clubId: string;
}

export interface AthleteDTO extends BaseUserDTO {
  role: Role.ATHLETE;
  clubId: string;
  birthDate: string;
  weight: number;
  height: number;
  gender: GenderEnum;
  affiliationStatus: AffiliationStatus;
  affiliationDate: string;
  firstAffiliationDate: string;
  medicalCertificateNumber: string;
  medicalCertificateExpireDate: string;
}

export type UserDTO = FederationManagerDTO | ClubManagerDTO | AthleteDTO;

// EVENT DTOs

export interface CreateEventDTO {
  name: string;
  location: string;
  date: string;
  registrationOpenDate: string;
  registrationCloseDate: string;
  disciplines: CompetitionType[];
}

export interface EventDTO {
  id: string;
  name: string;
  location: string;
  date: string;
  registrationOpenDate: string;
  registrationCloseDate: string;
  status: EventStatus;
  disciplines: CompetitionType[];
}

// CLUB DTOs

export interface CreateClubDTO {
  name: string;
  fiscalCode: string;
  legalAddress: string;
  affiliationStatus: AffiliationStatus;
  manager: CreateUserDTO;
}

export interface ClubDTO {
  id: string;
  name: string;
  fiscalCode: string;
  legalAddress: string;
  affiliationStatus: AffiliationStatus;
  affiliationDate: string;
  firstAffiliationDate: string;
  managers: string[];
  athletes: string[];
}

export interface CreateEnrollmentDTO {
  clubId: string;
  athleteId: string;
  eventId: string;
  competitionType: CompetitionType;
}

export interface EnrollmentDTO {
    id: string;
    eventName: string;
    eventDate: string;
    athleteName: string;
    athleteSurname: string;
    clubName: string;
    discipline: CompetitionType;
    category: string;
    status: EnrollmentStatus;
}

// Notification DTOs
export interface NotificationDTO {
  id: string;
  title: string;
  message: string;
  createdAt: string; // ISO String
  isRead: boolean;
  type: NotificationType;
  link?: string; // Opzionale: se cliccando porta a una pagina specifica (es. dettaglio club)
}