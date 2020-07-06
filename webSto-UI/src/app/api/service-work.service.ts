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
export class ServiceWorkService implements RestService<ServiceWorkResource> {

  constructor(private serviceWorkResourceService: ServiceWorkResourceService) {
    serviceWorkResourceService.register();
  }

  getAll(id: string): Observable<DocumentCollection<ServiceWorkResource>> {
    return this.serviceWorkResourceService.all({
      beforepath: `${environment.getBeforeUrl()}/serviceDocuments/${id}`
    });
  }

  saveServiceWorks(serviceDocument: ServiceDocumentResource, serviceWorks: DocumentCollection<ServiceWorkResource>): void {
    serviceWorks.data.forEach( (serviceWork) => {
      serviceWork.addRelationship(serviceDocument, 'document');
      serviceWork.save({ beforepath: environment.getBeforeUrl() }).subscribe( (saved: IDocumentResource) => {
        serviceWork.fill( saved );
      } );
    } );
  }

  delete(model: ServiceWorkResource): Observable<void> {
    return this.serviceWorkResourceService.delete(model.id, { beforepath: environment.getBeforeUrl() });
  }

}
