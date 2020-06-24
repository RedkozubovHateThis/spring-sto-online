import { Injectable } from '@angular/core';
import { Autoregister, Service, Resource, DocumentCollection, DocumentResource } from 'ngx-jsonapi';
import {UserResource} from './user.resource.service';
import {SubscriptionTypeResource} from './subscription-type.resource.service';
import {SubscriptionResource} from './subscription.resource.service';

export class EventMessageResource extends Resource {
  public attributes = {
    documentId: null,
    documentName: null,
    messageType: null,
    additionalInformation: null,
    messageDate: null
  };

  public relationships = {
    sendUser: new DocumentResource<UserResource>(),
    targetUser: new DocumentResource<UserResource>()
  };
}

@Injectable()
@Autoregister()
export class EventMessageResourceService extends Service<EventMessageResource> {
  public resource = EventMessageResource;
  public type = 'eventMessage';
  public path = 'eventMessages';
}
