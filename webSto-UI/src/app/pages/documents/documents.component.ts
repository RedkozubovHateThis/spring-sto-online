import {Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Pagination} from '../pagination';
import {UserService} from '../../api/user.service';
import {DocumentController} from '../../controller/document.controller';
import {ServiceDocumentResource} from '../../model/resource/service-document.resource.service';
import {DocumentService} from '../../api/document-service.service';

@Component({
  selector: 'app-tables',
  templateUrl: './documents.component.html',
  styleUrls: ['./documents.component.scss']
})
export class DocumentsComponent extends Pagination {

  private isLoading: boolean = false;
  private selected: ServiceDocumentResource;
  protected routeName = '/documents';

  constructor(protected route: ActivatedRoute, protected router: Router, private documentService: DocumentService,
              private userService: UserService, protected documentController: DocumentController) {
    super(route, router, documentController);
  }

  requestData() {
    this.selected = null;
    this.isLoading = true;
    this.documentService.getAll(this.documentController.filter).subscribe(data => {
      this.documentController.all = data;

      this.isLoading = false;
    }, error => {
      this.isLoading = false;
    } );
  }

  select(document: ServiceDocumentResource) {
      this.selected = document;
  }

  private navigate(documentResponse: ServiceDocumentResource) {
    this.documentService.setTransferModel( documentResponse );
    this.router.navigate(['/documents', documentResponse.id]);
  }

}
