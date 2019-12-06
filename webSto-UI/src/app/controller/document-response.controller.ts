import {Injectable} from '@angular/core';
import {PaginationController} from './pagination.controller';
import {DocumentsFilter} from '../model/documentsFilter';
import {Pageable} from '../model/pageable';
import {DocumentResponse} from '../model/firebird/documentResponse';
import {Subject} from 'rxjs';

@Injectable()
export class DocumentResponseController extends PaginationController {

  filter: DocumentsFilter = new DocumentsFilter();
  all: Pageable<DocumentResponse>;
  last5: Pageable<DocumentResponse>;

  organizationChange: Subject<void> = new Subject<void>();

  constructor() {
    super();
  }

  emitOrganizationChange() {
    this.organizationChange.next();
  }

}
