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
  isAutoRegistered: boolean;
  inn: string;
  vin: string;
  currentSubscriptionId: number;
  serviceWorkPrice: number;
  serviceGoodsCost: number;
  balance: number;
  isCurrentSubscriptionExpired: boolean;
  isCurrentSubscriptionEmpty: boolean;
  isBalanceInvalid: boolean;
  subscriptionTypeId: number;
  vinNumbers: string[];

  // TODO: подключить js-data-angular и переделать на computed property
  userAdmin: boolean;
  userClient: boolean;
  userServiceLeader: boolean;

}
