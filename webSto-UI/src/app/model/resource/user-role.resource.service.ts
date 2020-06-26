import {Injectable} from '@angular/core';
import {Autoregister, DocumentCollection, Resource, Service} from 'ngx-jsonapi';
import {UserResource} from './user.resource.service';

export class UserRoleResource extends Resource {
  public attributes = {
    name: null,
    nameRus: null
  };

  public relationships = {
    users: new DocumentCollection<UserResource>()
  };
}

@Injectable()
@Autoregister()
export class UserRoleResourceService extends Service<UserRoleResource> {
  public resource = UserRoleResource;
  public type = 'userRole';
  public path = 'userRoles';
}
