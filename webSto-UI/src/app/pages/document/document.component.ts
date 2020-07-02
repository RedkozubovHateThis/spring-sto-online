import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ModelTransfer} from '../model.transfer';
import {UserService} from '../../api/user.service';
import {HttpClient} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {Location} from '@angular/common';
import {PaymentService} from '../../api/payment.service';
import {ServiceDocumentResource} from '../../model/resource/service-document.resource.service';
import {DocumentService} from '../../api/document-service.service';
import {UserResource} from '../../model/resource/user.resource.service';
import {DocumentCollection} from 'ngx-jsonapi';
import {ServiceWorkResource} from '../../model/resource/service-work.resource.service';
import {ServiceAddonResource} from '../../model/resource/service-addon.resource.service';

@Component({
  selector: 'app-documents',
  templateUrl: './document.component.html',
  styleUrls: ['./document.component.scss']
})
export class DocumentComponent extends ModelTransfer<ServiceDocumentResource, string> implements OnInit {

  private isLoading: boolean = false;
  private serviceWorks: DocumentCollection<ServiceWorkResource> = new DocumentCollection<ServiceWorkResource>();
  private serviceAddons: DocumentCollection<ServiceAddonResource> = new DocumentCollection<ServiceAddonResource>();

  constructor(private documentService: DocumentService, protected route: ActivatedRoute, private toastrService: ToastrService,
              private userService: UserService, private httpClient: HttpClient, private router: Router,
              private location: Location) {
    super(documentService, route);
  }

  requestData() {
    this.isLoading = true;
    this.documentService.getOne(this.id).subscribe( data => {
      this.model = data;
      this.isLoading = false;
      this.requestRelations();
    }, error => {
      this.isLoading = false;
    } );
  }

  onTransferComplete() {
    this.requestRelations();
  }

  requestRelations() {
    this.documentService.getServiceWorks(this.model.id).subscribe( (data) => {
      this.serviceWorks = data;
    } );
    this.documentService.getServiceAddons(this.model.id).subscribe( (data) => {
      this.serviceAddons = data;
    } );
  }

  private navigate(document: ServiceDocumentResource) {
    this.documentService.setTransferModel( document );
    this.router.navigate(['/documents', document.id, 'edit']);
  }
}
