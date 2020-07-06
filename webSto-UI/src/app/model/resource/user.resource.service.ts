import {Injectable} from '@angular/core';
import {Autoregister, DocumentCollection, DocumentResource, Resource, Service} from 'ngx-jsonapi';
import {UserRoleResource} from './user-role.resource.service';
import {SubscriptionResource} from './subscription.resource.service';
import {ProfileResource} from './profile.resource.service';
import {SubscriptionTypeResource} from './subscription-type.resource.service';

export class UserResource extends Resource {
  public attributes = {
    username: null,
    rawPassword: null,
    firstName: null,
    lastName: null,
    middleName: null,
    fio: null,
    shortFio: null,
    fullFio: null,
    enabled: true,
    isAutoRegistered: false,
    serviceWorkPrice: null,
    serviceGoodsCost: null,
    balance: null,
    isCurrentSubscriptionExpired: null,
    isCurrentSubscriptionEmpty: null,
    isBalanceInvalid: null,

    userAdmin: null,
    userClient: null,
    userServiceLeader: null
  };

  public relationships = {
    roles: new DocumentCollection<UserRoleResource>(),
    currentSubscription: new DocumentResource<SubscriptionResource>(),
    profile: new DocumentResource<ProfileResource>(),
    subscriptionType: new DocumentResource<SubscriptionTypeResource>()
  };
}

@Injectable()
@Autoregister()
export class UserResourceService extends Service<UserResource> {
  public resource = UserResource;
  public type = 'user';
  public path = 'users';
}
