import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ModelTransfer} from '../model.transfer';
import {UserService} from '../../api/user.service';
import {HttpClient} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {Location} from '@angular/common';
import {ServiceDocumentResource, ServiceDocumentResourceService} from '../../model/resource/service-document.resource.service';
import {DocumentService} from '../../api/document-service.service';
import * as moment from 'moment';
import {VehicleResource, VehicleResourceService} from '../../model/resource/vehicle.resource.service';
import {ServiceWorkResource, ServiceWorkResourceService} from '../../model/resource/service-work.resource.service';
import {ServiceAddonResource, ServiceAddonResourceService} from '../../model/resource/service-addon.resource.service';
import {VehicleMileageResourceService} from '../../model/resource/vehicle-mileage.resource.service';
import {DocumentCollection} from 'ngx-jsonapi';

@Component({
  selector: 'app-document-add',
  templateUrl: '../document-edit/document-edit.component.html',
  styleUrls: ['../document-edit/document-edit.component.scss']
})
export class DocumentAddComponent implements OnInit {

  private isSaving = false;
  protected model: ServiceDocumentResource;
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
              private userService: UserService, private httpClient: HttpClient, private router: Router,
              private location: Location, private serviceDocumentResourceService: ServiceDocumentResourceService,
              private serviceWorkResourceService: ServiceWorkResourceService,
              private serviceAddonResourceService: ServiceAddonResourceService,
              private vehicleResourceService: VehicleResourceService,
              private vehicleMileageResourceService: VehicleMileageResourceService) {
    this.model = serviceDocumentResourceService.new();
    this.model.attributes.startDate = new Date().getTime();
    this.model.attributes.status = 'CREATED';
    if ( this.userService.isServiceLeader() && this.userService.currentUser.relationships.profile.data )
      this.model.addRelationship( this.userService.currentUser.relationships.profile.data, 'executor' );
    this.model.addRelationship( vehicleResourceService.new(), 'vehicle' );
    this.model.addRelationship( vehicleMileageResourceService.new(), 'vehicleMileage' );
  }

  ngOnInit(): void {
    this.setDates();
    this.requestPreviousVehicles();
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
    this.isSaving = true;
    this.documentService.saveVehicle( this.model ).subscribe( (savedVehicle) => {
      this.documentService.saveVehicleMileage( this.model ).subscribe( (savedVehicleMileage) => {
        this.documentService.saveServiceDocument(this.model).subscribe( (savedModel) => {
          this.documentService.saveServiceWorks( this.model, this.serviceWorks );
          this.documentService.saveServiceAddons( this.model, this.serviceAddons );
          this.model = savedModel;
          this.isSaving = false;
          this.toastrService.success('Документ успешно сохранен!');
          this.router.navigate(['documents', this.model.id, 'edit']);
        } );
      } );
    } );
  }
}
