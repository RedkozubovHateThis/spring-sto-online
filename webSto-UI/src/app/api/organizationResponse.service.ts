import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import { UserService } from './user.service';
import {ManagerResponse} from '../model/firebird/managerResponse';
import {Observable} from 'rxjs';

@Injectable()
export class OrganizationResponseService {

  constructor(private http: HttpClient, private router: Router, private userService: UserService) { }

  getOne(id: number) {
    const headers = this.userService.getHeaders();

    return this.http.get( this.userService.getApiUrl() + 'secured/organizations/' + id, {headers} );
  }

  getOneByInn(inn: string) {
    const headers = this.userService.getHeaders();

    return this.http.get( this.userService.getApiUrl() + 'secured/organizations/inn/' + inn, {headers} );
  }

  getAll() {
    const headers = this.userService.getHeaders();

    return this.http.get( this.userService.getApiUrl() + 'secured/organizations/findAll', {headers} );
  }

  getOneManager(managerId: number): Observable<ManagerResponse> {
    const headers = this.userService.getHeaders();

    return this.http.get<ManagerResponse>( this.userService.getApiUrl() + 'secured/organizations/managers/' + managerId, {headers} );
  }

  getAllManagers(organizationId: number): Observable<ManagerResponse[]> {
    const headers = this.userService.getHeaders();
    const params = {
      organizationId: organizationId != null ? organizationId.toString() : ''
    };

    return this.http.get<ManagerResponse[]>( this.userService.getApiUrl() + 'secured/organizations/managers/findAll', {headers, params} );
  }

  // TODO: вынести в отдельный сервис
  getAllClients() {
    const headers = this.userService.getHeaders();

    return this.http.get( this.userService.getApiUrl() + 'secured/clients/findAll', {headers} );
  }

}
