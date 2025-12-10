import { AffiliationStatus } from '../enums/affiliation-status.enum';
import { CompetitionType } from '../enums/competition-type.enum';
import { EventStatus } from '../enums/event-status.enum';
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
