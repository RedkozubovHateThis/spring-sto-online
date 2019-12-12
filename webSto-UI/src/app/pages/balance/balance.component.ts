import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {PaymentService} from '../../api/payment.service';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-payment',
  templateUrl: './balance.component.html',
  styleUrls: ['./balance.component.scss']
})
export class BalanceComponent implements OnInit {

  private isLoading: boolean = false;
  private amount: number = null;
  private paymentStatus: string = 'REGISTER';

  constructor(private paymentService: PaymentService, private activatedRoute: ActivatedRoute, private toastrService: ToastrService) { }

  ngOnInit() {
    const paymentStatus = this.activatedRoute.snapshot.queryParamMap.get('paymentStatus');
    const orderId = this.activatedRoute.snapshot.queryParamMap.get('orderId');

    if ( paymentStatus != null && orderId != null )
      this.processStatus(paymentStatus, orderId);
  }

  sendRequest() {
    if ( this.amount == null || this.amount <= 0 ) return;

    this.isLoading = true;
    this.paymentService.sendRegisterRequest( this.amount * 100 ).subscribe(response => {
      this.isLoading = false;
      window.location.href = response.formUrl;
    }, error => {
      if ( error.error.responseText )
        this.toastrService.error(error.error.responseText, 'Внимание!');
      else
        this.toastrService.error('Ошибка регистрации запроса на оплату!', 'Внимание!');

      this.isLoading = false;
    } );
  }

  processStatus(paymentStatus: string, orderId: string) {
    console.log(paymentStatus, orderId);

    this.paymentStatus = paymentStatus;
    this.sendUpdateRequestExtended(orderId);
  }

  sendUpdateRequestExtended(orderId: string) {
    this.paymentService.sendUpdateRequestExtended( orderId ).subscribe( paymentResponse => {
      this.isLoading = false;
      this.toastrService.success('Запрос на оплату успешно обработан!');
    }, error => {
      if ( error.error.responseText )
        this.toastrService.error(error.error.responseText, 'Внимание!');
      else
        this.toastrService.error('Ошибка обработки запроса на оплату!', 'Внимание!');
      this.isLoading = false;
    } );
  }

}
