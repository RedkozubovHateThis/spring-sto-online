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

@Injectable()
export class VehicleService {

  constructor(private vehicleResourceService: VehicleResourceService) {
    vehicleResourceService.register();
  }

  findByVinOrRegOrModel(search: string): Observable<DocumentCollection<VehicleResource>> {
    return this.vehicleResourceService.all({
      beforepath: `external`,
      remotefilter: {
        vinNumber: search,
        regNumber: search,
        modelName: search
      }
    });
  }

  saveVehicle(serviceDocument: ServiceDocumentResource): Observable<VehicleResource> {
    return new Observable<VehicleResource>( (subscriber) => {
      const vehicle: VehicleResource = serviceDocument.relationships.vehicle.data;

      vehicle.save().subscribe( (saved: IDocumentResource) => {
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

      vehicleMileage.save().subscribe( (saved: IDocumentResource) => {
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
