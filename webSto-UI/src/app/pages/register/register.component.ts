import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {UserService} from "../../api/user.service";
import {Router} from "@angular/router";
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {

  constructor(private formBuilder: FormBuilder, private router: Router, private userService: UserService,
              private toastrService: ToastrService) { }

  private addForm: FormGroup;
  private isRegistering: boolean = false;
  private selectedRole = 'CLIENT';
  private roles = [
    { name: 'Клиент', id: 'CLIENT' },
    { name: 'Руководитель сервиса', id: 'SERVICE_LEADER' },
    { name: 'Самозанятый', id: 'FREELANCER' },
    // { name: 'Модератор', id: 'MODERATOR' },
  ];

  ngOnInit() {
    this.addForm = this.formBuilder.group({
      email: [null],
      inn: [''],
      vin: [''],
      phone: ['', Validators.required],
      password: ['', Validators.required],
      rePassword: ['', Validators.required],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      middleName: ['', Validators.required]
    });

  }

  setSelectedRole(role) {
    this.selectedRole = role;
  }

  onSubmit() {
    localStorage.removeItem('demoDomain');

    if ( this.addForm.invalid ) return;

    if ( this.addForm.controls.password.value.length < 6 || this.addForm.controls.rePassword.value.length < 6 ) {
      this.showError( 'Пароль не может содержать менее 6 символов!' );
      return;
    }

    if ( this.addForm.controls.password.value !== this.addForm.controls.rePassword.value ) {
      this.showError( 'Пароли не совпадают!' );
      return;
    }

    this.isRegistering = true;

    this.userService.createUser(this.addForm.value, this.selectedRole)
      .subscribe( data => {
        this.isRegistering = false;
        this.toastrService.success('Регистрация успешно завершена!');
        this.router.navigate(['login']);
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
