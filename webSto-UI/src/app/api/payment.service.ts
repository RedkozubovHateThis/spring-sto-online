import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {UserService} from './user.service';
import {RegisterResponse} from '../model/payment/registerResponse';
import {Observable, Subscription} from 'rxjs';
import {PaymentResponse} from '../model/payment/paymentResponse';
import {SubscriptionTypeResponse} from '../model/payment/subscriptionTypeResponse';
import {SubscriptionResponse} from '../model/payment/subscriptionResponse';
import {PromisedAvailableResponse} from '../model/payment/promisedAvailableResponse';
import {SubscriptionTypeResource, SubscriptionTypeResourceService} from '../model/resource/subscription-type.resource.service';
import {SubscriptionResource, SubscriptionResourceService} from '../model/resource/subscription.resource.service';
import {DocumentCollection} from 'ngx-jsonapi';

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
    const headers = this.userService.getHeaders();

    return this.http.put<RegisterResponse>(
      `${this.userService.getApiUrl()}payment/registerRequest?amount=${amount}`, {}, {headers}
      );
  }

  sendRegisterPromisedRequest(amount: number): Observable<RegisterResponse> {
    const headers = this.userService.getHeaders();

    return this.http.put<RegisterResponse>(
      `${this.userService.getApiUrl()}payment/registerRequest/promised?amount=${amount}`, {}, {headers}
      );
  }

  getPromisedStatus(): Observable<PromisedAvailableResponse> {
    const headers = this.userService.getHeaders();

    return this.http.get<PromisedAvailableResponse>(
      `${this.userService.getApiUrl()}payment/registerRequest/promised/isAvailable`, {headers}
      );
  }

  sendUpdateRequestExtended(orderId: string): Observable<PaymentResponse> {
    const headers = this.userService.getHeaders();

    return this.http.put<PaymentResponse>(`${this.userService.getApiUrl()}payment/updateRequest/extended?orderId=${orderId}`,
      {}, {headers});
  }

  getAll(fromDate: string, toDate: string): Observable<PaymentResponse[]> {
    const headers = this.userService.getHeaders();

    return this.http.get<PaymentResponse[]>(
      `${this.userService.getApiUrl()}payment/findAll?fromDate=${fromDate}&toDate=${toDate}`, {headers}
    );
  }

  getAllSubscriptionTypes(): Observable<DocumentCollection<SubscriptionTypeResource>> {
    return this.subscriptionTypeResourceService.all({
      beforepath: 'payment'
    });
  }

  getAllSubscriptions(): Observable<DocumentCollection<SubscriptionResource>> {
    return this.subscriptionResourceService.all({
      beforepath: 'payment'
    });
  }

  buySubscription(subscriptionTypeId: string): Observable<SubscriptionResponse> {
    const headers = this.userService.getHeaders();
    const params = {
      subscriptionTypeId: subscriptionTypeId != null ? subscriptionTypeId.toString() : ''
    };

    return this.http.put<SubscriptionResponse>(
      `${this.userService.getApiUrl()}payment/subscriptions/buy`, {}, {headers, params}
    );
  }

  giftSubscription(serviceLeaderId: string): Observable<SubscriptionResponse> {
    const headers = this.userService.getHeaders();
    const params = {
      serviceLeaderId: serviceLeaderId != null ? serviceLeaderId : ''
    };

    return this.http.put<SubscriptionResponse>(
      `${this.userService.getApiUrl()}payment/subscriptions/gift`, {}, {headers, params}
    );
  }

  buySubscriptionAddon(subscriptionId: string, documentsCount: number): Observable<void> {
    const headers = this.userService.getHeaders();
    const params = {
      subscriptionId: subscriptionId != null ? subscriptionId : '',
      documentsCount: documentsCount != null ? documentsCount.toString() : ''
    };

    return this.http.put<void>(
      `${this.userService.getApiUrl()}payment/subscriptions/addon/buy`, {}, {headers, params}
    );
  }

  updateRenewalSubscription(subscriptionTypeId: string): Observable<void> {
    const headers = this.userService.getHeaders();
    const params = {
      subscriptionTypeId: subscriptionTypeId != null ? subscriptionTypeId : ''
    };

    return this.http.put<void>(
      `${this.userService.getApiUrl()}payment/subscriptions/updateRenewal`, {}, {headers, params}
    );
  }

  updateSubscription(subscriptionType: SubscriptionTypeResource): Observable<object> {
    const headers = this.userService.getHeaders();

    return subscriptionType.save();
  }

}
