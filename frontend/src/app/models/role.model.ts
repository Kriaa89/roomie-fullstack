export enum Role {
  RENTER = 'RENTER',
  OWNER = 'OWNER',
  ROOMMATE_HOST = 'ROOMMATE_HOST',
  ADMIN = 'ADMIN'
}

export interface RoleRequest {
  roleType: Role;
}
