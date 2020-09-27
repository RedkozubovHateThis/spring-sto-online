import {Injectable} from '@angular/core';
import {PaginationController} from './pagination.controller';
import {DocumentsFilter} from '../model/documentsFilter';
import {Subject} from 'rxjs';
import {DocumentCollection} from 'ngx-jsonapi';
import {ServiceDocumentResource} from '../model/resource/service-document.resource.service';
import {PaymentRecordsFilter} from '../model/paymentRecordsFilter';
import {PaymentRecordResource} from '../model/resource/payment-record.resource.service';

@Injectable()
export class PaymentRecordController extends PaginationController {

  filter: PaymentRecordsFilter = new PaymentRecordsFilter();
  all: DocumentCollection<PaymentRecordResource>;

  constructor() {
    super();
  }

}
