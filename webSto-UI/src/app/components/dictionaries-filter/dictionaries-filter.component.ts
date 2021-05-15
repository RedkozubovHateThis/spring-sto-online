import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {UserService} from '../../api/user.service';
import {DocumentsFilter} from '../../model/documentsFilter';
import * as moment from 'moment';
import {Observable} from 'rxjs';
import {debounceTime, distinctUntilChanged, map} from 'rxjs/operators';
import {ServiceDocumentResource} from '../../model/resource/service-document.resource.service';
import {ProfileResource} from '../../model/resource/profile.resource.service';
import {DocumentCollection} from 'ngx-jsonapi';
import {ProfileService} from '../../api/profile.service';
import {DictionariesFilter} from '../../model/dictionariesFilter';

@Component({
  selector: 'app-dictionaries-filter',
  templateUrl: './dictionaries-filter.component.html',
  styleUrls: ['./dictionaries-filter.component.scss'],
})
export class DictionariesFilterComponent implements OnInit {

  constructor() {}

  @Input()
  private filter: DictionariesFilter;
  @Output()
  private onChange: EventEmitter<any> = new EventEmitter();

  ngOnInit(): void {
  }

  emitChange() {
    this.onChange.emit();
  }

  resetFilters() {
    this.filter.name = null;
    this.emitChange();
  }

  resetSort() {
    this.filter.sort = 'name';
    this.filter.direction = 'desc';
    this.emitChange();
  }

}
