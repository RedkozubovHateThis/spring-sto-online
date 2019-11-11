import {UserRole} from './userRole';

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
  moderatorFio: string;
  isApproved: boolean;
  isAutoRegistered: boolean;
  inVacation: boolean;
  inn: string;
  replacementModeratorId: number;
  replacementModeratorFio: string;
  partShops: number[];
  allowSms: boolean;

  // TODO: подключить js-data-angular и переделать на computed property
  userAdmin: boolean;
  userClient: boolean;
  userGuest: boolean;
  userServiceLeader: boolean;
  userModerator: boolean;

}
