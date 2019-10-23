import {Injectable, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import { UserService } from './user.service';
import {User} from '../model/postgres/auth/user';
import {OpponentResponse} from '../model/postgres/opponentResponse';
import {EventMessageResponse} from '../model/postgres/eventMessageResponse';

@Injectable()
export class EventMessageResponseService {

  constructor(private http: HttpClient, private router: Router, private userService: UserService) { }
  baseUrl = 'http://localhost:8181/';

  public isLoading: boolean = false;
  public messages: EventMessageResponse[] = [];

  getMessages() {
    const headers = this.userService.getHeaders();

    this.isLoading = true;
    return this.http.get( `${this.baseUrl}secured/eventMessages/findAll`, {headers} ).subscribe( response => {

      this.isLoading = false;
      this.messages = response as EventMessageResponse[];

    }, error => {
      this.isLoading = false;
    } );
  }

}
