import {Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Pagination} from '../pagination';
import {UserService} from '../../api/user.service';
import {DocumentController} from '../../controller/document.controller';
import {ServiceDocumentResource} from '../../model/resource/service-document.resource.service';
import {DocumentService} from '../../api/document-service.service';
import {PaymentService} from '../../api/payment.service';
import {PaymentRecordController} from '../../controller/payment-record.controller';
import {UserResource} from '../../model/resource/user.resource.service';

@Component({
  selector: 'app-payment-records',
  templateUrl: './payment-records.component.html',
  styleUrls: ['./payment-records.component.scss']
})
export class PaymentRecordsComponent extends Pagination {

  private isLoading: boolean = false;
  protected routeName = '/payment-records';

  constructor(protected route: ActivatedRoute, protected router: Router, private paymentService: PaymentService,
              private userService: UserService, protected paymentRecordController: PaymentRecordController) {
    super(route, router, paymentRecordController);
  }

  requestData() {
    this.isLoading = true;
    this.paymentService.filter(this.paymentRecordController.filter).subscribe(data => {
      this.paymentRecordController.all = data;

      this.isLoading = false;
    }, error => {
      this.isLoading = false;
    } );
  }

}
