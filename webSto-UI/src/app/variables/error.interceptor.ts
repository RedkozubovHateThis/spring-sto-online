import { Observable, throwError } from 'rxjs';
import { HttpErrorResponse, HttpEvent, HttpHandler,HttpInterceptor, HttpRequest } from '@angular/common/http';

import { Injectable } from '@angular/core';
import { catchError } from 'rxjs/operators';
import {UserService} from "../api/user.service";
import { Router } from '@angular/router';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

  constructor(private userService: UserService, private router: Router) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request)
      .pipe(
        catchError((err, caught: Observable<HttpEvent<any>>) => {
          if (err instanceof HttpErrorResponse) {
            if ( err.status == 401 ) {
              if ( localStorage.getItem('redirectUrl') == null )
                localStorage.setItem('redirectUrl', this.router.url);
              this.userService.logout();
            }
            else if ( err.status == 403 )
              this.router.navigate(["dashboard"]);
          }
          throw err;
        })
      );
  }

}
