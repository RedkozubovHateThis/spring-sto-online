import {UserRole} from "./userRole";

export class User {

  id: number;
  username: string;
  password: string;
  firstName: string;
  lastName: string;
  middleName: string;
  fio: string;
  phone: string;
  email: string;
  enabled: boolean;
  roles: UserRole[];
  clientId: number;
  organizationId: number;
  moderatorId: number;

  //TODO: подключить js-data-angular и переделать на computed property
  admin: boolean;
  client: boolean;
  guest: boolean;
  serviceLeader: boolean;
  moderator: boolean;
  approved: boolean;

}
