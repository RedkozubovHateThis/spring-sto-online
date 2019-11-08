import { Component } from '@angular/core';
import {DocumentResponse} from '../../model/firebird/documentResponse';
import {DocumentResponseService} from '../../api/documentResponse.service';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {Pageable} from '../../model/pageable';
import {Pagination} from '../pagination';
import { DocumentsFilter } from 'src/app/model/documentsFilter';
import {UserService} from '../../api/user.service';

@Component({
  selector: 'app-tables',
  templateUrl: './documents.component.html',
  styleUrls: ['./documents.component.scss']
})
export class DocumentsComponent extends Pagination {

  private all: Pageable<DocumentResponse>;
  private isLoading: boolean = false;
  protected filter: DocumentsFilter = new DocumentsFilter();
  private selected: DocumentResponse;
  protected routeName = '/documents';

  constructor(private documentResponseService: DocumentResponseService, protected route: ActivatedRoute, protected router: Router,
              private userService: UserService) {
    super(route, router);
  }

  requestData() {
    this.selected = null;
    this.isLoading = true;
    this.documentResponseService.getAll(this.size, this.offset, this.filter).subscribe(data => {
      this.all = data as Pageable<DocumentResponse>;

      this.isLoading = false;
    }, error => {
      this.isLoading = false;
    } );
  }

  private navigate(documentResponse: DocumentResponse) {
    this.documentResponseService.setTransferModel( documentResponse );
    this.router.navigate(['/documents', documentResponse.id]);
  }

}
