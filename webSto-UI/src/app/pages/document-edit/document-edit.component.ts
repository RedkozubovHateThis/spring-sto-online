import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ModelTransfer} from '../model.transfer';
import {UserService} from '../../api/user.service';
import {HttpClient} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {Location} from '@angular/common';
import {ServiceDocumentResource} from '../../model/resource/service-document.resource.service';
import {DocumentService} from '../../api/document-service.service';
import * as moment from 'moment';
import {VehicleResource, VehicleResourceService} from '../../model/resource/vehicle.resource.service';
import {ServiceWorkResource, ServiceWorkResourceService} from '../../model/resource/service-work.resource.service';
import {ServiceAddonResource, ServiceAddonResourceService} from '../../model/resource/service-addon.resource.service';
import {VehicleMileageResourceService} from '../../model/resource/vehicle-mileage.resource.service';
import {DocumentCollection} from 'ngx-jsonapi';

@Component({
  selector: 'app-document-edit',
  templateUrl: './document-edit.component.html',
  styleUrls: ['./document-edit.component.scss']
})
export class DocumentEditComponent extends ModelTransfer<ServiceDocumentResource, string> implements OnInit {

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
  private startDate: moment.Moment;
  private endDate: moment.Moment;
  private datePickerConfig = {
    locale: 'ru',
    firstDayOfWeek: 'mo',
    // showGoToCurrent: true,
    format: 'DD.MM.YYYY HH:mm',
    showTwentyFourHours: true
    // monthFormat: 'MM, YYYY'
  };
  private serviceWorks: DocumentCollection<ServiceWorkResource> = new DocumentCollection<ServiceWorkResource>();
  private serviceAddons: DocumentCollection<ServiceAddonResource> = new DocumentCollection<ServiceAddonResource>();

  constructor(private documentService: DocumentService, protected route: ActivatedRoute, private toastrService: ToastrService,
              private userService: UserService, private httpClient: HttpClient,
              private location: Location, private serviceWorkResourceService: ServiceWorkResourceService,
              private serviceAddonResourceService: ServiceAddonResourceService,
              private vehicleResourceService: VehicleResourceService,
              private vehicleMileageResourceService: VehicleMileageResourceService) {
    super(documentService, route);
  }

  requestData() {
    this.isLoading = true;
    this.documentService.getOne(this.id).subscribe( data => {
      this.model = data;
      this.isLoading = false;
      this.checkRelations();
      this.requestRelations();
      this.setDates();
      this.requestPreviousVehicles();
    }, error => {
      this.isLoading = false;
    } );
  }

  onTransferComplete() {
    this.checkRelations();
    this.requestRelations();
    this.setDates();
    this.requestPreviousVehicles();
  }

  checkRelations() {
    if ( !this.model.relationships.client.data )
      this.model.addRelationship( this.userService.currentUser, 'client' );
    if ( !this.model.relationships.vehicle.data )
      this.model.addRelationship( this.vehicleResourceService.new(), 'vehicle' );
    if ( !this.model.relationships.vehicleMileage.data )
      this.model.addRelationship( this.vehicleMileageResourceService.new(), 'vehicleMileage' );
  }

  requestRelations() {
    this.documentService.getServiceWorks(this.model.id).subscribe( (data) => {
      this.serviceWorks = data;
    } );
    this.documentService.getServiceAddons(this.model.id).subscribe( (data) => {
      this.serviceAddons = data;
    } );
  }

  requestPreviousVehicles() {
    this.documentService.getPreviousVehicles().subscribe((response) => {
      console.log(response);
    });
  }

  setDates() {
    if ( this.model.attributes.startDate )
      this.startDate = moment(this.model.attributes.startDate);
    if ( this.model.attributes.endDate )
      this.endDate = moment(this.model.attributes.endDate);
  }

  setStartDate(e) {
    const startDate = moment(e, 'dd.MM.yyyy HH.mm');
    this.model.attributes.startDate = startDate.toDate().getTime();
  }
  setEndDate(e) {
    const endDate = moment(e, 'dd.MM.yyyy HH.mm');
    this.model.attributes.endDate = endDate.toDate().getTime();
  }

  newServiceWork() {
    const serviceWork: ServiceWorkResource = this.serviceWorkResourceService.new();
    this.serviceWorks.data.push( serviceWork );
  }
  newServiceAddon() {
    const serviceAddon: ServiceAddonResource = this.serviceAddonResourceService.new();
    this.serviceAddons.data.push( serviceAddon );
  }

  save() {
    this.documentService.saveVehicle( this.model ).subscribe( (savedVehicle) => {
      this.documentService.saveVehicleMileage( this.model ).subscribe( (savedVehicleMileage) => {
        this.documentService.saveServiceDocument(this.model).subscribe( (savedModel) => {
          this.documentService.saveServiceWorks( this.model, this.serviceWorks );
          this.documentService.saveServiceAddons( this.model, this.serviceAddons );
          this.model = savedModel;
        } );
      } );
    } );
  }
}
