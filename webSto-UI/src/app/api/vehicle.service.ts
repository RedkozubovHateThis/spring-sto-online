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
import {VehiclesFilter} from '../model/vehiclesFilter';

@Injectable()
export class VehicleService {

  constructor(private vehicleResourceService: VehicleResourceService, private http: HttpClient) {
    vehicleResourceService.register();
  }

  getAll(filter: VehiclesFilter): Observable<DocumentCollection<VehicleResource>> {
    const params = {
      modelName: filter.modelName != null ? filter.modelName : '',
      vinNumber: filter.vinNumber != null ? filter.vinNumber : '',
      regNumber: filter.regNumber != null ? filter.regNumber : '',
      year: filter.year != null ? filter.year : ''
    };
    return this.vehicleResourceService.all({
      beforepath: `${environment.getBeforeUrl()}/external`,
      sort: [`${filter.direction === 'desc' ? '-' : ''}${filter.sort}`],
      page: { number: filter.page, size: filter.size },
      remotefilter: params
    });
  }

  findByVinOrRegOrModel(search: string): Observable<DocumentCollection<VehicleResource>> {
    return new Observable<DocumentCollection<VehicleResource>>( (subscriber) => {
      const params = {
        search
      };
      this.http.get<IDataCollection>(`${environment.getApiUrl()}external/vehicles/search`, { params }).subscribe( (data) => {
        const collection = this.vehicleResourceService.newCollection();
        collection.fill( data );
        subscriber.next( collection );
        subscriber.complete();
      }, (error) => {
        subscriber.error( error );
        subscriber.complete();
      } );
    } );
  }

  save(model: VehicleResource): Observable<VehicleResource> {
    return new Observable<VehicleResource>( (subscriber) => {
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

  saveVehicle(serviceDocument: ServiceDocumentResource): Observable<VehicleResource> {
    return new Observable<VehicleResource>( (subscriber) => {
      const vehicle: VehicleResource = serviceDocument.relationships.vehicle.data;

      vehicle.save({ beforepath: environment.getBeforeUrl() }).subscribe( (saved: IDocumentResource) => {
        // vehicle.fill( saved );
        subscriber.next(vehicle);
        subscriber.complete();
      }, ( error ) => {
        subscriber.error( error );
        subscriber.complete();
      } );
    } );
  }

  saveVehicleMileage(serviceDocument: ServiceDocumentResource): Observable<VehicleMileageResource> {
    return new Observable<VehicleMileageResource>( (subscriber) => {
      const vehicle: VehicleResource = serviceDocument.relationships.vehicle.data;
      const vehicleMileage: VehicleMileageResource = serviceDocument.relationships.vehicleMileage.data;

      vehicleMileage.addRelationship(serviceDocument, 'document');
      vehicleMileage.addRelationship(vehicle, 'vehicle');

      vehicleMileage.save({ beforepath: environment.getBeforeUrl() }).subscribe( (saved: IDocumentResource) => {
        // vehicleMileage.fill( saved );
        subscriber.next( vehicleMileage );
        subscriber.complete();
      }, ( error ) => {
        subscriber.error( error );
        subscriber.complete();
      } );
    } );
  }

}
