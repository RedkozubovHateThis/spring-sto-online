import { Component, OnInit } from '@angular/core';
import {ApiService} from "../../api/api.service";
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss']
})
export class UserProfileComponent implements OnInit {

  private apiService:ApiService;

  constructor(apiService:ApiService) {
    this.apiService = apiService;
  }

  ngOnInit() {
  }

}
