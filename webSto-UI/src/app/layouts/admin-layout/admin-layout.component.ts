import { Component, OnInit } from '@angular/core';
import {Router, NavigationEnd} from "@angular/router";
import {UserService} from "../../api/user.service";
import {WebSocketService} from '../../api/webSocket.service';

@Component({
  selector: 'app-admin-layout',
  templateUrl: './admin-layout.component.html',
  styleUrls: ['./admin-layout.component.scss']
})
export class AdminLayoutComponent implements OnInit {

  constructor(private router: Router, private userService: UserService, private webSocketService: WebSocketService) {
    router.events.subscribe(val => {

      if ( val instanceof NavigationEnd && location.hash !== '' ) {

        if ( !location.hash.includes( 'login' ) &&
          !location.hash.includes( 'register' ) ) {

          if ( !userService.isAuthenticated() ) {

            if ( userService.isTokenExists() )
              userService.getCurrentUser();
            else
              router.navigate(['login']);

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
