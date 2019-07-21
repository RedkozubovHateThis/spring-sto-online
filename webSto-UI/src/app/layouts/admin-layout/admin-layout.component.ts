import { Component, OnInit } from '@angular/core';
import {Router} from "@angular/router";
import {ApiService} from "../../api/api.service";

@Component({
  selector: 'app-admin-layout',
  templateUrl: './admin-layout.component.html',
  styleUrls: ['./admin-layout.component.scss']
})
export class AdminLayoutComponent implements OnInit {

  constructor(router:Router, apiService:ApiService) {
    // within our constructor we can define our subscription
    // and then decide what to do when this event is triggered.
    // in this case I simply update my route string.
    router.events.subscribe(val => {
      if (location.hash != "") {
        if ( !apiService.isAuthenticated &&
          !location.hash.includes( 'login' ) &&
          !location.hash.includes( 'register' ) ) {

          router.navigate(['login']);

        }
      }
    });
  }

  ngOnInit() {
  }

}
