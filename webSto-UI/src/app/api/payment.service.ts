import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import { UserService } from './user.service';
import {RegisterResponse} from '../model/payment/registerResponse';
import {environment} from '../../environments/environment';
import {Observable, Subscription} from 'rxjs';
import {PaymentResponse} from '../model/payment/paymentResponse';
import { SubscriptionTypeResponse } from '../model/payment/subscriptionTypeResponse';
import {SubscriptionResponse} from '../model/payment/subscriptionResponse';
import {PromisedAvailableResponse} from '../model/payment/promisedAvailableResponse';

@Injectable()
export class PaymentService {

  currentSubscription: SubscriptionResponse;
  isSubscriptionLoading: boolean = false;
  private subscription: Subscription;

  constructor(private http: HttpClient, private userService: UserService) { }

  subscribeToUserLoaded() {

    this.subscription = this.userService.currentUserIsLoaded.subscribe( currentUser => {

      if ( currentUser.userServiceLeader ) {
        this.requestCurrentSubscription();
      }

    } );

    this.userService.currentUserIsLoggedOut.subscribe( () => {
      if ( this.subscription != null )
        this.subscription.unsubscribe();
    } );
  }

  requestCurrentSubscription() {
    this.isSubscriptionLoading = true;

    this.getCurrentSubscription().subscribe( subscription => {
      this.currentSubscription = subscription;
      this.isSubscriptionLoading = false;
    }, () => {
      this.isSubscriptionLoading = false;
    } );
  }

  sendRegisterRequest(amount: number): Observable<RegisterResponse> {
    const headers = this.userService.getHeaders();

    return this.http.put<RegisterResponse>(
      `${this.userService.getApiUrl()}secured/payment/registerRequest?amount=${amount}`, {}, {headers}
      );
  }

  sendRegisterPromisedRequest(amount: number): Observable<RegisterResponse> {
    const headers = this.userService.getHeaders();

    return this.http.put<RegisterResponse>(
      `${this.userService.getApiUrl()}secured/payment/registerRequest/promised?amount=${amount}`, {}, {headers}
      );
  }

  getPromisedStatus(): Observable<PromisedAvailableResponse> {
    const headers = this.userService.getHeaders();

    return this.http.get<PromisedAvailableResponse>(
      `${this.userService.getApiUrl()}secured/payment/registerRequest/promised/isAvailable`, {headers}
      );
  }

  sendUpdateRequestExtended(orderId: string): Observable<PaymentResponse> {
    const headers = this.userService.getHeaders();

    return this.http.put<PaymentResponse>(`${this.userService.getApiUrl()}secured/payment/updateRequest/extended?orderId=${orderId}`,
      {}, {headers});
  }

  getAll(fromDate: string, toDate: string): Observable<PaymentResponse[]> {
    const headers = this.userService.getHeaders();

    return this.http.get<PaymentResponse[]>(
      `${this.userService.getApiUrl()}secured/payment/findAll?fromDate=${fromDate}&toDate=${toDate}`, {headers}
    );
  }

  getAllSubscriptionTypes(): Observable<SubscriptionTypeResponse[]> {
    const headers = this.userService.getHeaders();

    return this.http.get<SubscriptionTypeResponse[]>(
      `${this.userService.getApiUrl()}secured/payment/subscriptions/types/findAll`, {headers}
    );
  }

  getAllSubscriptions(): Observable<SubscriptionResponse[]> {
    const headers = this.userService.getHeaders();

    return this.http.get<SubscriptionResponse[]>(
      `${this.userService.getApiUrl()}secured/payment/subscriptions/findAll`, {headers}
    );
  }

  getCurrentSubscription(): Observable<SubscriptionResponse> {
    const headers = this.userService.getHeaders();

    return this.http.get<SubscriptionResponse>(
      `${this.userService.getApiUrl()}secured/payment/subscriptions/currentSubscription`, {headers}
    );
  }

  buySubscription(subscriptionType: string): Observable<SubscriptionResponse> {
    const headers = this.userService.getHeaders();
    const params = {
      subscriptionType
    };

    return this.http.put<SubscriptionResponse>(
      `${this.userService.getApiUrl()}secured/payment/subscriptions/buy`, {}, {headers, params}
    );
  }

  buySubscriptionAddon(subscriptionId: number, documentsCount: number): Observable<void> {
    const headers = this.userService.getHeaders();
    const params = {
      subscriptionId: subscriptionId != null ? subscriptionId.toString() : '',
      documentsCount: documentsCount != null ? documentsCount.toString() : ''
    };

    return this.http.put<void>(
      `${this.userService.getApiUrl()}secured/payment/subscriptions/addon/buy`, {}, {headers, params}
    );
  }

  updateRenewalSubscription(subscriptionType: string): Observable<void> {
    const headers = this.userService.getHeaders();
    const params = {
      subscriptionType
    };

    return this.http.put<void>(
      `${this.userService.getApiUrl()}secured/payment/subscriptions/updateRenewal`, {}, {headers, params}
    );
  }

}
