import { Injectable } from '@angular/core';
import { Autoregister, Service, Resource, DocumentCollection, DocumentResource } from 'ngx-jsonapi';
import {UserResource} from './user.resource.service';
import {SubscriptionTypeResource} from './subscription-type.resource.service';

export class SubscriptionResource extends Resource {
  public attributes = {
    name: null,
    startDate: null,
    endDate: null,
    isRenewable: null,
    documentCost: null,
    documentsCount: null,
    isClosedEarly: null,
  };

  public relationships = {
    user: new DocumentResource<UserResource>(),
    type: new DocumentResource<SubscriptionTypeResource>()
  };
}

@Injectable()
@Autoregister()
export class SubscriptionResourceService extends Service<SubscriptionResource> {
  public resource = SubscriptionResource;
  public type = 'subscription';
  public path = 'subscriptions';
}
