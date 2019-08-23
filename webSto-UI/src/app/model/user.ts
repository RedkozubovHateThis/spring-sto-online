export class User {

  id: number;
  username: string;
  firstName: string;
  lastName: string;
  middleName: string;
  phone: string;
  email: string;

  public getFio():string {
    if ( this.lastName === null )
      return this.username;
    else {

      if ( this.middleName == null && this.firstName == null )
        return this.lastName;
      else if ( this.middleName == null && this.firstName != null )
        return this.lastName + " " + this.firstName.substring(0, 1) + ".";
      else if ( this.middleName != null && this.firstName == null )
        return this.lastName + " " + this.middleName.substring(0, 1) + ".";
      else if ( this.middleName != null && this.firstName != null )
        return this.lastName + " " + this.firstName.substring(0, 1) + ". " +
          this.middleName.substring(0, 1) + ".";
      else
        return this.username;

    }
  }

}
