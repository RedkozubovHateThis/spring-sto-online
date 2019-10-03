import { Component, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import {UserService} from '../../api/user.service';
import {User} from '../../model/postgres/auth/user';
import {ModelTransfer} from '../model.transfer';
import {ClientResponse} from '../../model/firebird/clientResponse';
import {ClientResponseService} from '../../api/clientResponse.service';
import {DocumentResponse} from '../../model/firebird/documentResponse';
import {OrganizationResponse} from '../../model/firebird/organizationResponse';
import {OrganizationResponseService} from '../../api/organizationResponse.service';
import {HttpClient, HttpParams} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {error} from 'util';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent extends ModelTransfer<User, number> implements OnInit {

  private isLoading: boolean = false;
  private clientResponse: ClientResponse;
  private organizationResponse: OrganizationResponse;
  private isADLoading: boolean = false;
  private title: string = "Данные пользователя";

  constructor(private userService: UserService, protected route: ActivatedRoute,
              private clientResponseService: ClientResponseService, private router: Router,
              private organizationResponseService: OrganizationResponseService, private httpClient: HttpClient,
              private toastrService: ToastrService) {
    super(userService, route);
  }

  requestData() {
    this.isLoading = true;
    this.userService.getOne(this.id).subscribe( data => {
      this.model = data as User;
      this.isLoading = false;

      if ( this.model.client )
        this.requestClient();
      else if ( this.model.serviceLeader )
        this.requestOrganization();

    }, error => {
      this.isLoading = false;
    } );
  }

  requestClient() {
    if ( this.model.clientId == null ) return;

    this.isADLoading = true;

    this.clientResponseService.getOne(this.model.clientId).subscribe( data => {
      this.clientResponse = data as ClientResponse;
      this.isADLoading = false;
    }, () => {
      this.isADLoading = false;
    } );
  }

  requestOrganization() {
    if ( this.model.organizationId == null ) return;

    this.isADLoading = true;

    this.organizationResponseService.getOne(this.model.organizationId).subscribe( data => {
      this.organizationResponse = data as OrganizationResponse;
      this.isADLoading = false;
    }, () => {
      this.isADLoading = false;
    } );
  }

  onTransferComplete() {
    if ( this.model.client )
      this.requestClient();
    else if ( this.model.serviceLeader )
      this.requestOrganization();
  }

  private navigate(user: User) {
    this.userService.setTransferModel( user );
    this.router.navigate(['/users', user.id, 'edit']);
  }

  private approve(user: User, approved: boolean) {
    user.isApproved = approved;
    this.userService.saveUser(user);
  }

  /** Временные методы для тестирвоания СМС */

  private phoneNumber: string;
  private messageText: string;
  private smsSending = false;
  private baseUrl = 'http://localhost:8181/';

  private sendSms() {
    if ( !this.phoneNumber || !this.messageText ) return;

    const headers = this.userService.getHeaders();

    const params = {
      phone: this.phoneNumber,
      message: this.messageText
    };

    this.smsSending = true;
    this.httpClient.get( `${this.baseUrl}secured/sms/send`, { headers, params } ).subscribe( response => {
      this.smsSending = false;
      // @ts-ignore
      if ( response.error ) {
        // @ts-ignore
        this.toastrService.error(response.error, 'Внимание!');
      } else {
        // @ts-ignore
        const successMessage = `Сообщение "${this.messageText}" успешно отправлено абоненту "${this.phoneNumber}".\nТекущий баланс: ${response.balance}`;
        this.toastrService.success(successMessage);
      }
    }, error => {
      this.smsSending = false;
      this.toastrService.error('Ошибка отправки СМС!', 'Внимание!');
    } );
  }

}
