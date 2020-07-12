import {Injectable} from '@angular/core';
import {PaginationController} from './pagination.controller';
import {DictionariesFilter} from '../model/dictionariesFilter';
import {DocumentCollection} from 'ngx-jsonapi';
import {VehicleDictionaryResource} from '../model/resource/vehicle-dictionary.resource.service';

@Injectable()
export class VehicleDictionaryController extends PaginationController {

  filter: DictionariesFilter = new DictionariesFilter();
  all: DocumentCollection<VehicleDictionaryResource>;
  selectedModel: VehicleDictionaryResource;

  constructor() {
    super();
  }

}
