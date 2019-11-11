import { Component, OnInit } from '@angular/core';
import {HttpParams} from "@angular/common/http";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {UserService} from "../../api/user.service";
import {ActivatedRoute, Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-restore',
  templateUrl: './restore.component.html',
  styleUrls: ['./restore.component.scss']
})
export class RestoreComponent implements OnInit {

  private restoreForm: FormGroup;
  private passwordForm: FormGroup;
  private isRestoring: boolean = false;
  private hashFound: boolean = false;
  private hash: string;
  constructor(private formBuilder: FormBuilder, private router: Router, private route: ActivatedRoute, private userService: UserService, private toastrService: ToastrService) { }

  onRestoreSubmit() {
    localStorage.removeItem('demoDomain');

    if (this.restoreForm.invalid) return;

    this.isRestoring = true;

    const body = new HttpParams()
      .set('restoreData', this.restoreForm.controls.username.value);

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

  onPasswordSubmit() {
    localStorage.removeItem('demoDomain');

    if (this.passwordForm.invalid) return;

    this.isRestoring = true;

    const password = this.passwordForm.controls.password.value;
    const rePassword = this.passwordForm.controls.rePassword.value;

    const body = new HttpParams()
      .set('password', password)
      .set('rePassword', rePassword)
      .set('hash', this.hash);

    this.userService.restorePassword(body)
      .subscribe(data => {
        this.isRestoring = false;
        // @ts-ignore
        this.toastrService.success( data.responseText );
        this.router.navigate(['/login']);
    }, error => {
        this.isRestoring = false;
        if ( error.error.responseText )
          this.showError(error.error.responseText);
        else
          this.showError('Ошибка восстановления пароля!');
    });
  }

  ngOnInit() {
    this.hash = this.route.snapshot.queryParamMap.get('hash');
    if ( this.hash )
      this.hashFound = true;

    this.restoreForm = this.formBuilder.group({
      username: ['', Validators.required]
    });
    this.passwordForm = this.formBuilder.group({
      password: ['', Validators.required],
      rePassword: ['', Validators.required]
    });
  }

  showError(messageText) {
    this.toastrService.error(messageText, 'Внимание!');
  }

}
