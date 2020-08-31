import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {PaymentService} from '../../api/payment.service';
import {ToastrService} from 'ngx-toastr';
import {PaymentResponse} from '../../model/payment/paymentResponse';
import * as moment from 'moment';
import {UserService} from '../../api/user.service';
import {PromisedAvailableResponse} from '../../model/payment/promisedAvailableResponse';

@Component({
  selector: 'app-balance',
  templateUrl: './balance.component.html',
  styleUrls: ['./balance.component.scss']
})
export class BalanceComponent implements OnInit {

  private isLoading: boolean = false;
  private isProcessing: boolean = false;
  private isPromised: boolean = false;
  private amount: number = null;
  private promisedAmount: number = null;
  private paymentStatus: string = 'REGISTER';
  private paymentResponses: PaymentResponse[] = [];
  private promisedStatus: PromisedAvailableResponse;

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
    {id: 'PURCHASE', value: 'Списание'},
    {id: 'PROMISED', value: 'Обещанный платеж'}
  ];

  constructor(private paymentService: PaymentService, private activatedRoute: ActivatedRoute, private toastrService: ToastrService,
              private userService: UserService) { }

  ngOnInit() {
    const paymentStatus = this.activatedRoute.snapshot.queryParamMap.get('paymentStatus');
    const orderId = this.activatedRoute.snapshot.queryParamMap.get('orderId');

    if ( paymentStatus != null && orderId != null )
      this.processStatus(paymentStatus, orderId);
    else
      this.requestData();

    this.getPromisedStatus();
  }

  requestData() {
    this.isLoading = true;
    this.paymentService.getAll(this.fromDate, this.toDate)
      .subscribe( paymentResponses => {
        this.paymentResponses = paymentResponses;
        this.isLoading = false;
      } );
  }

  getPromisedStatus() {
    this.isProcessing = true;
    this.paymentService.getPromisedStatus().subscribe( promisedStatus => {
      this.promisedStatus = promisedStatus;
      this.isProcessing = false;
    }, error => {
      if ( error.error.responseText )
        this.toastrService.error(error.error.responseText, 'Внимание!');
      else
        this.toastrService.error('Ошибка запроса статуса обещанного платежа!', 'Внимание!');

      this.isProcessing = false;
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

  sendPromisedRequest(promisedAmount) {
    // if ( this.promisedAmount == null || this.promisedAmount <= 0 ) return;

    this.isProcessing = true;
    this.paymentService.sendRegisterPromisedRequest( promisedAmount * 100 ).subscribe(response => {
      this.isProcessing = false;
      this.userService.getCurrentUser();
      this.requestData();
      this.getPromisedStatus();
      this.paymentStatus = 'SUCCESS';
      this.isPromised = false;
      this.promisedAmount = null;
    }, error => {
      this.isProcessing = false;

      if ( error.error.responseText )
        this.toastrService.error(error.error.responseText, 'Внимание!');
      else
        this.toastrService.error('Ошибка регистрации запроса на оплату!', 'Внимание!');
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

  togglePromised() {
    this.isPromised = true;
  }

  get totalSum(): number {
    if ( this.promisedStatus && this.promisedStatus.availableCosts )
      return this.promisedStatus.availableCosts.reduce( (prevoius, current) => {
        return prevoius += current;
      } );
  }

}
