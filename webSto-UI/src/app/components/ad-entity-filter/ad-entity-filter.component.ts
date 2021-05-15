import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {UserService} from '../../api/user.service';
import {UsersFilter} from '../../model/usersFilter';
import {ProfilesFilter} from '../../model/profilesFilter';
import {AdEntitiesFilter} from '../../model/adEntitiesFilter';
import {DocumentCollection} from 'ngx-jsonapi';
import {UserResource} from '../../model/resource/user.resource.service';

@Component({
  selector: 'app-ad-entity-filter',
  templateUrl: './ad-entity-filter.component.html',
  styleUrls: ['./ad-entity-filter.component.scss'],
})
export class AdEntityFilterComponent implements OnInit {

  constructor(private userService: UserService) {}

  @Input()
  private filter: AdEntitiesFilter;
  @Output()
  private onChange: EventEmitter<any> = new EventEmitter();
  private users: DocumentCollection<UserResource> = new DocumentCollection<UserResource>();

  ngOnInit(): void {
    this.userService.getAllServiceLeadersAndFreelancers().subscribe( (response) => {
      this.users = response;
    } );
  }

  emitChange() {
    this.onChange.emit();
  }

  resetFilters() {
    this.filter.name = null;
    this.filter.phone = null;
    this.filter.email = null;
    this.filter.sideOffer = null;
    this.filter.active = null;
    this.filter.userId = null;
    this.emitChange();
  }

  resetSort() {
    this.filter.sort = 'name';
    this.filter.direction = 'asc';
    this.emitChange();
  }

}
