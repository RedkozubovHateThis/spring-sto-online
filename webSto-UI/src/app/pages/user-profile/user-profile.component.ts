import {Component, OnInit} from '@angular/core';
import {UserService} from '../../api/user.service';
import {User} from '../../model/postgres/auth/user';
import {Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {Shops} from '../../variables/shops';

@Component({
  selector: 'app-user-profile',
  templateUrl: './../user/user.component.html',
  styleUrls: ['./../user/user.component.scss']
})
export class UserProfileComponent implements OnInit {

  private isLoading: boolean = false;
  private model: User;
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

  private navigate(user: User) {
    this.userService.setTransferModel( user );
    this.router.navigate(['/users', user.id, 'edit']);
  }

}
