import {Component, OnInit, ViewChild} from '@angular/core';
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
import {ServiceWorkService} from '../../api/service-work.service';
import {ServiceAddonService} from '../../api/service-addon.service';
import {ProfileResource, ProfileResourceService} from '../../model/resource/profile.resource.service';
import {VehicleService} from '../../api/vehicle.service';
import {ProfileService} from '../../api/profile.service';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-document-edit',
  templateUrl: './document-edit.component.html',
  styleUrls: ['./document-edit.component.scss']
})
export class DocumentEditComponent extends ModelTransfer<ServiceDocumentResource, string> implements OnInit {

  @ViewChild('clientModal', {static: false}) private clientModal;
  @ViewChild('vehicleModal', {static: false}) private vehicleModal;
  @ViewChild('executorModal', {static: false}) private executorModal;
  private vinSearch = '';
  private phoneOrEmailSearch = '';

  private vehicleEdit = false;
  private clientEdit = false;
  private clientRegister = false;
  private isSaving = false;
  private isLoading = false;
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
  private vehicles: DocumentCollection<VehicleResource> = new DocumentCollection<VehicleResource>();
  private clients: DocumentCollection<ProfileResource> = new DocumentCollection<ProfileResource>();
  private executors: DocumentCollection<ProfileResource> = new DocumentCollection<ProfileResource>();

  constructor(private documentService: DocumentService, protected route: ActivatedRoute, private toastrService: ToastrService,
              private userService: UserService, private httpClient: HttpClient,
              private location: Location, private serviceWorkResourceService: ServiceWorkResourceService,
              private serviceAddonResourceService: ServiceAddonResourceService, private vehicleService: VehicleService,
              private vehicleResourceService: VehicleResourceService, private profileService: ProfileService,
              private vehicleMileageResourceService: VehicleMileageResourceService,
              private serviceWorkService: ServiceWorkService, private profileResourceService: ProfileResourceService,
              private serviceAddonService: ServiceAddonService, private modalService: NgbModal) {
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
    }, error => {
      this.isLoading = false;
    } );
  }

  onTransferComplete() {
    this.checkRelations();
    this.requestRelations();
    this.setDates();
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
    this.serviceWorkService.getAll(this.model.id).subscribe( (data) => {
      this.serviceWorks = data;
    } );
    this.serviceAddonService.getAll(this.model.id).subscribe( (data) => {
      this.serviceAddons = data;
    } );
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
    serviceWork.attributes.count = 1;
    serviceWork.attributes.byPrice = true;
    this.serviceWorks.data.push( serviceWork );
  }
  newServiceAddon() {
    const serviceAddon: ServiceAddonResource = this.serviceAddonResourceService.new();
    serviceAddon.attributes.count = 1;
    this.serviceAddons.data.push( serviceAddon );
  }

  save() {
    this.calculateTotalCost();
    this.isSaving = true;
    this.profileService.saveClientProfile( this.model ).subscribe( (savedClient) => {
      this.profileService.saveExecutorProfile( this.model ).subscribe( (savedExecutor) => {
        this.vehicleService.saveVehicle( this.model ).subscribe( (savedVehicle) => {
          this.vehicleService.saveVehicleMileage( this.model ).subscribe( (savedVehicleMileage) => {
            this.documentService.saveServiceDocument(this.model).subscribe( (savedModel) => {
              this.serviceWorkService.saveServiceWorks( this.model, this.serviceWorks );
              this.serviceAddonService.saveServiceAddons( this.model, this.serviceAddons );
              this.model = savedModel;
              this.isSaving = false;
              this.toastrService.success('Документ успешно сохранен!');
            } );
          } );
        } );
      } );
    } );
  }

  newVehicle() {
    const vehicle: VehicleResource = this.vehicleResourceService.new();
    this.model.addRelationship( vehicle, 'vehicle' );
  }

  newClient() {
    const profile: ProfileResource = this.profileResourceService.new();
    this.model.addRelationship( profile, 'client' );
    this.clientRegister = true;
  }

  openClientsModal() {
    this.modalService.open(this.clientModal, { size: 'lg' });
  }
  openExecutorsModal() {
    this.searchExecutors();
    this.modalService.open(this.executorModal, { size: 'lg' });
  }

  openVehiclesModal() {
    this.modalService.open(this.vehicleModal, { size: 'lg' });
  }

  searchClients() {
    if ( !this.phoneOrEmailSearch || this.phoneOrEmailSearch.length < 3 ) return;

    this.profileService.findByPhoneOrEmail( this.phoneOrEmailSearch ).subscribe( (clients) => {
      this.clients = clients;
    } );
  }

  searchExecutors() {
    this.profileService.getAllExecutors().subscribe( (executors) => {
      this.executors = executors;
    } );
  }

  searchVehicles() {
    if ( !this.vinSearch || this.vinSearch.length < 3 ) return;

    this.vehicleService.findByVin( this.vinSearch ).subscribe( (vehicles) => {
      this.vehicles = vehicles;
    } );
  }

  setVehicle(vehicle: VehicleResource) {
    this.model.addRelationship(vehicle, 'vehicle');
    this.modalService.dismissAll();
  }

  setClient(client: ProfileResource) {
    this.model.addRelationship(client, 'client');
    this.modalService.dismissAll();
  }

  setExecutor(executor: ProfileResource) {
    this.model.addRelationship(executor, 'executor');
    this.modalService.dismissAll();
  }

  calculateTotalCost() {
    let cost = 0;
    this.serviceWorks.data.forEach( (serviceWork) => {
      if ( serviceWork.attributes.count > 0 ) {
        if ( serviceWork.attributes.byPrice )
          cost += serviceWork.attributes.price > 0 ? serviceWork.attributes.price * serviceWork.attributes.count : 0;
        else {
          cost += serviceWork.attributes.timeValue > 0 && serviceWork.attributes.priceNorm > 0
            ? serviceWork.attributes.timeValue * serviceWork.attributes.priceNorm
            : 0;
        }
      }
    } );
    this.serviceAddons.data.forEach( (serviceAddon) => {
      if ( serviceAddon.attributes.count > 0 ) {
        cost += serviceAddon.attributes.cost > 0 ? serviceAddon.attributes.cost * serviceAddon.attributes.count : 0;
      }
    } );
    this.model.attributes.cost = cost;
  }
}
