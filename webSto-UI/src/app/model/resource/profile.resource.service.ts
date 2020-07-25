import {Injectable} from '@angular/core';
import {Autoregister, DocumentCollection, DocumentResource, Resource, Service} from 'ngx-jsonapi';
import {UserRoleResource} from './user-role.resource.service';
import {SubscriptionResource} from './subscription.resource.service';
import {UserResource} from './user.resource.service';

export class ProfileResource extends Resource {
  public attributes = {
    name: null,
    address: null,
    phone: null,
    email: null,
    inn: null,
    deleted: false,
    autoRegister: false,
    byFio: false,
    firstName: null,
    middleName: null,
    lastName: null
  };

  public relationships = {
    user: new DocumentResource<UserResource>()
  };
}

@Injectable()
@Autoregister()
export class ProfileResourceService extends Service<ProfileResource> {
  public resource = ProfileResource;
  public type = 'profile';
  public path = 'profiles';
}
