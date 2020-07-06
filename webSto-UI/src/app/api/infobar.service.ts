import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {UserService} from './user.service';
import {environment} from '../../environments/environment';

@Injectable()
export class InfobarService {

  constructor(private http: HttpClient, private userService: UserService) { }

  getClientInfo() {
    return this.http.get( `${environment.getApiUrl()}infoBar/client`);
  }

  getServiceLeaderInfo() {
    return this.http.get( `${environment.getApiUrl()}infoBar/serviceLeader`);
  }

  getModeratorInfo(organizationId: number) {
    const params = {
      organizationId: organizationId != null ? organizationId.toString() : ''
    };

    return this.http.get( `${environment.getApiUrl()}infoBar/moderator`, {params} );
  }

}
