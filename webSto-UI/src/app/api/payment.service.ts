import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import { UserService } from './user.service';
import {RegisterResponse} from '../model/payment/registerResponse';
import {environment} from '../../environments/environment';
import {Observable} from 'rxjs';
import {PaymentResponse} from '../model/payment/paymentResponse';

@Injectable()
export class PaymentService {

  constructor(private http: HttpClient, private userService: UserService) { }

  sendRegisterRequest(amount: number): Observable<RegisterResponse> {
    const headers = this.userService.getHeaders();

    return this.http.put<RegisterResponse>(
      `${this.userService.getApiUrl()}secured/payment/registerRequest?amount=${amount}`, {}, {headers}
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

}
