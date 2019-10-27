import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {UserService} from '../../api/user.service';
import {OrganizationResponseService} from '../../api/organizationResponse.service';
import {OrganizationResponse} from '../../model/firebird/organizationResponse';
import {VehicleResponse} from '../../model/firebird/vehicleResponse';
import {DocumentsFilter} from '../../model/documentsFilter';
import {Router} from '@angular/router';
import {DocumentResponse} from '../../model/firebird/documentResponse';

@Component({
  selector: 'app-document-filter',
  templateUrl: './document-filter.component.html',
  styleUrls: ['./document-filter.component.scss'],
})
export class DocumentFilterComponent implements OnInit {

  constructor(private organizationResponseService: OrganizationResponseService, private userService: UserService) {}

  @Input()
  private filter: DocumentsFilter;
  @Input()
  private document: DocumentResponse;
  @Output()
  private onChange: EventEmitter<any> = new EventEmitter();
  private states = [
    {
      name: 'Черновик',
      id: 2
    },
    {
      name: 'Оформлен',
      id: 4
    }
  ];
  private organizations: OrganizationResponse[] = [];
  private vehicles: VehicleResponse[] = [];

  ngOnInit(): void {
    this.organizationResponseService.getAll().subscribe( response => {
      this.organizations = response as OrganizationResponse[];
    } );
    this.organizationResponseService.getAllVehicles().subscribe( response => {
      this.vehicles = response as VehicleResponse[];
    } );
  }

  emitChange() {
    this.onChange.emit();
  }

  resetFilters() {
    this.filter.state = null;
    this.filter.organization = null;
    this.filter.vehicle = null;
    this.emitChange();
  }

  resetSort() {
    this.filter.sort = 'dateStart';
    this.filter.direction = 'asc';
    this.emitChange();
  }

}
