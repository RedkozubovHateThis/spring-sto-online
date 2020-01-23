import { Component, OnInit } from '@angular/core';
import {Router, NavigationEnd} from "@angular/router";
import {UserService} from "../../api/user.service";
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

    paymentService.subscribeToUserLoaded();

    router.events.subscribe(val => {

      if ( val instanceof NavigationEnd ) {

        if ( !location.pathname.includes( '/login' ) &&
          !location.pathname.includes( '/register' ) &&
          !location.pathname.includes( '/restore' ) ) {

          if ( !userService.isAuthenticated() ) {

            if ( userService.isTokenExists() )
              userService.getCurrentUser();
            else {
              localStorage.setItem('redirectUrl', val.url);
              router.navigate(['login']);
            }

          }
          else if ( userService.isAuthenticated() )
            userService.getCurrentUser();

        }

      }
    });
  }

  ngOnInit() {
    this.webSocketService.connect();
  }

}
