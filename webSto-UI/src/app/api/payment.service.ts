import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {UserService} from './user.service';
import {RegisterResponse} from '../model/payment/registerResponse';
import {Observable, Subscription} from 'rxjs';
import {PaymentResponse} from '../model/payment/paymentResponse';
import {SubscriptionResponse} from '../model/payment/subscriptionResponse';
import {PromisedAvailableResponse} from '../model/payment/promisedAvailableResponse';
import {SubscriptionTypeResource, SubscriptionTypeResourceService} from '../model/resource/subscription-type.resource.service';
import {SubscriptionResource, SubscriptionResourceService} from '../model/resource/subscription.resource.service';
import {DocumentCollection} from 'ngx-jsonapi';
import {environment} from '../../environments/environment';
import {IDocumentResource} from 'ngx-jsonapi/interfaces/data-object';
import {PaymentRecordResource, PaymentRecordResourceService} from '../model/resource/payment-record.resource.service';
import {IDataCollection} from 'ngx-jsonapi/interfaces/data-collection';
import {PaymentRecordsFilter} from '../model/paymentRecordsFilter';

@Injectable()
export class PaymentService {

  currentSubscription: SubscriptionResponse;
  isSubscriptionLoading: boolean = false;
  private subscription: Subscription;

  constructor(private http: HttpClient, private userService: UserService, private subscriptionResourceService: SubscriptionResourceService,
              private subscriptionTypeResourceService: SubscriptionTypeResourceService,
              private paymentRecordResourceService: PaymentRecordResourceService) {
    subscriptionResourceService.register();
    subscriptionTypeResourceService.register();
  }

  sendRegisterRequest(amount: number): Observable<RegisterResponse> {
    return this.http.put<RegisterResponse>(
      `${environment.getApiUrl()}payment/registerRequest?amount=${amount}`, {});
  }

  sendRegisterPromisedRequest(amount: number): Observable<RegisterResponse> {
    return this.http.put<RegisterResponse>(
      `${environment.getApiUrl()}payment/registerRequest/promised?amount=${amount}`, {});
  }

  getPromisedStatus(): Observable<PromisedAvailableResponse> {
    return this.http.get<PromisedAvailableResponse>(
      `${environment.getApiUrl()}payment/registerRequest/promised/isAvailable`);
  }

  sendUpdateRequestExtended(orderId: string): Observable<PaymentResponse> {
    return this.http.put<PaymentResponse>(`${environment.getApiUrl()}payment/updateRequest/extended?orderId=${orderId}`,
      {});
  }

  getAll(fromDate: string, toDate: string): Observable<DocumentCollection<PaymentRecordResource>> {
    return new Observable<DocumentCollection<PaymentRecordResource>>( (subscriber) => {
      this.http.get<IDataCollection>(
        `${environment.getApiUrl()}payment/paymentRecords?fromDate=${fromDate}&toDate=${toDate}`
      )
        .subscribe( (raw: IDataCollection) => {
          const collection: DocumentCollection<PaymentRecordResource> = this.paymentRecordResourceService.newCollection();
          collection.fill(raw);
          subscriber.next( collection );
          subscriber.complete();
        }, (error) => {
          subscriber.error( error );
          subscriber.complete();
        } );
    } );
  }

  filter(filter: PaymentRecordsFilter): Observable<DocumentCollection<PaymentRecordResource>> {
    const params = {
      userId: filter.userId != null ? filter.userId : '',
      paymentType: filter.paymentType != null ? filter.paymentType : '',
      paymentState: filter.paymentState != null ? filter.paymentState : '',
      fromDate: filter.fromDate != null ? filter.fromDate : '',
      toDate: filter.toDate != null ? filter.toDate : '',
      sort: `${filter.sort},${filter.direction}`,
      page: (filter.page - 1).toString(),
      size: filter.size.toString(),
      offset: filter.offset.toString()
    };

    return new Observable<DocumentCollection<PaymentRecordResource>>( (subscriber) => {
      this.http.get<IDataCollection>(
        `${environment.getApiUrl()}payment/paymentRecords/search/filter`, { params }
      )
        .subscribe( (raw: IDataCollection) => {
          const collection: DocumentCollection<PaymentRecordResource> = this.paymentRecordResourceService.newCollection();
          collection.fill(raw);
          subscriber.next( collection );
          subscriber.complete();
        }, (error) => {
          subscriber.error( error );
          subscriber.complete();
        } );
    } );
  }

  getAllSubscriptionTypes(): Observable<DocumentCollection<SubscriptionTypeResource>> {
    return this.subscriptionTypeResourceService.all({
      beforepath: `${environment.getBeforeUrl()}/payment`
    });
  }

  getAllSubscriptions(): Observable<DocumentCollection<SubscriptionResource>> {
    return this.subscriptionResourceService.all({
      beforepath: `${environment.getBeforeUrl()}/payment`
    });
  }

  buySubscription(subscriptionTypeId: string): Observable<SubscriptionResource> {
    const params = {
      subscriptionTypeId: subscriptionTypeId != null ? subscriptionTypeId.toString() : ''
    };

    return new Observable<SubscriptionResource>( (subscriber) => {
      this.http.put<IDocumentResource>(
        `${environment.getApiUrl()}payment/subscriptions/buy`, {}, {params}
      )
        .subscribe( (raw: IDocumentResource) => {
          const subscription: SubscriptionResource = this.subscriptionResourceService.new();
          subscription.fill(raw);
          subscriber.next( subscription );
          subscriber.complete();
        }, (error) => {
          subscriber.error( error );
          subscriber.complete();
        } );
    } );
  }

  unsubscribeSubscription(subscriptionId: string): Observable<void> {
    const params = {
      subscriptionId: subscriptionId != null ? subscriptionId : ''
    };

    return this.http.put<void>(
      `${environment.getApiUrl()}payment/subscriptions/unsubscribe`, {}, {params}
    );
  }

  updateSubscription(subscriptionType: SubscriptionTypeResource): Observable<object> {
    return subscriptionType.save({ beforepath: environment.getBeforeUrl() });
  }

}
