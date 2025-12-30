import { FederationManagerDTO } from './dtos';
import { User } from './user.model';

export class FederationManager extends User {

  constructor(data: FederationManagerDTO) {
    super(data);
  }
}
