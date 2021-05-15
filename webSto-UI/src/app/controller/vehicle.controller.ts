import {Injectable} from '@angular/core';
import {PaginationController} from './pagination.controller';
import {DocumentsFilter} from '../model/documentsFilter';
import {Subject} from 'rxjs';
import {DocumentCollection} from 'ngx-jsonapi';
import {ServiceDocumentResource} from '../model/resource/service-document.resource.service';
import {DictionariesFilter} from '../model/dictionariesFilter';
import {ServiceWorkDictionaryResource} from '../model/resource/service-work-dictionary.resource.service';
import {ServiceAddonDictionaryResource} from '../model/resource/service-addon-dictionary.resource.service';
import {VehiclesFilter} from '../model/vehiclesFilter';
import {VehicleResource} from '../model/resource/vehicle.resource.service';

@Injectable()
export class VehicleController extends PaginationController {

  filter: VehiclesFilter = new VehiclesFilter();
  all: DocumentCollection<VehicleResource>;
  selectedModel: VehicleResource;

  constructor() {
    super();
  }

}
