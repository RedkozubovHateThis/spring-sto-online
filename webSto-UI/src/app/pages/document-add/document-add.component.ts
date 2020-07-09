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
import {VehicleMileageResource, VehicleMileageResourceService} from '../../model/resource/vehicle-mileage.resource.service';
import {DocumentCollection} from 'ngx-jsonapi';
import {ProfileResource, ProfileResourceService} from '../../model/resource/profile.resource.service';
import {ServiceWorkService} from '../../api/service-work.service';
import {VehicleService} from '../../api/vehicle.service';
import {ProfileService} from '../../api/profile.service';
import {ServiceAddonService} from '../../api/service-addon.service';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {VehicleDictionaryResource} from '../../model/resource/vehicle-dictionary.resource.service';
import {VehicleDictionaryService} from '../../api/vehicle.dictionary.service';
import {ServiceWorkDictionaryService} from '../../api/service-work.dictionary.service';
import {ServiceWorkDictionaryResource} from '../../model/resource/service-work-dictionary.resource.service';
import {ServiceAddonDictionaryResource} from '../../model/resource/service-addon-dictionary.resource.service';
import {ServiceAddonDictionaryService} from '../../api/service-addon.dictionary.service';
import {CustomerResource, CustomerResourceService} from '../../model/resource/customer.resource.service';
import {CustomerService} from '../../api/customer.service';

@Component({
  selector: 'app-document-add',
  templateUrl: '../document-edit/document-edit.component.html',
  styleUrls: ['../document-edit/document-edit.component.scss']
})
export class DocumentAddComponent implements OnInit {

  @ViewChild('clientModal', {static: false}) private clientModal;
  @ViewChild('vehicleModal', {static: false}) private vehicleModal;
  @ViewChild('executorModal', {static: false}) private executorModal;
  @ViewChild('customerModal', {static: false}) private customerModal;
  private vinSearch = '';
  private phoneOrEmailSearch = '';

  private vehicleEdit = false;
  private clientEdit = false;
  private customerEdit = false;
  private clientRegister = false;
  private showExecutor = false;
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
  private serviceWorks: Array<ServiceWorkResource> = new Array<ServiceWorkResource>();
  private serviceAddons: Array<ServiceAddonResource> = new Array<ServiceAddonResource>();
  private vehicles: DocumentCollection<VehicleResource> = new DocumentCollection<VehicleResource>();
  private clients: DocumentCollection<ProfileResource> = new DocumentCollection<ProfileResource>();
  private executors: DocumentCollection<ProfileResource> = new DocumentCollection<ProfileResource>();

  // Справочники
  @ViewChild('vehicleDictionaryModal', {static: false}) private vehicleDictionaryModal;
  @ViewChild('serviceWorkDictionaryModal', {static: false}) private serviceWorkDictionaryModal;
  @ViewChild('serviceAddonDictionaryModal', {static: false}) private serviceAddonDictionaryModal;
  private vehicleDictionaryNameSearch = '';
  private serviceWorkDictionaryNameSearch = '';
  private serviceAddonDictionaryNameSearch = '';
  private customerSearch = '';
  private vehicleDictionaries: DocumentCollection<VehicleDictionaryResource> = new DocumentCollection<VehicleDictionaryResource>();
  private serviceWorkDictionaries: DocumentCollection<ServiceWorkDictionaryResource> = new DocumentCollection<ServiceWorkDictionaryResource>();
  private serviceAddonDictionaries: DocumentCollection<ServiceAddonDictionaryResource> = new DocumentCollection<ServiceAddonDictionaryResource>();
  private customers: DocumentCollection<CustomerResource> = new DocumentCollection<CustomerResource>();

  constructor(private documentService: DocumentService, protected route: ActivatedRoute, private toastrService: ToastrService,
              private userService: UserService, private httpClient: HttpClient, private router: Router,
              private location: Location, private serviceDocumentResourceService: ServiceDocumentResourceService,
              private serviceWorkResourceService: ServiceWorkResourceService, private vehicleService: VehicleService,
              private serviceAddonResourceService: ServiceAddonResourceService, private profileService: ProfileService,
              private vehicleResourceService: VehicleResourceService, private profileResourceService: ProfileResourceService,
              private vehicleMileageResourceService: VehicleMileageResourceService, private serviceWorkService: ServiceWorkService,
              private serviceAddonService: ServiceAddonService, private modalService: NgbModal,
              private vehicleDictionaryService: VehicleDictionaryService,
              private serviceWorkDictionaryService: ServiceWorkDictionaryService,
              private serviceAddonDictionaryService: ServiceAddonDictionaryService,
              private customerResourceService: CustomerResourceService, private customerService: CustomerService) {
    this.model = serviceDocumentResourceService.new();
    this.model.attributes.startDate = new Date().getTime();
    this.model.attributes.status = 'CREATED';
    this.model.attributes.paidStatus = 'NOT_PAID';
    this.model.attributes.clientIsCustomer = true;
    if ( this.userService.isServiceLeader() && this.userService.currentUser.relationships.profile.data ) {
      this.model.addRelationship( this.userService.currentUser.relationships.profile.data, 'executor' );
      this.model.attributes.masterFio = this.userService.currentUser.attributes.fullFio;
    }
    this.model.addRelationship( profileResourceService.new(), 'client' );
    this.model.addRelationship( vehicleResourceService.new(), 'vehicle' );
    this.model.addRelationship( vehicleMileageResourceService.new(), 'vehicleMileage' );
  }

  ngOnInit(): void {
    this.setDates();
    const subscription = this.userService.currentUserIsLoaded.subscribe( (currentUser) => {
      if ( !this.model.hasOneRelated('executor') && currentUser.isServiceLeader()
        && currentUser.relationships.profile.data) {
        this.model.addRelationship( currentUser.relationships.profile.data, 'executor' );
        this.model.attributes.masterFio = this.userService.currentUser.attributes.fullFio;
      }

      subscription.unsubscribe();
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

  newServiceWork(name?: string) {
    const serviceWork: ServiceWorkResource = this.serviceWorkResourceService.new();
    serviceWork.attributes.count = 1;
    serviceWork.attributes.byPrice = true;
    serviceWork.attributes.name = name;
    this.serviceWorks.push( serviceWork );
  }
  newServiceAddon(name?: string) {
    const serviceAddon: ServiceAddonResource = this.serviceAddonResourceService.new();
    serviceAddon.attributes.count = 1;
    serviceAddon.attributes.name = name;
    this.serviceAddons.push( serviceAddon );
  }

  checkData(): boolean {
    const client: ProfileResource = this.model.relationships.client.data;
    const executor: ProfileResource = this.model.relationships.executor.data;
    const customer: CustomerResource = this.model.relationships.customer.data;
    const vehicle: VehicleResource = this.model.relationships.vehicle.data;
    const vehicleMileage: VehicleMileageResource = this.model.relationships.vehicleMileage.data;

    if ( !client || !client.type || !Object.keys( client.attributes ).length ) {
      this.toastrService.error('Не указан клиент!', 'Внимание!');
      return false;
    }
    else if ( !client.attributes.phone || client.attributes.phone.length === 0 ) {
      this.toastrService.error('Не указан телефон клиента!', 'Внимание!');
      return false;
    }
    else if ( !client.attributes.name || client.attributes.name.length === 0 ) {
      this.toastrService.error('Не указано полное наименование клиента!', 'Внимание!');
      return false;
    }

    if ( !this.model.attributes.clientIsCustomer ) {
      console.log(customer);
      if ( !customer || !customer.type || !Object.keys( customer.attributes ).length ) {
        this.toastrService.error('Не указан заказчик!', 'Внимание!');
        return false;
      }
      else if ( !customer.attributes.phone || customer.attributes.phone.length === 0 ) {
        this.toastrService.error('Не указан телефон заказчика!', 'Внимание!');
        return false;
      }
      else if ( !customer.attributes.name || customer.attributes.name.length === 0 ) {
        this.toastrService.error('Не указано полное наименование заказчика!', 'Внимание!');
        return false;
      }
      else if ( !customer.attributes.inn || customer.attributes.inn.length === 0 ) {
        this.toastrService.error('Не указан ИНН заказчика!', 'Внимание!');
        return false;
      }
    }

    if ( !executor || !executor.type || !Object.keys( executor.attributes ).length ) {
      this.toastrService.error('Не указан исполнитель!', 'Внимание!');
      return false;
    }

    if ( !vehicle || !vehicle.type || !Object.keys( vehicle.attributes ).length ) {
      this.toastrService.error('Не указан автомобиль!', 'Внимание!');
      return false;
    }
    else if ( !vehicle.attributes.modelName || vehicle.attributes.modelName.length === 0 ) {
      this.toastrService.error('Не указана марка/модель автомобиля!', 'Внимание!');
      return false;
    }
    else if ( !vehicle.attributes.vinNumber || vehicle.attributes.vinNumber.length === 0 ) {
      this.toastrService.error('Не указан VIN-номер автомобиля!', 'Внимание!');
      return false;
    }
    else if ( !vehicle.attributes.regNumber || vehicle.attributes.regNumber.length === 0 ) {
      this.toastrService.error('Не указан регистрационный номер автомобиля!', 'Внимание!');
      return false;
    }
    else if ( !vehicle.attributes.year ) {
      this.toastrService.error('Не указан год выпуска автомобиля!', 'Внимание!');
      return false;
    }

    if ( !vehicleMileage || !vehicleMileage.type || !Object.keys( vehicleMileage.attributes ).length
      || !vehicleMileage.attributes.mileage ) {
      this.toastrService.error('Не указан пробег автомобиля!', 'Внимание!');
      return false;
    }

    // if ( !this.model.attributes.number || this.model.attributes.number.length === 0 ) {
    //   this.toastrService.error('Не указан номер заказ-наряда!', 'Внимание!');
    //   return false;
    // }
    if ( !this.model.attributes.startDate ) {
      this.toastrService.error('Не указана дата начала ремонта!', 'Внимание!');
      return false;
    }
    else if ( !this.model.attributes.masterFio || this.model.attributes.masterFio.length === 0 ) {
      this.toastrService.error('Не указано ФИО мастера!', 'Внимание!');
      return false;
    }

    return true;
  }

  checkDataList(): boolean {
    let isWorksError = false;
    let isAddonsError = false;
    this.serviceWorks.forEach( (serviceWork) => {
      if ( !serviceWork.attributes.name || serviceWork.attributes.name.length === 0 )
        isWorksError = true;
      if ( !serviceWork.attributes.count )
        isWorksError = true;
      if ( serviceWork.attributes.byPrice && !serviceWork.attributes.price )
        isWorksError = true;
      else if ( !serviceWork.attributes.byPrice && ( !serviceWork.attributes.priceNorm || !serviceWork.attributes.timeValue ) )
        isWorksError = true;
    } );
    this.serviceAddons.forEach( (serviceAddon) => {
      if ( !serviceAddon.attributes.name || serviceAddon.attributes.name.length === 0 )
        isAddonsError = true;
      if ( !serviceAddon.attributes.count )
        isAddonsError = true;
      if ( !serviceAddon.attributes.cost )
        isWorksError = true;
    } );

    if ( isWorksError )
      this.toastrService.error('Ошибка заполнения работ по заказ-наряду!', 'Внимание!');
    if ( isAddonsError )
      this.toastrService.error('Ошибка заполнения товаров по заказ-наряду!', 'Внимание!');

    return !isWorksError || !isAddonsError;
  }

  save() {
    if ( !this.checkData() ) return;
    if ( !this.checkDataList() ) return;

    this.calculateTotalCost();
    this.isSaving = true;
    this.profileService.saveClientProfile( this.model, this.clientRegister ).subscribe( (savedClient) => {
      this.profileService.saveExecutorProfile( this.model ).subscribe( (savedExecutor) => {
        this.vehicleService.saveVehicle( this.model ).subscribe( (savedVehicle) => {
          this.vehicleService.saveVehicleMileage( this.model ).subscribe( (savedVehicleMileage) => {
            this.customerService.saveCustomer( this.model ).subscribe( (savedCustomer) => {
              this.documentService.saveServiceDocument(this.model).subscribe( (savedModel) => {
                this.serviceWorkService.saveServiceWorks( this.model, this.serviceWorks );
                this.serviceAddonService.saveServiceAddons( this.model, this.serviceAddons );
                this.model = savedModel;
                this.isSaving = false;
                this.toastrService.success('Заказ-наряд успешно сохранен!');
                this.router.navigate(['documents', this.model.id, 'edit']);
              }, (error) => {
                this.showError(error, 'Ошибка сохранения заказ-наряда');
                this.isSaving = false;
              }  );
            }, (error) => {
              this.showError(error, 'Ошибка сохранения заказ-наряда');
              this.isSaving = false;
            } );
          }, (error) => {
            this.showError(error, 'Ошибка сохранения пробега');
            this.isSaving = false;
          }  );
        }, (error) => {
          this.showError(error, 'Ошибка сохранения автомобиля');
          this.isSaving = false;
        }  );
      }, (error) => {
        this.showError(error, 'Ошибка сохранения исполнителя');
        this.isSaving = false;
      }  );
    }, (error) => {
      this.showError(error, 'Ошибка сохранения клиента');
      this.isSaving = false;
    } );
  }

  showError(error: any, defaultMessage: string) {
    if ( error.errors && Array.isArray( error.errors ) )
      this.toastrService.error( `${defaultMessage}: ${error.errors[0].detail}`, 'Внимание!' );
    else
      this.toastrService.error( `${defaultMessage}!`, 'Внимание!' );
  }

  newVehicle() {
    const vehicle: VehicleResource = this.vehicleResourceService.new();
    this.model.addRelationship( vehicle, 'vehicle' );
    this.vehicleEdit = false;
  }

  newClient() {
    const profile: ProfileResource = this.profileResourceService.new();
    this.model.addRelationship( profile, 'client' );
    this.clientRegister = true;
    this.clientEdit = false;
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

    this.vehicleService.findByVinOrRegOrModel( this.vinSearch ).subscribe( (vehicles) => {
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
    this.clientRegister = false;
  }

  setExecutor(executor: ProfileResource) {
    this.model.addRelationship(executor, 'executor');
    this.modalService.dismissAll();
  }

  calculateTotalCost() {
    let cost = 0;
    this.serviceWorks.forEach( (serviceWork) => {
      serviceWork.prepareRecord();

      if ( serviceWork.attributes.count > 0 ) {
        if ( serviceWork.attributes.byPrice )
          cost += serviceWork.attributes.price > 0 ? serviceWork.attributes.price * serviceWork.attributes.count : 0;
        else {
          cost += serviceWork.attributes.timeValue > 0 && serviceWork.attributes.priceNorm > 0
            ? serviceWork.attributes.timeValue * serviceWork.attributes.priceNorm * serviceWork.attributes.count
            : 0;
        }
      }
    } );
    this.serviceAddons.forEach( (serviceAddon) => {
      serviceAddon.prepareRecord();

      if ( serviceAddon.attributes.count > 0 ) {
        cost += serviceAddon.attributes.cost > 0 ? serviceAddon.attributes.cost * serviceAddon.attributes.count : 0;
      }
    } );
    this.model.attributes.cost = cost;
  }

  // Справочники

  openVehicleDictionariesModal() {
    this.modalService.open(this.vehicleDictionaryModal, { size: 'lg' });
  }
  openServiceWorkDictionariesModal() {
    this.modalService.open(this.serviceWorkDictionaryModal, { size: 'lg' });
  }
  openServiceAddonDictionariesModal() {
    this.modalService.open(this.serviceAddonDictionaryModal, { size: 'lg' });
  }
  openCustomersModal() {
    this.modalService.open(this.customerModal, { size: 'lg' });
  }

  searchVehicleDictionaries() {
    if ( !this.vehicleDictionaryNameSearch || this.vehicleDictionaryNameSearch.length < 3 ) return;

    this.vehicleDictionaryService.findByName( this.vehicleDictionaryNameSearch ).subscribe( (vehicleDictionaries) => {
      this.vehicleDictionaries = vehicleDictionaries;
    } );
  }
  searchServiceWorkDictionaries() {
    if ( !this.serviceWorkDictionaryNameSearch || this.serviceWorkDictionaryNameSearch.length < 3 ) return;

    this.serviceWorkDictionaryService.findByName( this.serviceWorkDictionaryNameSearch ).subscribe( (serviceWorkDictionaries) => {
      this.serviceWorkDictionaries = serviceWorkDictionaries;
    } );
  }
  searchServiceAddonDictionaries() {
    if ( !this.serviceAddonDictionaryNameSearch || this.serviceAddonDictionaryNameSearch.length < 3 ) return;

    this.serviceAddonDictionaryService.findByName( this.serviceAddonDictionaryNameSearch ).subscribe( (serviceAddonDictionaries) => {
      this.serviceAddonDictionaries = serviceAddonDictionaries;
    } );
  }
  newServiceWorkFromDictionary(serviceWorkDictionaryResource: ServiceWorkDictionaryResource) {
    this.newServiceWork(serviceWorkDictionaryResource.attributes.name);
    this.modalService.dismissAll();
  }
  newServiceAddonFromDictionary(serviceAddonDictionaryResource: ServiceAddonDictionaryResource) {
    this.newServiceAddon(serviceAddonDictionaryResource.attributes.name);
    this.modalService.dismissAll();
  }
  searchCustomers() {
    if ( !this.customerSearch || this.customerSearch.length < 3 ) return;

    this.customerService.findByPhoneOrEmail( this.customerSearch ).subscribe( (customers) => {
      this.customers = customers;
    } );
  }

  updateVehicle(vehicleDictionary: VehicleDictionaryResource) {
    const vehicle = this.model.relationships.vehicle.data;
    vehicle.attributes.modelName = vehicleDictionary.attributes.name;
    this.modalService.dismissAll();
  }
  setCustomer(customer: CustomerResource) {
    this.model.addRelationship(customer, 'customer');
    this.modalService.dismissAll();
  }

  createCustomer() {
    const customer: CustomerResource = this.model.relationships.customer.data;
    if ( !this.model.attributes.clientIsCustomer && ( !customer || !customer.type || !Object.keys( customer.attributes ).length ) )
      this.newCustomer();
  }

  newCustomer() {
    this.model.addRelationship( this.customerResourceService.new(), 'customer' );
  }
}
