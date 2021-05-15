import {Component, ElementRef, OnInit} from '@angular/core';
import {Location} from '@angular/common';
import {Router} from '@angular/router';
import {UserService} from '../../api/user.service';
import {EventMessageService} from '../../api/event-message.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {
  public focus;
  public listTitles: any[];
  public location: Location;
  constructor(location: Location,  private element: ElementRef, private router: Router, private userService: UserService,
              private eventMessageResponseService: EventMessageService) {
    this.location = location;
  }

  ngOnInit() {
  }

  getUsername() {
    return this.userService.currentUser ? this.userService.currentUser.getTitle() : '';
  }
  logout() {
    this.userService.logout(false);
  }

}
