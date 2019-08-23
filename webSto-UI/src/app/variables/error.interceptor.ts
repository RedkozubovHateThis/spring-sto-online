import { Observable, throwError } from 'rxjs';
import { HttpErrorResponse, HttpEvent, HttpHandler,HttpInterceptor, HttpRequest } from '@angular/common/http';

import { Injectable } from '@angular/core';
import { catchError } from 'rxjs/operators';
import {ApiService} from "../api/api.service";

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

  constructor(private apiService:ApiService) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request)
      .pipe(
        catchError((err, caught: Observable<HttpEvent<any>>) => {
          if (err instanceof HttpErrorResponse && err.status == 401) {
            this.apiService.logout();
          }
          throw err;
        })
      );
  }

}
