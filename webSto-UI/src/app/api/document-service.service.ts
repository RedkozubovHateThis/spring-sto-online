import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {TransferService} from './transfer.service';
import {ToastrService} from 'ngx-toastr';
import {RestService} from './rest.service';
import {ServiceDocumentResource, ServiceDocumentResourceService} from '../model/resource/service-document.resource.service';
import {ServiceWorkResource, ServiceWorkResourceService} from '../model/resource/service-work.resource.service';
import {ServiceAddonResource, ServiceAddonResourceService} from '../model/resource/service-addon.resource.service';
import {VehicleResource, VehicleResourceService} from '../model/resource/vehicle.resource.service';
import {VehicleMileageResource, VehicleMileageResourceService} from '../model/resource/vehicle-mileage.resource.service';
import {DocumentsFilter} from '../model/documentsFilter';
import {Observable} from 'rxjs';
import {DocumentCollection, DocumentResource} from 'ngx-jsonapi';
import {UserService} from './user.service';
import {IDataCollection} from 'ngx-jsonapi/interfaces/data-collection';
import {IDocumentResource} from 'ngx-jsonapi/interfaces/data-object';
import {subscribeOn} from 'rxjs/operators';
import {environment} from '../../environments/environment';

@Injectable()
export class DocumentService implements TransferService<ServiceDocumentResource>, RestService<ServiceDocumentResource> {

  private transferModel: ServiceDocumentResource;

  constructor(private http: HttpClient, private router: Router, private toastrService: ToastrService,
              private serviceDocumentResourceService: ServiceDocumentResourceService,
              private serviceWorkResourceService: ServiceWorkResourceService,
              private serviceAddonResourceService: ServiceAddonResourceService,
              private vehicleResourceService: VehicleResourceService, private userService: UserService,
              private vehicleMileageResourceService: VehicleMileageResourceService) {
    serviceDocumentResourceService.register();
  }

  getAll(filter: DocumentsFilter): Observable<DocumentCollection<ServiceDocumentResource>> {
    const params = {
      state: filter.state != null ? filter.state : '',
      organization: filter.organization != null ? filter.organization : '',
      vehicle: filter.vehicle != null ? filter.vehicle : '',
      vinNumber: filter.vinNumber != null ? filter.vinNumber : '',
      client: filter.client != null ? filter.client : '',
      fromDate: filter.fromDate != null ? filter.fromDate : '',
      toDate: filter.toDate != null ? filter.toDate : '',
    };
    return this.serviceDocumentResourceService.all({
      beforepath: `${environment.getBeforeUrl()}/external`,
      sort: [`${filter.direction === 'desc' ? '-' : ''}${filter.sort}`],
      page: { number: filter.page, size: filter.size },
      remotefilter: params
    });
  }

  getOne(id: string): Observable<ServiceDocumentResource> {
    return this.serviceDocumentResourceService.get(id, { beforepath: environment.getBeforeUrl() });
  }

  saveServiceDocument(model: ServiceDocumentResource): Observable<ServiceDocumentResource> {
    return new Observable<ServiceDocumentResource>( (subscriber) => {
      model.save({ beforepath: environment.getBeforeUrl() }).subscribe( (data: IDocumentResource) => {
        // model.fill(data);
        subscriber.next(model);
        subscriber.complete();
      }, ( error ) => {
        subscriber.error( error );
        subscriber.complete();
      } );
    } );
  }

  delete(model: ServiceDocumentResource): Observable<void> {
    return this.serviceDocumentResourceService.delete(model.id, { beforepath: environment.getBeforeUrl() });
  }

  getTransferModel() {
    return this.transferModel;
  }

  resetTransferModel() {
    this.transferModel = null;
  }

  setTransferModel(model: ServiceDocumentResource) {
    this.transferModel = model;
  }

  emitOrganizationChange() {

  }

}
