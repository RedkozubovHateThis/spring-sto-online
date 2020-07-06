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
import {VehicleMileageResourceService} from '../../model/resource/vehicle-mileage.resource.service';
import {ServiceWorkService} from '../../api/service-work.service';
import {ServiceAddonService} from '../../api/service-addon.service';

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
              private location: Location,
              private serviceWorkService: ServiceWorkService,
              private serviceAddonService: ServiceAddonService) {
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
    this.serviceWorkService.getAll(this.model.id).subscribe( (data) => {
      this.serviceWorks = data;
    } );
    this.serviceAddonService.getAll(this.model.id).subscribe( (data) => {
      this.serviceAddons = data;
    } );
  }

  private navigate(document: ServiceDocumentResource) {
    this.documentService.setTransferModel( document );
    this.router.navigate(['/documents', document.id, 'edit']);
  }

  calculateServiceWorkTotalCost(): number {
    let totalCost = 0;
    this.serviceWorks.data.forEach( (serviceWork) => {
      totalCost += this.calculateServiceWorkCost(serviceWork);
    } );
    return totalCost;
  }

  calculateServiceWorkCost(serviceWork: ServiceWorkResource): number {
    if ( serviceWork.attributes.count > 0 ) {
      if ( serviceWork.attributes.byPrice )
        return serviceWork.attributes.price > 0 ? serviceWork.attributes.price * serviceWork.attributes.count : 0;
      else {
        return serviceWork.attributes.timeValue > 0 && serviceWork.attributes.priceNorm > 0
          ? serviceWork.attributes.timeValue * serviceWork.attributes.priceNorm * serviceWork.attributes.count
          : 0;
      }
    }
    return 0;
  }

  calculateServiceAddonTotalCost(): number {
    let totalCost = 0;
    this.serviceAddons.data.forEach( (serviceAddon) => {
      totalCost += this.calculateServiceAddonCost(serviceAddon);
    } );
    return totalCost;
  }

  calculateServiceAddonCost(serviceAddon: ServiceAddonResource): number {
    if ( serviceAddon.attributes.count > 0 )
      return serviceAddon.attributes.cost > 0 ? serviceAddon.attributes.cost * serviceAddon.attributes.count : 0;
    return 0;
  }
}
