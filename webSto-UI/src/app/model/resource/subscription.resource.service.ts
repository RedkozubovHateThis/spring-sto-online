import {Injectable} from '@angular/core';
import {Autoregister, DocumentCollection, DocumentResource, IAttributes, Resource, Service} from 'ngx-jsonapi';
import {UserResource} from './user.resource.service';
import {SubscriptionTypeResource} from './subscription-type.resource.service';
import {IRelationships} from 'ngx-jsonapi/interfaces/relationship';

export interface ISubscriptionAttributes extends IAttributes {
  name: string;
  startDate: string;
  endDate: string;
  isRenewable: boolean;
}

export interface ISubscriptionRelationships extends IRelationships {
  user: DocumentResource<UserResource>;
  type: DocumentResource<SubscriptionTypeResource>;
}

export class SubscriptionResource extends Resource {
  public attributes: ISubscriptionAttributes = {
    name: null,
    startDate: null,
    endDate: null,
    isRenewable: null
  };

  public relationships: ISubscriptionRelationships = {
    user: new DocumentResource<UserResource>(),
    type: new DocumentResource<SubscriptionTypeResource>()
  };

  public isAdValid = (): boolean => {
    if ( !this.relationships.type.data )
      return false;

    if ( this.relationships.type.data.attributes.subscriptionOption !== 'AD' )
      return false;

    return this.attributes.endDate && new Date(this.attributes.endDate).getTime() > new Date().getTime();
  }
}

@Injectable()
@Autoregister()
export class SubscriptionResourceService extends Service<SubscriptionResource> {
  public resource = SubscriptionResource;
  public type = 'subscription';
  public path = 'subscriptions';
}
