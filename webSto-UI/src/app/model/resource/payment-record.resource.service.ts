import {Injectable} from '@angular/core';
import {Autoregister, DocumentResource, Resource, Service} from 'ngx-jsonapi';
import {UserResource} from './user.resource.service';
import {SubscriptionResource} from './subscription.resource.service';

export const states = [
  {id: 'CREATED', value: 'Создан'},
  {id: 'APPROVED', value: 'Подтвержден'},
  {id: 'DEPOSITED', value: 'Завершен'},
  {id: 'DECLINED', value: 'Отклонен'},
  {id: 'REVERSED', value: 'Отменен'},
  {id: 'REFUNDED', value: 'Возвращен'}
];

export const types = [
  {id: 'DEPOSIT', value: 'Внесение'},
  {id: 'PURCHASE', value: 'Списание'},
  {id: 'PROMISED', value: 'Обещанный платеж'}
];

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

  public get amount(): number {
    if ( this.attributes.amount == null ) return null;
    return this.attributes.amount / 100.0;
  }

  public get stateRus(): string {
    if ( !this.attributes.paymentState ) return '';

    const filtered = states.find( state => state.id === this.attributes.paymentState );

    if ( filtered )
      return filtered.value;

    return this.attributes.paymentState;
  }

  public get typeRus(): string {
    if ( !this.attributes.paymentType ) return '';

    const filtered = types.find( type => type.id === this.attributes.paymentType );

    if ( filtered )
      return filtered.value;

    return this.attributes.paymentType;
  }

  public relationships = {
    user: new DocumentResource<UserResource>(),
    subscription: new DocumentResource<SubscriptionResource>()
  };
}

@Injectable()
@Autoregister()
export class PaymentRecordResourceService extends Service<PaymentRecordResource> {
  public resource = PaymentRecordResource;
  public type = 'paymentRecord';
  public path = 'paymentRecords';
}
