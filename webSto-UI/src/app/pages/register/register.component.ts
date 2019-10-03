import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {UserService} from "../../api/user.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {

  constructor(private formBuilder: FormBuilder,private router: Router, private userService: UserService) { }

  private addForm: FormGroup;
  private invalidRegister: boolean = false;
  private isRegistering: boolean = false;
  private errorMessages:string[] = [];
  private roles = [
    { name: 'Клиент', id: 'CLIENT' },
    { name: 'Руководитель сервиса', id: 'SERVICE_LEADER' },
    { name: 'Модератор', id: 'MODERATOR' },
  ];

  ngOnInit() {
    this.addForm = this.formBuilder.group({
      email: ['', Validators.required],
      inn: [''],
      phone: ['', Validators.required],
      password: ['', Validators.required],
      rePassword: ['', Validators.required],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      middleName: ['', Validators.required],
      selectedRole: [null, Validators.required]
    });

  }

  onSubmit() {

    if ( this.addForm.invalid ) return;

    this.invalidRegister = false;
    this.errorMessages = [];

    if ( this.addForm.controls.selectedRole.value == null )
      this.errorMessages.push( "Необходимо выбрать роль!" );

    if ( this.addForm.controls.password.value.length < 6 || this.addForm.controls.rePassword.value.length < 6 )
      this.errorMessages.push( "Пароль не может содержать менее 6 символов!" );

    if ( this.addForm.controls.password.value !== this.addForm.controls.rePassword.value )
      this.errorMessages.push( "Пароли не совпадают!" );

    if ( this.errorMessages.length > 0 ) {
      this.invalidRegister = true;
      return;
    }

    this.isRegistering = true;

    this.userService.createUser(this.addForm.value, this.addForm.controls.selectedRole.value)
      .subscribe( data => {
        this.isRegistering = false;
        this.router.navigate(['login']);
      }, () => {
        this.isRegistering = false;
      } );
  }

}
