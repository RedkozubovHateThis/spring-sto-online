import { Injectable } from '@angular/core';
import { Autoregister, Service, Resource, DocumentCollection, DocumentResource } from 'ngx-jsonapi';
import {UserResource} from './user.resource.service';
import {SubscriptionTypeResource} from './subscription-type.resource.service';
import {SubscriptionResource} from './subscription.resource.service';

export class PaymentRecordResource extends Resource {
  public attributes = {
    paymentType: null,
    orderId: null,
    orderNumber: null,
    orderStatus: null,
    actionCode: null,
    actionCodeDescription: null,
    errorCode: null,
    errorMessage: null,
    currency: null,
    amount: null,
    depositedAmount: null,
    paymentState: null,
    createDate: null,
    registerDate: null,
    expirationDate: null,
    isExpired: null,
    ipAddress: null,
    maskedPan: null
  };

  public relationships = {
    user: new DocumentResource<UserResource>()
  };
}

@Injectable()
@Autoregister()
export class PaymentRecordResourceService extends Service<PaymentRecordResource> {
  public resource = PaymentRecordResource;
  public type = 'paymentRecord';
  public path = 'paymentRecords';
}
