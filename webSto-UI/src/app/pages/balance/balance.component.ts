import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {PaymentService} from '../../api/payment.service';
import {ToastrService} from 'ngx-toastr';
import {PaymentResponse} from '../../model/payment/paymentResponse';
import * as moment from 'moment';

@Component({
  selector: 'app-payment',
  templateUrl: './balance.component.html',
  styleUrls: ['./balance.component.scss']
})
export class BalanceComponent implements OnInit {

  private isLoading: boolean = false;
  private isProcessing: boolean = false;
  private amount: number = null;
  private paymentStatus: string = 'REGISTER';
  private paymentResponses: PaymentResponse[] = [];

  private datePickerConfig = {
    locale: 'ru',
    firstDayOfWeek: 'mo',
    // showGoToCurrent: true,
    format: 'DD.MM.YYYY',
    // monthFormat: 'MM, YYYY'
  };
  private fromDate: string = moment().startOf('month').format('DD.MM.YYYY');
  private toDate: string = moment().endOf('month').format('DD.MM.YYYY');

  private states = [
    {id: 'CREATED', value: 'Создан'},
    {id: 'APPROVED', value: 'Подтвержден'},
    {id: 'DEPOSITED', value: 'Завершен'},
    {id: 'DECLINED', value: 'Отклонен'},
    {id: 'REVERSED', value: 'Отменен'},
    {id: 'REFUNDED', value: 'Возвращен'}
  ];

  private types = [
    {id: 'DEPOSIT', value: 'Внесение'},
    {id: 'PURCHASE', value: 'Списание'}
  ];

  constructor(private paymentService: PaymentService, private activatedRoute: ActivatedRoute, private toastrService: ToastrService) { }

  ngOnInit() {
    const paymentStatus = this.activatedRoute.snapshot.queryParamMap.get('paymentStatus');
    const orderId = this.activatedRoute.snapshot.queryParamMap.get('orderId');

    if ( paymentStatus != null && orderId != null )
      this.processStatus(paymentStatus, orderId);
    else
      this.requestData();
  }

  requestData() {
    this.isLoading = true;
    this.paymentService.getAll(this.fromDate, this.toDate)
      .subscribe( paymentResponses => {
        this.paymentResponses = paymentResponses;
        this.isLoading = false;
      } );
  }

  setFromDate(e) {
    this.fromDate = e;
    this.requestData();
  }

  setToDate(e) {
    this.toDate = e;
    this.requestData();
  }

  sendRequest() {
    if ( this.amount == null || this.amount <= 0 ) return;

    this.isProcessing = true;
    this.paymentService.sendRegisterRequest( this.amount * 100 ).subscribe(response => {
      this.isProcessing = false;
      window.location.href = response.formUrl;
    }, error => {
      if ( error.error.responseText )
        this.toastrService.error(error.error.responseText, 'Внимание!');
      else
        this.toastrService.error('Ошибка регистрации запроса на оплату!', 'Внимание!');

      this.isProcessing = false;
    } );
  }

  processStatus(paymentStatus: string, orderId: string) {
    this.paymentStatus = paymentStatus;
    this.sendUpdateRequestExtended(orderId);
  }

  sendUpdateRequestExtended(orderId: string) {
    this.paymentService.sendUpdateRequestExtended( orderId ).subscribe( paymentResponse => {
      this.isProcessing = false;
      this.toastrService.success('Запрос на оплату успешно обработан!');
      this.requestData();
    }, error => {
      if ( error.error.responseText )
        this.toastrService.error(error.error.responseText, 'Внимание!');
      else
        this.toastrService.error('Ошибка обработки запроса на оплату!', 'Внимание!');
      this.isProcessing = false;
      this.requestData();
    } );
  }

  getStateRus(paymentState: string) {
    if ( !paymentState ) return '';

    const filtered = this.states.find( state => state.id === paymentState );

    if ( filtered )
      return filtered.value;

    return paymentState;
  }

  getTypeRus(paymentType: string) {
    if ( !paymentType ) return '';

    const filtered = this.types.find( type => type.id === paymentType );

    if ( filtered )
      return filtered.value;

    return paymentType;
  }

}
