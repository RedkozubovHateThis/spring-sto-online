import { Component } from '@angular/core';
import {DocumentResponse} from '../../model/firebird/documentResponse';
import {DocumentResponseService} from '../../api/documentResponse.service';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {Pageable} from '../../model/Pageable';
import {Pagination} from '../pagination';
import { DocumentsFilter } from 'src/app/model/documentsFilter';

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

  constructor(private documentResponseService: DocumentResponseService, protected route: ActivatedRoute, private router: Router) {
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

    if ( queryParams.vehicle ) this.filter.vehicle = parseInt(queryParams.vehicle, 10);
    else this.filter.vehicle = null;
  }

  refresh() {
    this.router.navigate(['documents'], { queryParams: this.filter });
  }

}
