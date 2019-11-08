import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {UserService} from '../../api/user.service';
import {HttpClient} from '@angular/common/http';
import {DomSanitizer} from '@angular/platform-browser';
import {ChatMessageResponse} from '../../model/postgres/chatMessageResponse';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-img-preview',
  templateUrl: './img-preview.component.html',
  styleUrls: ['./img-preview.component.scss'],
})
export class ImgPreviewComponent implements OnInit {

  constructor(private userService: UserService, private httpClient: HttpClient, private domSanitizer: DomSanitizer,
              private toastrService: ToastrService) {}
  @Input()
  private message: ChatMessageResponse;
  @Output()
  private onImageLoad: EventEmitter<any> = new EventEmitter();
  private src: any;
  private isImageLoading: boolean = false;
  private isDownloading: boolean = false;

  ngOnInit(): void {

    if ( !this.message.uploadFileUuid || !this.message.uploadFileIsImage ) return;

    const headers = this.userService.getHeaders();

    this.isImageLoading = true;
    this.httpClient.get(`${this.userService.getApiUrl()}secured/files/${this.message.uploadFileUuid}/preview`,
      {headers, responseType: 'blob'} ).subscribe( blob => {
      this.src = this.domSanitizer.bypassSecurityTrustUrl( URL.createObjectURL( blob ) );
      this.isImageLoading = false;
      // TODO: подумать над этим костылем, попробовать перенести на ngAfterViewChecked
      setTimeout( () => {
        this.onImageLoad.emit();
      }, 100 );
    }, error => {
      this.isImageLoading = false;
    } );

  }

  downloadFile() {
    if ( !this.message.uploadFileUuid || this.isDownloading ) return;

    const headers = this.userService.getHeaders();

    this.isDownloading = true;
    this.httpClient.get(`${this.userService.getApiUrl()}secured/files/${this.message.uploadFileUuid}/download`,
      {headers, responseType: 'blob'} ).subscribe( blob => {

        this.isDownloading = false;

        const data = window.URL.createObjectURL(blob);

        const link = document.createElement('a');
        link.href = data;
        link.download = this.message.uploadFileName;
        link.dispatchEvent(new MouseEvent('click', { bubbles: true, cancelable: true, view: window }));

        setTimeout( () => {
          window.URL.revokeObjectURL(data);
          link.remove();
        }, 100 );

    }, error => {
      this.isDownloading = false;
      if ( error.status === 404 )
        this.toastrService.error('Файл не найден!', 'Внимание!');
      else
        this.toastrService.error('Ошибка скачивания файла!', 'Внимание!');
    } );
  }

}
