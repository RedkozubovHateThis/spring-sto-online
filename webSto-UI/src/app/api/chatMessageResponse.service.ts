import {Injectable, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import { UserService } from './user.service';
import {User} from '../model/postgres/auth/user';
import {OpponentResponse} from '../model/postgres/opponentResponse';

@Injectable()
export class ChatMessageResponseService {

  constructor(private http: HttpClient, private router: Router, private userService: UserService) { }
  baseUrl = 'http://localhost:8181/';

  getMessages(toUserId: number) {
    const headers = this.userService.getHeaders();

    return this.http.get( `${this.baseUrl}secured/chat/messages?toUserId=${toUserId}`, {headers} );
  }

  saveMessage(opponent: OpponentResponse, messageText: string, uploadFileId: number) {
    const headers = this.userService.getHeaders();

    return this.http.put( `${this.baseUrl}secured/chat`, {
      toUserId: opponent.id,
      uploadFileId,
      messageText
    }, {headers} );
  }

}
