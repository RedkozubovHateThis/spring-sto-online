import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import {UserService} from "../../api/user.service";
import {EventMessageResponseService} from '../../api/eventMessageResponse.service';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {

  public isCollapsed = true;

  constructor(private router: Router, private userService: UserService,
              private eventMessageResponseService: EventMessageResponseService) { }

  ngOnInit() {
    this.router.events.subscribe((event) => {
      this.isCollapsed = true;
   });
  }
}
