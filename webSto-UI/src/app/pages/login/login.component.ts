import {Component, OnInit} from '@angular/core';
import {HttpParams} from '@angular/common/http';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {UserService} from '../../api/user.service';
import {Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  private loginForm: FormGroup;
  private isLoggingIn: boolean = false;
  constructor(private formBuilder: FormBuilder, private router: Router, private userService: UserService, private toastrService: ToastrService) { }

  onSubmit() {
    localStorage.removeItem('demoDomain');

    if (this.loginForm.invalid) return;

    this.isLoggingIn = true;

    const body = new HttpParams()
      .set('username', this.loginForm.controls.username.value)
      .set('password', this.loginForm.controls.password.value)
      .set('grant_type', 'password');

    this.userService.login(body.toString())
      .subscribe(data => {
      localStorage.setItem('token', JSON.stringify(data));
      this.isLoggingIn = false;

      this.userService.authenticate();
    }, error => {
      this.isLoggingIn = false;
      this.showError('Неправильные телефон/почта или пароль!');
    });
  }

  ngOnInit() {
    this.loginForm = this.formBuilder.group({
      username: ['', Validators.compose([Validators.required])],
      password: ['', Validators.required]
    });
  }

  showError(messageText) {
    this.toastrService.error(messageText, 'Внимание!');
  }

}
