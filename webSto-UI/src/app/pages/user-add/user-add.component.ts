import { Component, OnInit } from '@angular/core';
import {UserService} from '../../api/user.service';
import { Location } from '@angular/common';
import {Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {User} from '../../model/postgres/auth/user';

@Component({
  selector: 'app-user-add',
  templateUrl: './user-add.component.html',
  styleUrls: ['./user-add.component.scss']
})
export class UserAddComponent implements OnInit {

  constructor(private formBuilder: FormBuilder, private userService: UserService, private location: Location, private router: Router,
              private toastrService: ToastrService) {}

  private addForm: FormGroup = this.formBuilder.group({
    email: [null],
    inn: [''],
    phone: ['', Validators.required],
    password: ['', Validators.required],
    rePassword: ['', Validators.required],
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    middleName: ['', Validators.required],
    username: [''],
    moderatorId: [null],
    serviceWorkPrice: [null],
    serviceGoodsCost: [null],
    selectedRole: [null, Validators.required]
  });
  private isRegistering: boolean = false;
  private roles = [
    { name: 'Модератор', id: 'MODERATOR' },
    { name: 'Администратор', id: 'ADMIN' },
    { name: 'Автосервис', id: 'SERVICE_LEADER' }
  ];
  private moderators: User[] = [];

  ngOnInit() {
    // TODO: добавить проверку на админа
    this.userService.getModerators().subscribe( moderators => {
      this.moderators = moderators as User[];
    } );
  }

  onSubmit() {

    if ( this.addForm.invalid ) return;

    if ( this.addForm.controls.selectedRole.value == null ) {
      this.showError( 'Необходимо выбрать роль!' );
      return;
    }

    if ( this.addForm.controls.password.value.length < 6 || this.addForm.controls.rePassword.value.length < 6 ) {
      this.showError( 'Пароль не может содержать менее 6 символов!' );
      return;
    }

    if ( this.addForm.controls.password.value !== this.addForm.controls.rePassword.value ) {
      this.showError( 'Пароли не совпадают!' );
      return;
    }

    this.isRegistering = true;

    this.userService.createUser(this.addForm.value, this.addForm.controls.selectedRole.value)
      .subscribe( data => {
        this.isRegistering = false;
        this.router.navigate(['users']);
        this.toastrService.success('Пользователь успешно добавлен!');
      }, error => {
        this.isRegistering = false;
        if ( error.status === 400 ) {
          this.showError(error.error);
        }
        else {
          this.showError('Ошибка регистрации пользователя!');
        }
      } );
  }

  showError(messageText) {
    this.toastrService.error(messageText, 'Внимание!');
  }

}
