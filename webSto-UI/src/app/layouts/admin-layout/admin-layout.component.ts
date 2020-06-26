import {Component, OnInit} from '@angular/core';
import {NavigationEnd, Router} from '@angular/router';
import {UserService} from '../../api/user.service';
import {WebSocketService} from '../../api/webSocket.service';
import {PaymentService} from '../../api/payment.service';

@Component({
  selector: 'app-admin-layout',
  templateUrl: './admin-layout.component.html',
  styleUrls: ['./admin-layout.component.scss']
})
export class AdminLayoutComponent implements OnInit {

  constructor(private router: Router, private userService: UserService, private webSocketService: WebSocketService,
              private paymentService: PaymentService) {

    const splashScreen = document.getElementById("loading-splash");
    if ( splashScreen )
      splashScreen.remove();

    // this.requestCurrentUser();

    // paymentService.subscribeToUserLoaded();

    router.events.subscribe(val => {

      if ( val instanceof NavigationEnd ) {

        if ( !location.pathname.includes( '/login' ) &&
          !location.pathname.includes( '/register' ) &&
          !location.pathname.includes( '/restore' ) ) {

          if ( !userService.isAuthenticated() ) {

            if ( userService.isTokenExists() )
              this.requestCurrentUser();
            else {
              localStorage.setItem('redirectUrl', val.url);
              router.navigate(['login']);
            }

          }
          else if ( userService.isAuthenticated() )
            this.requestCurrentUser();

        }

      }
    });
  }

  ngOnInit() {
    this.webSocketService.connect();
  }

  requestCurrentUser() {
    this.userService.getCurrentUser();
  }

}
