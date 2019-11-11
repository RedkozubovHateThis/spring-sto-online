import { Component, OnInit } from '@angular/core';
import {UserService} from "../../api/user.service";
import { FormsModule } from '@angular/forms';
import {ClientResponse} from "../../model/firebird/clientResponse";
import {ClientResponseService} from "../../api/clientResponse.service";
import {User} from "../../model/postgres/auth/user";
import { Router } from '@angular/router';
import {OrganizationResponseService} from "../../api/organizationResponse.service";
import {OrganizationResponse} from "../../model/firebird/organizationResponse";
import {HttpClient} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {Shops} from '../../variables/shops';

@Component({
  selector: 'app-user-profile',
  templateUrl: './../user/user.component.html',
  styleUrls: ['./../user/user.component.scss']
})
export class UserProfileComponent implements OnInit {

  private model: User;
  private clientResponse: ClientResponse;
  private organizationResponse: OrganizationResponse;
  private isADLoading: boolean = false;
  private title: string = "Профиль";
  private showBack: boolean = false;
  private shops: ShopInterface[] = [];

  constructor(private userService: UserService, private clientResponseService: ClientResponseService,
              private router: Router, private organizationResponseService: OrganizationResponseService, private httpClient: HttpClient,
              private toastrService: ToastrService) {
    this.shops = Shops.shops;
  }

  ngOnInit() {

    if ( this.userService.currentUser == null ) {

      let subscription = this.userService.currentUserIsLoaded.subscribe( () => {
        this.ngOnInit();
        subscription.unsubscribe();
      } );

      return;

    }

    this.model = this.userService.currentUser;

    if ( this.model.userClient )
      this.requestClient();
    else if ( this.model.userServiceLeader )
      this.requestOrganization();

  }

  private navigate(user: User) {
    this.userService.setTransferModel( user );
    this.router.navigate(['/users', user.id, 'edit']);
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

  containsShop(user: User, shopId: number): boolean {
    if ( user == null || user.partShops == null ) return false;
    return this.model.partShops.includes(shopId);
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

  /** Временные методы для тестирвоания СМС */

  private phoneNumber: string;
  private messageText: string;
  private smsSending = false;

  private sendSms() {
    if ( !this.phoneNumber || !this.messageText ) return;

    const headers = this.userService.getHeaders();

    const params = {
      phone: this.phoneNumber,
      message: this.messageText
    };

    this.smsSending = true;
    this.httpClient.get( `${this.userService.getApiUrl()}secured/sms/send`, { headers, params } ).subscribe( response => {
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
