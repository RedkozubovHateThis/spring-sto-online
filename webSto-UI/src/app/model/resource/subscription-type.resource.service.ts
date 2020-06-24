import { Injectable } from '@angular/core';
import { Autoregister, Service, Resource, DocumentCollection, DocumentResource } from 'ngx-jsonapi';
import {SubscriptionResource} from './subscription.resource.service';

export class SubscriptionTypeResource extends Resource {
  public attributes = {
    sortPosition: null,
    name: null,
    isFree: null,
    cost: null,
    documentCost: null,
    documentsCount: null,
    durationDays: null,
  };

  public relationships = {
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
