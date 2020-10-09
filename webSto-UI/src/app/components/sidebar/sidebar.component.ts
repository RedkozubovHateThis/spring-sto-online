import {AfterViewChecked, AfterViewInit, Component, ElementRef, Inject, OnInit, Renderer2, ViewChild} from '@angular/core';
import {Router} from '@angular/router';
import {UserService} from '../../api/user.service';
import {EventMessageService} from '../../api/event-message.service';
import {DOCUMENT} from '@angular/common';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit, AfterViewInit {

  public isCollapsed = true;

  @ViewChild('adScript', {static: true}) script: ElementRef;

  constructor(private router: Router, private userService: UserService,
              private eventMessageResponseService: EventMessageService,
              @Inject(DOCUMENT) private document: Document,
              private renderer2: Renderer2) { }

  ngOnInit() {
    this.router.events.subscribe((event) => {
      this.isCollapsed = true;
   });
  }
  logout() {
    this.userService.logout(false);
  }
  ngAfterViewInit(): void {
    setTimeout( () => {
      let subID = "carcam-ad-banner";  // - local banner key;
      let injectTo = "carcam-ad-row";  // - #id of html element (ex., "top-banner").
      let subid_block = "";  // - #id of html element (ex., "top-banner").

      if(injectTo=="")injectTo="admitad_shuffle"+subID+Math.round(Math.random()*100000000);
      if(subID=='')subid_block=''; else subid_block='subid/'+subID+'/';
      // document.write('<div id="'+injectTo+'"></div>');
      var s = document.createElement('script');
      s.type = 'text/javascript'; s.async = true;
      s.src = 'https://ad.admitad.com/shuffle/211ca76b43/'+subid_block+'?inject_to='+injectTo;
      var x = document.getElementsByTagName('script')[0];
      console.log(x);
      x.parentNode.insertBefore(s, x);
    }, 2000 );
  }
}
