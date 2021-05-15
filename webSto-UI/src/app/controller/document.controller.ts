import {Injectable} from '@angular/core';
import {PaginationController} from './pagination.controller';
import {DocumentsFilter} from '../model/documentsFilter';
import {Subject} from 'rxjs';
import {DocumentCollection} from 'ngx-jsonapi';
import {ServiceDocumentResource} from '../model/resource/service-document.resource.service';

@Injectable()
export class DocumentController extends PaginationController {

  filter: DocumentsFilter = new DocumentsFilter();
  all: DocumentCollection<ServiceDocumentResource>;

  organizationChange: Subject<void> = new Subject<void>();

  constructor() {
    super();
  }

  emitOrganizationChange() {
    this.organizationChange.next();
  }

}
