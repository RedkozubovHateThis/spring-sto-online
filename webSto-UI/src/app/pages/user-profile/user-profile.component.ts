import {Component, OnInit} from '@angular/core';
import {UserService} from '../../api/user.service';
import {User} from '../../model/postgres/auth/user';
import {Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {Shops} from '../../variables/shops';
import {UserResource} from '../../model/resource/user.resource.service';

@Component({
  selector: 'app-user-profile',
  templateUrl: './../user/user.component.html',
  styleUrls: ['./../user/user.component.scss']
})
export class UserProfileComponent implements OnInit {

  private isLoading: boolean = false;
  private model: UserResource;
  private title: string = "Профиль";
  private showBack: boolean = false;
  private shops: ShopInterface[] = [];

  constructor(private userService: UserService,
              private router: Router, private httpClient: HttpClient,
              private toastrService: ToastrService) {
    this.shops = Shops.shops;
  }

  ngOnInit() {

    if ( this.userService.currentUser == null ) {

      const subscription = this.userService.currentUserIsLoaded.subscribe( () => {
        this.ngOnInit();
        subscription.unsubscribe();
      } );

      return;

    }

    this.model = this.userService.currentUser;
  }

  private navigate(user: UserResource) {
    this.userService.setTransferModel( user );
    this.router.navigate(['/users', user.id, 'edit']);
  }

}
