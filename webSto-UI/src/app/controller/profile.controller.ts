import {Injectable} from '@angular/core';
import {PaginationController} from './pagination.controller';
import {DocumentsFilter} from '../model/documentsFilter';
import {Subject} from 'rxjs';
import {DocumentCollection} from 'ngx-jsonapi';
import {ServiceDocumentResource} from '../model/resource/service-document.resource.service';
import {DictionariesFilter} from '../model/dictionariesFilter';
import {ServiceWorkDictionaryResource} from '../model/resource/service-work-dictionary.resource.service';
import {ServiceAddonDictionaryResource} from '../model/resource/service-addon-dictionary.resource.service';
import {ProfilesFilter} from '../model/profilesFilter';
import {ProfileResource} from '../model/resource/profile.resource.service';

@Injectable()
export class ProfileController extends PaginationController {

  filter: ProfilesFilter = new ProfilesFilter();
  all: DocumentCollection<ProfileResource>;
  selectedModel: ProfileResource;

  constructor() {
    super();
  }

}
