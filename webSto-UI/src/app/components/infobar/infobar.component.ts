import { Component, OnInit, ElementRef } from '@angular/core';
import { ROUTES } from '../sidebar/sidebar.component';
import { Location, LocationStrategy, PathLocationStrategy } from '@angular/common';
import { Router } from '@angular/router';
import {UserService} from "../../api/user.service";

@Component({
  selector: 'app-infobar',
  templateUrl: './infobar.component.html',
  styleUrls: ['./infobar.component.scss'],
  // host: {'class': 'header bg-gradient-danger pb-8 pt-5 pt-md-8'}
})
export class InfobarComponent implements OnInit {

  ngOnInit(): void {
  }

}
