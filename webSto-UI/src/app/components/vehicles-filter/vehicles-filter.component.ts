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
import {VehiclesFilter} from '../../model/vehiclesFilter';

@Component({
  selector: 'app-vehicles-filter',
  templateUrl: './vehicles-filter.component.html',
  styleUrls: ['./vehicles-filter.component.scss'],
})
export class VehiclesFilterComponent implements OnInit {

  constructor() {}

  @Input()
  private filter: VehiclesFilter;
  @Output()
  private onChange: EventEmitter<any> = new EventEmitter();

  ngOnInit(): void {
  }

  emitChange() {
    this.onChange.emit();
  }

  resetFilters() {
    this.filter.modelName = null;
    this.filter.regNumber = null;
    this.filter.vinNumber = null;
    this.filter.year = null;
    this.emitChange();
  }

  resetSort() {
    this.filter.sort = 'modelName';
    this.filter.direction = 'desc';
    this.emitChange();
  }

}
