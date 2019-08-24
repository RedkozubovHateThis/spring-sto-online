import { Component, OnInit } from '@angular/core';
import {Router} from "@angular/router";
import {UserService} from "../../api/user.service";

@Component({
  selector: 'app-admin-layout',
  templateUrl: './admin-layout.component.html',
  styleUrls: ['./admin-layout.component.scss']
})
export class AdminLayoutComponent implements OnInit {

  constructor(router:Router, userService:UserService) {
    // within our constructor we can define our subscription
    // and then decide what to do when this event is triggered.
    // in this case I simply update my route string.
    router.events.subscribe(val => {
      if (location.hash != "") {
        if ( !userService.isAuthenticated &&
          !location.hash.includes( 'login' ) &&
          !location.hash.includes( 'register' ) ) {

          userService.getUserFromStorage();

          if ( !userService.isAuthenticated )
            router.navigate(['login']);

        }
      }
    });
  }

  ngOnInit() {
  }

}
