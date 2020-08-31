import {Injectable} from '@angular/core';
import {Autoregister, DocumentCollection, DocumentResource, Resource, Service} from 'ngx-jsonapi';
import {UserRoleResource} from './user-role.resource.service';
import {SubscriptionResource} from './subscription.resource.service';
import {ProfileResource} from './profile.resource.service';
import {AdEntityResource} from './ad-entity.resource.service';

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
    isCurrentAdSubscriptionExpired: null,
    isCurrentAdSubscriptionEmpty: null,
    isCurrentOperatorSubscriptionExpired: null,
    isCurrentOperatorSubscriptionEmpty: null,
    isBalanceInvalid: null,

    bankBic: null,
    bankName: null,
    checkingAccount: null,
    corrAccount: null
  };

  public isServiceLeader = (): boolean => {
    return this.hasRole('SERVICE_LEADER');
  };
  public isServiceLeaderOrFreelancer = (): boolean => {
    return this.hasRole('SERVICE_LEADER') || this.hasRole('FREELANCER');
  };
  public isAdmin = (): boolean => {
    return this.hasRole('ADMIN');
  };
  public isClient = (): boolean => {
    return this.hasRole('CLIENT');
  };

  public hasRole = (roleName: string): boolean => {
    if ( !this.relationships.roles.data || !this.relationships.roles.data.length )
      return false;

    const role: UserRoleResource =
      this.relationships.roles.data.find( (eachRole: UserRoleResource) => eachRole.attributes.name === roleName );

    return typeof role !== 'undefined' && role != null;
  }

  public getTitle = (): string => {
      if ( this.attributes.fio != null )
        return this.attributes.fio;
      else if ( this.relationships.profile.data ) {
        const profile: ProfileResource = this.relationships.profile.data;

        if ( profile.attributes.name )
          return profile.attributes.name;
        else if ( profile.attributes.phone )
          return profile.attributes.phone;
        else if ( profile.attributes.email )
          return profile.attributes.email;
      }
      else
        return this.attributes.username;
  }

  public isAdSubscriptionActive = (): boolean => {
      if ( !this.relationships.currentAdSubscription.data )
        return false;

      return this.relationships.currentAdSubscription.data.isAdValid();
  }

  public relationships = {
    roles: new DocumentCollection<UserRoleResource>(),
    currentAdSubscription: new DocumentResource<SubscriptionResource>(),
    currentOperatorSubscription: new DocumentResource<SubscriptionResource>(),
    allSubscriptions: new DocumentCollection<SubscriptionResource>(),
    profile: new DocumentResource<ProfileResource>(),
    adEntity: new DocumentResource<AdEntityResource>()
  };
}

@Injectable()
@Autoregister()
export class UserResourceService extends Service<UserResource> {
  public resource = UserResource;
  public type = 'user';
  public path = 'users';
}
