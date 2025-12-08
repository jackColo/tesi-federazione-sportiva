import { Role } from '../enums/role.enum';
import { UserDTO } from './dtos';

export abstract class User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  role: Role;

  constructor(data: UserDTO) {
    this.id = data.id;
    this.email = data.email;
    this.firstName = data.firstName;
    this.lastName = data.lastName;
    this.role = data.role;
  }

  public getFullName(): string {
    return `${this.firstName} ${this.lastName}`;
  }
}
