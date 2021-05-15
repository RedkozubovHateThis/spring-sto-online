import {Injectable} from '@angular/core';
import {Autoregister, DocumentCollection, DocumentResource, IAttributes, Resource, Service} from 'ngx-jsonapi';
import {SubscriptionResource} from './subscription.resource.service';
import {IRelationships} from 'ngx-jsonapi/interfaces/relationship';

export interface ISubscriptionTypeAttributes extends IAttributes {
  subscriptionOption: string;
  sortPosition: number;
  name: string;
  cost: number;
  durationDays: number;
}

export interface ISubscriptionTypeRelationships extends IRelationships {
  subscriptions: DocumentCollection<SubscriptionResource>;
}

export class SubscriptionTypeResource extends Resource {
  public attributes: ISubscriptionTypeAttributes = {
    subscriptionOption: null,
    sortPosition: null,
    name: null,
    cost: null,
    durationDays: null,
  };

  public relationships: ISubscriptionTypeRelationships = {
    subscriptions: new DocumentCollection<SubscriptionResource>()
  };
}

@Injectable()
@Autoregister()
export class SubscriptionTypeResourceService extends Service<SubscriptionTypeResource> {
  public resource = SubscriptionTypeResource;
  public type = 'subscriptionType';
  public path = 'subscriptionTypes';
}
