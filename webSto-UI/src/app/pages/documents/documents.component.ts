import {Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Pageable} from '../../model/pageable';
import {Pagination} from '../pagination';
import {UserService} from '../../api/user.service';
import {DocumentResponseController} from '../../controller/document-response.controller';

@Component({
  selector: 'app-tables',
  templateUrl: './documents.component.html',
  styleUrls: ['./documents.component.scss']
})
export class DocumentsComponent extends Pagination {

  private isLoading: boolean = false;
  private selected: DocumentResponse;
  protected routeName = '/documents';

  constructor(protected route: ActivatedRoute, protected router: Router,
              private userService: UserService, protected documentResponseController: DocumentResponseController) {
    super(route, router, documentResponseController);
  }

  requestData() {
    this.selected = null;
    this.isLoading = true;
    this.documentResponseService.getAll(this.documentResponseController.filter).subscribe(data => {
      this.documentResponseController.all = data as Pageable<DocumentResponse>;

      this.isLoading = false;
    }, error => {
      this.isLoading = false;
    } );
  }

  select(document: DocumentResponse) {
    // if ( document.state === 4 )
      this.selected = document;
  }

  private navigate(documentResponse: DocumentResponse) {
    this.documentResponseService.setTransferModel( documentResponse );
    this.router.navigate(['/documents', documentResponse.id]);
  }

}
