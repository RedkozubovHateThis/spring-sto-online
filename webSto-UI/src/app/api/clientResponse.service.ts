import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import { UserService } from './user.service';

@Injectable()
export class ClientResponseService {

  constructor(private http: HttpClient, private router: Router, private userService: UserService) { }

  getOne(id: number) {
    const headers = this.userService.getHeaders();

    return this.http.get( this.userService.getApiUrl() + 'secured/clients/' + id, {headers} );
  }

  getOneByVin(vinNumber: string) {
    const headers = this.userService.getHeaders();

    return this.http.get( this.userService.getApiUrl() + 'secured/clients/vin/' + vinNumber, {headers} );
  }

}
