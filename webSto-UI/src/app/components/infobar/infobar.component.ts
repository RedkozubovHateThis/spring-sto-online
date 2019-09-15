import {Component, OnInit, ElementRef, Input} from '@angular/core';
import {DocumentResponseService} from "../../api/documentResponse.service";
import {UserService} from '../../api/user.service';
import {User} from '../../model/postgres/auth/user';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-infobar',
  templateUrl: './infobar.component.html',
  styleUrls: ['./infobar.component.scss'],
})
export class InfobarComponent implements OnInit {

  constructor(private documentResponseService: DocumentResponseService, private userService: UserService) {}

  private documentsCount: number = 0;
  private documentsCountLoading: boolean = false;
  private documents2Count: number = 0;
  private documents2CountLoading: boolean = false;
  private documents4Count: number = 0;
  private documents4CountLoading: boolean = false;
  private usersCount: number = 0;
  private usersCountLoading: boolean = false;
  private usersNotApprovedCount: number = 0;
  private usersNotApprovedCountLoading: boolean = false;
  private currentUser: User;

  ngOnInit(): void {

    if ( this.userService.currentUser == null ) {
      const subscription: Subscription = this.userService.currentUserIsLoaded.subscribe( currentUser => {
        this.currentUser = currentUser;
        this.ngOnInit();
        subscription.unsubscribe();
      } );

      return;
    }

    this.getDocumentsCount();
    this.getUsersCount();
    this.getUsersNotApprovedCount();
    this.getDocumentsCountByState();
  }

  getDocumentsCount() {
    this.documentsCountLoading = true;

    this.documentResponseService.getDocumentsCount().subscribe( result => {
      this.documentsCount = result as number;
      this.documentsCountLoading = false;
    }, () => {
      this.documentsCountLoading = false;
    } );
  }

  getDocumentsCountByState() {
    this.documents2CountLoading = true;
    this.documents4CountLoading = true;

    this.documentResponseService.getDocumentsCountByState(2).subscribe( result => {
      this.documents2Count = result as number;
      this.documents2CountLoading = false;
    }, () => {
      this.documents2CountLoading = false;
    } );

    this.documentResponseService.getDocumentsCountByState(4).subscribe( result => {
      this.documents4Count = result as number;
      this.documents4CountLoading = false;
    }, () => {
      this.documents4CountLoading = false;
    } );
  }

  getUsersCount() {
    this.usersCountLoading = true;

    this.userService.getUsersCount(false).subscribe( result => {
      this.usersCount = result as number;
      this.usersCountLoading = false;
    }, () => {
      this.usersCountLoading = false;
    } );
  }

  getUsersNotApprovedCount() {
    this.usersNotApprovedCountLoading = true;

    this.userService.getUsersCount(true).subscribe( result => {
      this.usersNotApprovedCount = result as number;
      this.usersNotApprovedCountLoading = false;
    }, () => {
      this.usersNotApprovedCountLoading = false;
    } );
  }

}
