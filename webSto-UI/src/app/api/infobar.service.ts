import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import { UserService } from './user.service';

@Injectable()
export class InfobarService {

  constructor(private http: HttpClient, private userService: UserService) { }

  getClientInfo() {
    const headers = this.userService.getHeaders();

    return this.http.get( `${this.userService.getApiUrl()}secured/infoBar/client`, {headers} );
  }

  getServiceLeaderInfo() {
    const headers = this.userService.getHeaders();

    return this.http.get( `${this.userService.getApiUrl()}secured/infoBar/serviceLeader`, {headers} );
  }

  getModeratorInfo(organizationId: number) {
    const headers = this.userService.getHeaders();

    const params = {
      organizationId: organizationId != null ? organizationId.toString() : ''
    };

    return this.http.get( `${this.userService.getApiUrl()}secured/infoBar/moderator`, {headers, params} );
  }

}
