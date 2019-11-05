import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {UserService} from '../../api/user.service';
import {HttpClient} from '@angular/common/http';
import {DocumentResponse} from '../../model/firebird/documentResponse';
import {ToastrService} from 'ngx-toastr';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-report-button',
  templateUrl: './report-button.component.html',
  styleUrls: ['./report-button.component.scss']
})
export class ReportButtonComponent implements OnInit {

  private pdfSrc: any;
  private modalTitle: string;
  private iframe: any;
  private aLink: any;
  @ViewChild('content', {static: false}) private content;
  private isDownloading: boolean = false;
  @Input()
  private withPrint: boolean = true;

  @Input()
  private model: DocumentResponse;
  @Input()
  private compact: boolean = false;

  constructor(private userService: UserService, private httpClient: HttpClient, private toastrService: ToastrService,
              private modalService: NgbModal) { }

  ngOnInit() {
  }

  downloadReport(reportType: string, reportName: string, printStamp: boolean) {
    const headers = this.userService.getHeaders();

    this.isDownloading = true;
    this.httpClient.get(`${this.userService.getApiUrl()}secured/reports/${this.model.id}/${reportType}/${printStamp}`,
      {headers, responseType: 'blob'} ).subscribe( response => {

      this.isDownloading = false;

      this.pdfSrc = window.URL.createObjectURL(new Blob([response], { type: 'application/pdf' }));
      this.modalTitle = reportName;
      if ( this.withPrint )
        this.open(this.content);
      else {
        this.save();
        setTimeout( () => {
          this.clean();
        }, 100 );
      }

    }, error => {
      this.isDownloading = false;
      if ( error.status === 404 )
        this.toastrService.error('Необходимые данные не найдены!', 'Внимание!');
      else
        this.toastrService.error('Ошибка формирования заказ-наряда!', 'Внимание!');
    } );
  }

  save() {
    if ( this.aLink == null ) {
      this.aLink = document.createElement('a');
      this.aLink.href = this.pdfSrc;
      this.aLink.download = `${this.modalTitle} № ${this.model.documentNumber}.pdf`;
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

  open(content) {
    this.modalService.open(content, {windowClass : 'wide-modal'}).result.then( resolve => {
      this.clean();
    }, reject => {
      this.clean();
    } );
  }

  clean() {
    if ( this.aLink != null ) {
      this.aLink.remove();
      this.aLink = null;
    }
    if ( this.iframe != null ) {
      this.iframe.remove();
      this.iframe = null;
    }
    window.URL.revokeObjectURL(this.pdfSrc);
  }
}
