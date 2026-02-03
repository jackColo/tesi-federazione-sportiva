import { AffiliationStatus } from '../enums/affiliation-status.enum';
import { CompetitionType } from '../enums/competition-type.enum';
import { EnrollmentStatus } from '../enums/enrollment-status.enum';
import { EventStatus } from '../enums/event-status.enum';
import { GenderEnum } from '../enums/gender.enum';
import { Role } from '../enums/role.enum';

export interface JwtResponseDTO {
  token: string;
  id: string;
  email: string;
  role: Role;
}

export interface JwtResponsePayload {
  sub: string;        
  id: string;         
  role: string;       
  iat: number;
  exp: number;
}

export interface ErrorResponse {
  error: { status: number; message: string; timestamp: string };
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
  description: string;
  location: string;
  date: string;
  registrationOpenDate: string;
  registrationCloseDate: string;
  disciplines: CompetitionType[];
}

export interface EventDTO {
  id: string;
  name: string;
  description: string;
  location: string;
  date: string;
  registrationOpenDate: string;
  registrationCloseDate: string;
  status: EventStatus;
  disciplines: CompetitionType[];
  enrolledCount: number;
}

// CLUB DTOs

export interface CreateClubDTO {
  id?: string;
  name: string;
  fiscalCode: string;
  legalAddress: string;
  affiliationStatus: AffiliationStatus;
  manager?: CreateUserDTO;
}

export interface UpdateClubDTO {
  id: string;
  name: string;
  fiscalCode: string;
  legalAddress: string;
  affiliationStatus: AffiliationStatus;
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
  eventId: string;
  clubId: string;
  competitionType: CompetitionType;
  enrollmentDate: string;
  athleteId: string;
  athleteFirstname: string;
  athleteLastname: string;
  athleteWeight: string;
  athleteHeight: string;
  athleteGender: string;
  athleteAffiliationStatus: AffiliationStatus;
  athleteMedicalCertificateExpireDate: string;
  draft: boolean;
}

export interface EnrollmentDTO {
  id: string;
  eventId: string;
  clubId: string;
  competitionType: CompetitionType;
  enrollmentDate: string;
  status: EnrollmentStatus;
  athleteId: string;
  athleteClubName: string;
  athleteFirstname: string;
  athleteLastname: string;
  athleteWeight: string;
  athleteHeight: string;
  athleteGender: string;
  athleteAffiliationStatus: AffiliationStatus;
  athleteMedicalCertificateExpireDate: string;
}

// ChatMessage DTOs
export interface ChatMessageOutputDTO {
  message: string;
  chatUserId: string;
}

export interface ChatMessageInputDTO {
  id: string,
  chatUserId: string,
  senderId: string,
  senderRole: Role,
  content: string,
  timestamp: string
}

export interface ChatSummaryDTO {
  chatUserId: string;
  clubManagerName: string;
  lastMessageTime: string;
  status: 'FREE' | 'ASSIGNED';
  assignedAdminId?: string | null;
  waitingForReply: boolean;
}
