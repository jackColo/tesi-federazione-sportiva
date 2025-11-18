import { CompetitionType } from "../enums/competition-type.enum";
import { EventStatus } from "../enums/event-status.enum";
import { Role } from "../enums/role.enum";

export interface JwtResponseDTO {
    token: string;
}

export interface LogUserDTO {
    email: string;
    password: string;
}

export interface UserDTO {
    id: string;
    firstName: string;
    lastName: string;
    email: string;
    role: Role;
}

export interface CreateUserDTO {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    role: Role;
}

export interface CreateEventDTO {
    name: string;
    location: string;
    date: Date;
    registrationOpenDate: Date;
    registrationCloseDate: Date;
    disciplines: CompetitionType[];
}

export interface EventDTO {
    id: string;
    name: string;
    location: string;
    date: Date;
    registrationOpenDate: Date;
    registrationCloseDate: Date;
    status: EventStatus;
    disciplines: CompetitionType[];
}