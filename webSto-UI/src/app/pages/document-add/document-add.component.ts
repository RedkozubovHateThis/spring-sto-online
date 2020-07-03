import {Component, OnInit, ViewChild} from '@angular/core';
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
import {ProfileResource, ProfileResourceService} from '../../model/resource/profile.resource.service';
import {ServiceWorkService} from '../../api/service-work.service';
import {VehicleService} from '../../api/vehicle.service';
import {ProfileService} from '../../api/profile.service';
import {ServiceAddonService} from '../../api/service-addon.service';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-document-add',
  templateUrl: '../document-edit/document-edit.component.html',
  styleUrls: ['../document-edit/document-edit.component.scss']
})
export class DocumentAddComponent implements OnInit {

  @ViewChild('clientModal', {static: false}) private clientModal;
  @ViewChild('vehicleModal', {static: false}) private vehicleModal;
  private vinSearch = '';
  private phoneOrEmailSearch = '';

  private vehicleEdit = false;
  private clientEdit = false;
  private clientRegister = false;
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
  private vehicles: DocumentCollection<VehicleResource> = new DocumentCollection<VehicleResource>();
  private clients: DocumentCollection<ProfileResource> = new DocumentCollection<ProfileResource>();

  constructor(private documentService: DocumentService, protected route: ActivatedRoute, private toastrService: ToastrService,
              private userService: UserService, private httpClient: HttpClient, private router: Router,
              private location: Location, private serviceDocumentResourceService: ServiceDocumentResourceService,
              private serviceWorkResourceService: ServiceWorkResourceService, private vehicleService: VehicleService,
              private serviceAddonResourceService: ServiceAddonResourceService, private profileService: ProfileService,
              private vehicleResourceService: VehicleResourceService, private profileResourceService: ProfileResourceService,
              private vehicleMileageResourceService: VehicleMileageResourceService, private serviceWorkService: ServiceWorkService,
              private serviceAddonService: ServiceAddonService, private modalService: NgbModal) {
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
              this.router.navigate(['documents', this.model.id, 'edit']);
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

  openVehiclesModal() {
    this.modalService.open(this.vehicleModal, { size: 'lg' });
  }

  searchClients() {
    if ( !this.phoneOrEmailSearch ) return;

    this.profileService.findByPhoneOrEmail( this.phoneOrEmailSearch ).subscribe( (clients) => {
      this.clients = clients;
    } );
  }

  searchVehicles() {
    if ( !this.vinSearch ) return;

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
}
