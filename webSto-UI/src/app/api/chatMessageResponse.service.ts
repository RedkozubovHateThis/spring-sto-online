import {Injectable, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import { UserService } from './user.service';
import {OpponentResponse} from '../model/postgres/opponentResponse';

@Injectable()
export class ChatMessageResponseService {

  constructor(private http: HttpClient, private router: Router, private userService: UserService) { }

  getMessages(toUserId: number) {
    const headers = this.userService.getHeaders();

    return this.http.get( `${this.userService.getApiUrl()}secured/chat/messages?toUserId=${toUserId}`, {headers} );
  }

  saveMessage(opponent: OpponentResponse, messageText: string, uploadFileId: number) {
    const headers = this.userService.getHeaders();

    return this.http.put( `${this.userService.getApiUrl()}secured/chat`, {
      toUserId: opponent.id,
      uploadFileId,
      messageText
    }, {headers} );
  }

  createGreetMessage() {
    const headers = this.userService.getHeaders();

    this.http.get( `${this.userService.getApiUrl()}secured/chat/greet`, {headers} ).subscribe( () => {} );
  }

}
