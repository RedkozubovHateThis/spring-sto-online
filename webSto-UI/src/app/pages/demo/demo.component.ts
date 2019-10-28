import { Component, OnInit } from '@angular/core';
import {UserService} from "../../api/user.service";
import {ToastrService} from 'ngx-toastr';
import {HttpParams} from '@angular/common/http';

@Component({
  selector: 'app-demo',
  templateUrl: './demo.component.html',
  styleUrls: ['./demo.component.scss']
})
export class DemoComponent implements OnInit {

  constructor(private userService: UserService, private toastrService: ToastrService) { }

  ngOnInit() {
    this.registerDemo();
  }

  registerDemo() {
    localStorage.setItem('demoDomain', 'true');

    this.userService.createDemoUser()
      .subscribe( credentials => {
        const body = new HttpParams()
          // @ts-ignore
          .set('username', credentials.username)
          // @ts-ignore
          .set('password', credentials.password)
          .set('grant_type', 'password');
        this.userService.login(body.toString())
          .subscribe(data => {
            localStorage.setItem('token', JSON.stringify(data));

            this.userService.authenticate();
          }, error => {
            this.showError('Ошибка входа в демо-режим!');
          });
      }, error => {
        this.showError('Ошибка входа в демо-режим!');
      } );
  }

  showError(messageText) {
    this.toastrService.error(messageText, 'Внимание!');
  }

}
