import {AfterViewChecked, AfterViewInit, Component, OnInit} from '@angular/core';
import {NavigationEnd, Router} from '@angular/router';
import {UserService} from '../../api/user.service';
import {WebSocketService} from '../../api/webSocket.service';
import {PaymentService} from '../../api/payment.service';

@Component({
  selector: 'app-admin-layout',
  templateUrl: './admin-layout.component.html',
  styleUrls: ['./admin-layout.component.scss']
})
export class AdminLayoutComponent implements OnInit, AfterViewInit {

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

  ngAfterViewInit() {
    setTimeout( () => {
      // tslint:disable-next-line:only-arrow-functions
      (function(w, d, n, s, t) {
        w[n] = w[n] || [];
        // tslint:disable-next-line:only-arrow-functions
        w[n].push(function() {
          // @ts-ignore
          Ya.Context.AdvManager.render({
            blockId: "R-A-624145-1",
            renderTo: "yandex_rtb_R-A-624145-1",
            async: true
          });
        });
        t = d.getElementsByTagName("script")[0];
        s = d.createElement("script");
        s.type = "text/javascript";
        s.src = "//an.yandex.ru/system/context.js";
        s.async = true;
        t.parentNode.insertBefore(s, t);
      })(window, window.document, "yandexContextAsyncCallbacks");
    }, 2000 );
  }

  requestCurrentUser() {
    this.userService.getCurrentUser();
  }

}
