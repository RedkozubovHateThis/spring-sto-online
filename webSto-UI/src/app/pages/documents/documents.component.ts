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
  private filter: DocumentsFilter = new DocumentsFilter();
  private selected: DocumentResponse;

  constructor(private documentResponseService: DocumentResponseService, protected route: ActivatedRoute, private router: Router,
              private userService: UserService) {
    super(route);
  }

  requestData() {
    this.selected = null;
    this.isLoading = true;
    this.documentResponseService.getAll(this.page, this.size, this.offset, this.filter).subscribe(data => {
      this.all = data as Pageable<DocumentResponse>;
      this.setPageData(this.all);

      this.isLoading = false;
    }, error => {
      this.isLoading = false;
    } );
  }

  private navigate(documentResponse: DocumentResponse) {
    this.documentResponseService.setTransferModel( documentResponse );
    this.router.navigate(['/documents', documentResponse.id]);
  }

  prepareFilter(queryParams: Params) {
    if ( queryParams.sort ) this.filter.sort = queryParams.sort;
    if ( queryParams.direction ) this.filter.direction = queryParams.direction;

    if ( queryParams.state ) this.filter.state = parseInt(queryParams.state, 10);
    else this.filter.state = null;

    if ( queryParams.organization ) this.filter.organization = parseInt(queryParams.organization, 10);
    else this.filter.organization = null;

    if ( queryParams.client ) this.filter.client = parseInt(queryParams.client, 10);
    else this.filter.client = null;

    if ( queryParams.vehicle ) this.filter.vehicle = queryParams.vehicle;
    else this.filter.vehicle = null;

    if ( queryParams.vinNumber ) this.filter.vinNumber = queryParams.vinNumber;
    else this.filter.vinNumber = null;

    if ( queryParams.fromDate ) this.filter.fromDate = queryParams.fromDate;
    else this.filter.fromDate = null;

    if ( queryParams.toDate ) this.filter.toDate = queryParams.toDate;
    else this.filter.toDate = null;
  }

  refresh() {
    this.router.navigate(['documents'], { queryParams: this.filter });
  }

}
