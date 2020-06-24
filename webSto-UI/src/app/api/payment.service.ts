import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {UserService} from './user.service';
import {RegisterResponse} from '../model/payment/registerResponse';
import {Observable, Subscription} from 'rxjs';
import {PaymentResponse} from '../model/payment/paymentResponse';
import {SubscriptionTypeResponse} from '../model/payment/subscriptionTypeResponse';
import {SubscriptionResponse} from '../model/payment/subscriptionResponse';
import {PromisedAvailableResponse} from '../model/payment/promisedAvailableResponse';

@Injectable()
export class PaymentService {

  currentSubscription: SubscriptionResponse;
  isSubscriptionLoading: boolean = false;
  private subscription: Subscription;

  constructor(private http: HttpClient, private userService: UserService) { }

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

  getAllSubscriptionTypes(): Observable<SubscriptionTypeResponse[]> {
    const headers = this.userService.getHeaders();

    return this.http.get<SubscriptionTypeResponse[]>(
      `${this.userService.getApiUrl()}payment/subscriptions/types/findAll`, {headers}
    );
  }

  getAllSubscriptions(): Observable<SubscriptionResponse[]> {
    const headers = this.userService.getHeaders();

    return this.http.get<SubscriptionResponse[]>(
      `${this.userService.getApiUrl()}payment/subscriptions/findAll`, {headers}
    );
  }

  getCurrentSubscription(): Observable<SubscriptionResponse> {
    const headers = this.userService.getHeaders();

    return this.http.get<SubscriptionResponse>(
      `${this.userService.getApiUrl()}payment/subscriptions/currentSubscription`, {headers}
    );
  }

  buySubscription(subscriptionTypeId: number): Observable<SubscriptionResponse> {
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

  buySubscriptionAddon(subscriptionId: number, documentsCount: number): Observable<void> {
    const headers = this.userService.getHeaders();
    const params = {
      subscriptionId: subscriptionId != null ? subscriptionId.toString() : '',
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

  updateSubscription(subscriptionType: SubscriptionTypeResponse): Observable<void> {
    const headers = this.userService.getHeaders();

    return this.http.post<void>(
      `${this.userService.getApiUrl()}payment/subscriptions/types/update`, subscriptionType, {headers}
    );
  }

}
