import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {UserService} from '../../api/user.service';
import {HttpClient, HttpParams} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {User} from '../../model/postgres/auth/user';

@Component({
  selector: 'app-password-change-button',
  templateUrl: './password-change-button.component.html',
  styleUrls: ['./password-change-button.component.scss']
})
export class PasswordChangeButtonComponent implements OnInit {

  private isChanging: boolean = false;
  private oldPassword: string;
  private newPassword: string;
  private rePassword: string;

  @Input()
  private model: User;
  @Input()
  private currentUser: User;
  @ViewChild('content', {static: false}) private content;

  constructor(private userService: UserService, private httpClient: HttpClient, private toastrService: ToastrService,
              private modalService: NgbModal) { }

  ngOnInit() {
  }

  changePassword() {
    const headers = this.userService.getHeaders();

    const body = new HttpParams()
      .set('oldPassword', this.oldPassword)
      .set('newPassword', this.newPassword)
      .set('rePassword', this.rePassword);

    this.isChanging = true;
    this.httpClient.post(`${this.userService.getApiUrl()}users/${this.model.id}/password/change`,
      body, {headers} ).subscribe( response => {
      this.isChanging = false;

      this.oldPassword = null;
      this.newPassword = null;
      this.rePassword = null;

      this.toastrService.success('Пароль успешно изменен!');
    }, error => {
      this.isChanging = false;
      if ( [400, 404, 403].includes(error.status) )
        this.toastrService.error(error.error, 'Внимание!');
      else
        this.toastrService.error('Ошибка изменения пароля!', 'Внимание!');
    } );
  }

  open(content) {
    this.modalService.open(content);
  }
}
