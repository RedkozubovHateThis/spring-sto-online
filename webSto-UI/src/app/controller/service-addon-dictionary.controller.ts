import {Injectable} from '@angular/core';
import {PaginationController} from './pagination.controller';
import {DocumentCollection} from 'ngx-jsonapi';
import {DictionariesFilter} from '../model/dictionariesFilter';
import {ServiceAddonDictionaryResource} from '../model/resource/service-addon-dictionary.resource.service';

@Injectable()
export class ServiceAddonDictionaryController extends PaginationController {

  filter: DictionariesFilter = new DictionariesFilter();
  all: DocumentCollection<ServiceAddonDictionaryResource>;
  selectedModel: ServiceAddonDictionaryResource;

  constructor() {
    super();
  }

}
