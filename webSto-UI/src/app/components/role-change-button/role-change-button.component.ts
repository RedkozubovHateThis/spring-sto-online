import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {UserService} from '../../api/user.service';
import {HttpClient, HttpParams} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {User} from '../../model/postgres/auth/user';

@Component({
  selector: 'app-role-change-button',
  templateUrl: './role-change-button.component.html',
  styleUrls: ['./role-change-button.component.scss']
})
export class RoleChangeButtonComponent implements OnInit {

  private isChanging: boolean = false;
  private selectedRole: string;

  private roles = [
    { name: 'Автовладелец', id: 'CLIENT', onlyAdmin: false },
    { name: 'Автосервис', id: 'SERVICE_LEADER', onlyAdmin: false },
    { name: 'Администратор', id: 'ADMIN', onlyAdmin: true }
  ];

  @Input()
  private model: User;
  @Input()
  private currentUser: User;
  @Output()
  private onRoleChange: EventEmitter<void> = new EventEmitter();
  @ViewChild('content', {static: false}) private content;

  constructor(private userService: UserService, private httpClient: HttpClient, private toastrService: ToastrService,
              private modalService: NgbModal) { }

  ngOnInit() {
    console.log(this.model);
    this.selectedRole = this.model.roles[0].name;
  }

  isContains(role) {
    const containedRole = this.model.roles.find( r => r.name === role );
    return containedRole != null;
  }

  changeRole() {
    const headers = this.userService.getHeaders();

    const body = new HttpParams()
      .set('role', this.selectedRole);

    this.isChanging = true;
    this.httpClient.post(`${this.userService.getApiUrl()}users/${this.model.id}/role/change`,
      body, {headers} ).subscribe( response => {
      this.isChanging = false;
      this.onRoleChange.emit();

      this.toastrService.success('Роль успешно изменена!');
    }, error => {
      this.isChanging = false;
      if ( error.error.responseText )
        this.toastrService.error(error.error.responseText, 'Внимание!');
      else
        this.toastrService.error('Ошибка изменения роли!', 'Внимание!');
    } );
  }

  open(content) {
    this.modalService.open(content);
  }
}
