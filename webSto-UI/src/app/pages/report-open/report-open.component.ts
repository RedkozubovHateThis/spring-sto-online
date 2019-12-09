import { Component, OnInit } from '@angular/core';
import {UserService} from "../../api/user.service";
import {ActivatedRoute, Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-login',
  templateUrl: './report-open.component.html',
  styleUrls: ['./report-open.component.scss']
})
export class ReportOpenComponent implements OnInit {

  private pdfSrc: any;
  private iframe: any;
  private aLink: any;
  private isDownloading: boolean = false;

  constructor(private userService: UserService, private activatedRoute: ActivatedRoute, private router: Router,
              private toastrService: ToastrService) { }

  ngOnInit() {
    const uuid = this.activatedRoute.snapshot.queryParamMap.get('uuid');

    if ( uuid == null ) this.router.navigate(['login']);

    this.isDownloading = true;
    this.userService.requestOpenReport(uuid).subscribe( response => {
      this.pdfSrc = window.URL.createObjectURL(new Blob([response], { type: 'application/pdf' }));
      this.isDownloading = false;
    }, () => {
      this.toastrService.error('Отчет по заказ-наряду не найден!', 'Внимание!');
    } );
  }

  save() {
    if ( this.aLink == null ) {
      this.aLink = document.createElement('a');
      this.aLink.href = this.pdfSrc;
      this.aLink.download = `Заказ-наряд.pdf`;
    }
    this.aLink.dispatchEvent(new MouseEvent('click', { bubbles: true, cancelable: true, view: window }));
  }

  print() {
    if ( this.iframe == null ) {
      this.iframe = document.createElement('iframe');
      this.iframe.style.display = 'none';
      this.iframe.src = this.pdfSrc;
      document.body.appendChild(this.iframe);
    }
    this.iframe.contentWindow.print();
  }

}
