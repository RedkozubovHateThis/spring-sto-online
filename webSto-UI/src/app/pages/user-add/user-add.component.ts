import {Component, OnInit} from '@angular/core';
import {UserService} from '../../api/user.service';
import {Location} from '@angular/common';
import {Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {DocumentCollection} from 'ngx-jsonapi';
import {UserRoleResource, UserRoleResourceService} from '../../model/resource/user-role.resource.service';
import {UserResource, UserResourceService} from '../../model/resource/user.resource.service';
import {ProfileResourceService} from '../../model/resource/profile.resource.service';

@Component({
  selector: 'app-user-add',
  templateUrl: '../user-edit/user-edit.component.html',
  styleUrls: ['../user-edit/user-edit.component.scss']
})
export class UserAddComponent implements OnInit {

  constructor(private formBuilder: FormBuilder, private userService: UserService, private location: Location, private router: Router,
              private toastrService: ToastrService, private userResourceService: UserResourceService,
              private userRoleResourceService: UserRoleResourceService, private profileResourceService: ProfileResourceService) {
    this.model = userResourceService.new();
    this.model.addRelationship( profileResourceService.new(), 'profile' );
  }

  private roles: DocumentCollection<UserRoleResource> = new DocumentCollection<UserRoleResource>();
  private password = '';
  private rePassword = '';
  private model: UserResource;
  private selectedRole: UserRoleResource;

  ngOnInit() {
    this.requestRoles();
  }

  requestRoles() {
    this.userRoleResourceService.all().subscribe( (roles) => {
      this.roles = roles;
    } );
  }

  setRole() {
    if ( this.selectedRole ) {
      this.model.addRelationships( [ this.selectedRole ], 'roles' );
    }
  }

  save() {
    if ( this.model.relationships.roles.data.length === 0 ) {
      this.showError( 'Необходимо выбрать роль!' );
      return;
    }
    if ( this.password.length < 6 || this.rePassword.length < 6 ) {
      this.showError( 'Пароль не может содержать менее 6 символов!' );
      return;
    }
    if ( this.password !== this.rePassword ) {
      this.showError( 'Пароли не совпадают!' );
      return;
    }
    this.model.attributes.rawPassword = this.password;
    this.userService.saveUser( this.model, 'Пользователь успешно сохранен!' );
  }

  // onSubmit() {
  //
  //   if ( this.addForm.invalid ) return;
  //
  //   if ( this.addForm.controls.selectedRole.value == null ) {
  //     this.showError( 'Необходимо выбрать роль!' );
  //     return;
  //   }
  //
  //   if ( this.addForm.controls.password.value.length < 6 || this.addForm.controls.rePassword.value.length < 6 ) {
  //     this.showError( 'Пароль не может содержать менее 6 символов!' );
  //     return;
  //   }
  //
  //   if ( this.addForm.controls.password.value !== this.addForm.controls.rePassword.value ) {
  //     this.showError( 'Пароли не совпадают!' );
  //     return;
  //   }
  //
  //   this.isRegistering = true;
  //
  //   this.userService.createUser(this.addForm.value, this.addForm.controls.selectedRole.value)
  //     .subscribe( data => {
  //       this.isRegistering = false;
  //       this.router.navigate(['users']);
  //       this.toastrService.success('Пользователь успешно добавлен!');
  //     }, error => {
  //       this.isRegistering = false;
  //       if ( error.status === 400 ) {
  //         this.showError(error.error);
  //       }
  //       else {
  //         this.showError('Ошибка регистрации пользователя!');
  //       }
  //     } );
  // }
  //
  showError(messageText) {
    this.toastrService.error(messageText, 'Внимание!');
  }

}
