import {UserRole} from "./userRole";

export class User {

  id: number;
  username: string;
  firstName: string;
  lastName: string;
  middleName: string;
  fio: string;
  phone: string;
  email: string;
  enabled:boolean;
  roles: UserRole[];

}
