import {Injectable} from '@angular/core';
import {Autoregister, DocumentCollection, DocumentResource, Resource, Service} from 'ngx-jsonapi';
import {UserRoleResource} from './user-role.resource.service';
import {SubscriptionResource} from './subscription.resource.service';

export class UserResource extends Resource {
  public attributes = {
    username: null,
    password: null,
    firstName: null,
    lastName: null,
    middleName: null,
    fio: null,
    phone: null,
    email: null,
    enabled: null,
    isAutoRegistered: null,
    inn: null,
    vin: null,
    serviceWorkPrice: null,
    serviceGoodsCost: null,
    balance: null,
    isCurrentSubscriptionExpired: null,
    isCurrentSubscriptionEmpty: null,
    isBalanceInvalid: null,
    subscriptionTypeId: null,
    vinNumbers: [],

    userAdmin: null,
    userClient: null,
    userServiceLeader: null
  };

  public relationships = {
    roles: new DocumentCollection<UserRoleResource>(),
    currentSubscription: new DocumentResource<SubscriptionResource>()
  };
}

@Injectable()
@Autoregister()
export class UserResourceService extends Service<UserResource> {
  public resource = UserResource;
  public type = 'user';
  public path = 'users';
}
