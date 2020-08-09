import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {UserService} from './user.service';
import {environment} from '../../environments/environment';
import {Observable} from 'rxjs';
import {ClientInfo} from '../model/info/clientInfo';
import {ServiceLeaderInfo} from '../model/info/serviceLeaderInfo';

@Injectable()
export class InfobarService {

  constructor(private http: HttpClient, private userService: UserService) { }

  getClientInfo(): Observable<ClientInfo> {
    return this.http.get<ClientInfo>( `${environment.getApiUrl()}infoBar/client`);
  }

  getServiceLeaderInfo(): Observable<ServiceLeaderInfo> {
    return this.http.get<ServiceLeaderInfo>( `${environment.getApiUrl()}infoBar/serviceLeader`);
  }

  getAdminInfo(organizationId: string): Observable<ServiceLeaderInfo> {
    const params = {
      organizationId: organizationId != null ? organizationId : ''
    };

    return this.http.get<ServiceLeaderInfo>( `${environment.getApiUrl()}infoBar/admin`, {params} );
  }

}
