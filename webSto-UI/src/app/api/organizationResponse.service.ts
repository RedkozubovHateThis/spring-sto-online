import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import { UserService } from './user.service';

@Injectable()
export class OrganizationResponseService {

  constructor(private http: HttpClient, private router: Router, private userService:UserService) { }
  baseUrl: string = 'http://localhost:8181/';

  getOne(id:number) {
    const headers = this.userService.getHeaders();

    return this.http.get( this.baseUrl + 'secured/organizations/' + id, {headers} );
  }

  getOneByInn(inn:string) {
    const headers = this.userService.getHeaders();

    return this.http.get( this.baseUrl + 'secured/organizations/inn/' + inn, {headers} );
  }

}
