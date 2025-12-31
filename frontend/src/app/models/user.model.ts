import { Role } from '../enums/role.enum';
import { UserDTO } from './dtos';

export class User {
  _id: string;
  _email: string;
  _firstName: string;
  _lastName: string;
  _role: Role;

  constructor(data: UserDTO) {
    this._id = data.id;
    this._email = data.email;
    this._firstName = data.firstName;
    this._lastName = data.lastName;
    this._role = data.role;
  }

  get id(): string {
    return this._id;
  }

  get email(): string {
    return this._email;
  }

  set email(value: string) {
    this._email = value;
  }

  get firstName(): string {
    return this._firstName;
  }

  set firstName(value: string) {
    this._firstName = value;
  }

  get lastName(): string {
    return this._lastName;
  }

  set lastName(value: string) {
    this._lastName = value;
  }

  get role(): Role {
    return this._role;
  }
  set role(value: Role) {
    this._role = value;
  }

  // Virtual method to be overridden by subclasses
  get clubId(): string | null {
    return null;
  }
}
