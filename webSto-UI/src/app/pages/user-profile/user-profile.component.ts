import { Component, OnInit } from '@angular/core';
import {UserService} from "../../api/user.service";
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss']
})
export class UserProfileComponent implements OnInit {

  constructor(private userService:UserService) { }

  ngOnInit() {
  }

}
