import {Observable} from 'rxjs';
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpHeaders, HttpInterceptor, HttpRequest} from '@angular/common/http';

import {Injectable} from '@angular/core';
import {catchError} from 'rxjs/operators';
import {UserService} from '../api/user.service';
import {Router} from '@angular/router';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

  constructor(private userService: UserService, private router: Router) { }

  private addExtraHeaders(headers: HttpHeaders): HttpHeaders {
    headers = headers.append('Authorization', `Bearer ${JSON.parse(localStorage.getItem('token')).access_token}`);
    return headers;
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    let modified;

    const ignoreAuth = request.url.includes('/api/oauth/token') || request.url.includes('/api/oauth/restore')
      || request.url.includes('/api/oauth/restore/password') || request.url.includes('/api/open/report/compiled');

    if ( !ignoreAuth && this.userService.isTokenExists() ) {
      modified = request.clone({
        headers: this.addExtraHeaders(request.headers)
      });
    }
    else {
      modified = request;
    }

    return next.handle(modified)
      .pipe(
        catchError((err, caught: Observable<HttpEvent<any>>) => {
          if (err instanceof HttpErrorResponse) {
            if ( err.status == 401 ) {
              if ( localStorage.getItem('redirectUrl') == null )
                localStorage.setItem('redirectUrl', this.router.url);
              this.userService.logout();
            }
            else if ( err.status == 403 )
              this.router.navigate(["documents"]);
          }
          throw err;
        })
      );
  }

}
