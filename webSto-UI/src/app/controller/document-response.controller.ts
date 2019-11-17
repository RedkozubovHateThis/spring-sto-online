import {Injectable} from '@angular/core';
import {PaginationController} from './pagination.controller';
import {DocumentsFilter} from '../model/documentsFilter';
import {Pageable} from '../model/pageable';
import {DocumentResponse} from '../model/firebird/documentResponse';

@Injectable()
export class DocumentResponseController extends PaginationController {

  filter: DocumentsFilter = new DocumentsFilter();
  all: Pageable<DocumentResponse>;
  last5: Pageable<DocumentResponse>;

  constructor() {
    super();
  }

}
