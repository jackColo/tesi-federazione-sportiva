import { AffiliationStatus } from '../enums/affiliation-status.enum';
import { CompetitionType } from '../enums/competition-type.enum';
import { EnrollmentStatus } from '../enums/enrollment-status.enum';
import { EventStatus } from '../enums/event-status.enum';
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

export interface CreateUserDTO {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  role: Role;
}

export interface CreateAthleteDTO extends CreateUserDTO {
  birthDate: Date;
  weight: number;
  height: number;
  clubId: String;
  medicalCertificateNumber: String;
  medicalCertificateExpireDate: Date;
}

export interface UserDTO {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  role: Role;
}

export interface ClubManagerDTO extends UserDTO {
  club: ClubDTO;
}

export interface AthleteDTO extends UserDTO {
  club: ClubDTO;
  birthDate: string;
  weight: number;
  height: number;
  experience: string;
  affiliationStatus: AffiliationStatus;
  affiliationDate: string;
  firstAffiliationDate: string;
}

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