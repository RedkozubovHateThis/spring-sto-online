import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {UserService} from '../../api/user.service';
import {User} from '../../model/postgres/auth/user';
import {ModelTransfer} from '../model.transfer';
import {Location} from '@angular/common';
import {Shops} from '../../variables/shops';
import {PaymentService} from '../../api/payment.service';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent extends ModelTransfer<User, number> implements OnInit {

  private isLoading: boolean = false;
  private title: string = "Данные пользователя";
  private showBack: boolean = true;
  private shops: ShopInterface[] = [];

  constructor(private userService: UserService, protected route: ActivatedRoute, private location: Location,
              private router: Router, private toastrService: ToastrService,
              private paymentService: PaymentService) {
    super(userService, route);
    this.shops = Shops.shops;
  }

  requestData() {
    this.isLoading = true;
    this.userService.getOne(this.id).subscribe( data => {
      this.model = data as User;
      this.isLoading = false;
    }, error => {
      this.isLoading = false;
    } );
  }

  onTransferComplete() {
  }

  private navigate(user: User) {
    this.userService.setTransferModel( user );
    this.router.navigate(['/users', user.id, 'edit']);
  }

  private giftSubscription() {
    this.isLoading = true;

    this.paymentService.giftSubscription(this.model.id).subscribe( subscriptionResponse => {
      this.isLoading = false;
      this.toastrService.success('Тариф успешно выдан!');
    }, error => {
      this.isLoading = false;

      if ( error.error.responseText )
        this.toastrService.error(error.error.responseText, 'Внимание!');
      else
        this.toastrService.error('Ошибка выдачи тарифа!', 'Внимание!');
    } );
  }

}
