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

@Injectable()
export class PaymentService {

  currentSubscription: SubscriptionResponse;
  isSubscriptionLoading: boolean = false;
  private subscription: Subscription;

  constructor(private http: HttpClient, private userService: UserService, private subscriptionResourceService: SubscriptionResourceService,
              private subscriptionTypeResourceService: SubscriptionTypeResourceService) {
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

  getAll(fromDate: string, toDate: string): Observable<PaymentResponse[]> {
    return this.http.get<PaymentResponse[]>(
      `${environment.getApiUrl()}payment/findAll?fromDate=${fromDate}&toDate=${toDate}`
    );
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

  buySubscription(subscriptionTypeId: string): Observable<SubscriptionResponse> {
    const params = {
      subscriptionTypeId: subscriptionTypeId != null ? subscriptionTypeId.toString() : ''
    };

    return this.http.put<SubscriptionResponse>(
      `${environment.getApiUrl()}payment/subscriptions/buy`, {}, {params}
    );
  }

  giftSubscription(serviceLeaderId: string): Observable<SubscriptionResponse> {
    const params = {
      serviceLeaderId: serviceLeaderId != null ? serviceLeaderId : ''
    };

    return this.http.put<SubscriptionResponse>(
      `${environment.getApiUrl()}payment/subscriptions/gift`, {}, {params}
    );
  }

  buySubscriptionAddon(subscriptionId: string, documentsCount: number): Observable<void> {
    const params = {
      subscriptionId: subscriptionId != null ? subscriptionId : '',
      documentsCount: documentsCount != null ? documentsCount.toString() : ''
    };

    return this.http.put<void>(
      `${environment.getApiUrl()}payment/subscriptions/addon/buy`, {}, {params}
    );
  }

  updateRenewalSubscription(subscriptionTypeId: string): Observable<void> {
    const params = {
      subscriptionTypeId: subscriptionTypeId != null ? subscriptionTypeId : ''
    };

    return this.http.put<void>(
      `${environment.getApiUrl()}payment/subscriptions/updateRenewal`, {}, {params}
    );
  }

  updateSubscription(subscriptionType: SubscriptionTypeResource): Observable<object> {
    return subscriptionType.save({ beforepath: environment.getBeforeUrl() });
  }

}
