import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ModelTransfer} from '../model.transfer';
import {UserService} from '../../api/user.service';
import {HttpClient} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {Location} from '@angular/common';
import {PaymentService} from '../../api/payment.service';
import {ServiceDocumentResource} from '../../model/resource/service-document.resource.service';
import {DocumentService} from '../../api/document-service.service';

@Component({
  selector: 'app-documents',
  templateUrl: './document.component.html',
  styleUrls: ['./document.component.scss']
})
export class DocumentComponent extends ModelTransfer<ServiceDocumentResource, string> implements OnInit {

  private isLoading: boolean = false;
  private states = [
    {
      name: 'Черновик',
      id: 'CREATED'
    },
    {
      name: 'Оформлен',
      id: 'COMPLETED'
    }
  ];

  constructor(private documentService: DocumentService, protected route: ActivatedRoute, private toastrService: ToastrService,
              private userService: UserService, private httpClient: HttpClient,
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
    this.documentService.getServiceWorks(this.model.id).subscribe( data => {
      this.model.addRelationships( data.data, 'serviceWorks' );
    } );
    this.documentService.getServiceAddons(this.model.id).subscribe( data => {
      this.model.addRelationships( data.data, 'serviceAddons' );
    } );
  }
}
