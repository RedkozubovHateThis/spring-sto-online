import { Component, OnInit } from '@angular/core';
import {HttpParams} from "@angular/common/http";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {UserService} from "../../api/user.service";
import {Router} from "@angular/router";
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-restore',
  templateUrl: './restore.component.html',
  styleUrls: ['./restore.component.scss']
})
export class RestoreComponent implements OnInit {

  private loginForm: FormGroup;
  private isRestoring: boolean = false;
  constructor(private formBuilder: FormBuilder, private router: Router, private userService: UserService, private toastrService: ToastrService) { }

  onSubmit() {
    localStorage.removeItem('demoDomain');

    if (this.loginForm.invalid) return;

    this.isRestoring = true;

    const body = new HttpParams()
      .set('restoreData', this.loginForm.controls.username.value);

    this.userService.restore(body)
      .subscribe(data => {
        this.isRestoring = false;
        // @ts-ignore
        this.toastrService.success( data.responseText );
    }, error => {
        this.isRestoring = false;
        if ( error.error.responseText )
          this.showError(error.error.responseText);
        else
          this.showError('Ошибка восстановления пароля!');
    });
  }

  ngOnInit() {
    this.loginForm = this.formBuilder.group({
      username: ['', Validators.required]
    });
  }

  showError(messageText) {
    this.toastrService.error(messageText, 'Внимание!');
  }

}
