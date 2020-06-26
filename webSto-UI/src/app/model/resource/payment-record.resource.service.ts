import {Injectable} from '@angular/core';
import {Autoregister, DocumentResource, Resource, Service} from 'ngx-jsonapi';
import {UserResource} from './user.resource.service';

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
