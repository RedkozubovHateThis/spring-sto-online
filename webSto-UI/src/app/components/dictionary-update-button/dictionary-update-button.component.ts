import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {UserService} from '../../api/user.service';
import {ToastrService} from 'ngx-toastr';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {RestService} from '../../api/rest.service';
import {DocumentCollection, Resource} from 'ngx-jsonapi';
import {FileItem, FileUploader, ParsedResponseHeaders} from 'ng2-file-upload';
import {environment} from '../../../environments/environment';
import $ from 'jquery';

@Component({
  selector: 'app-dictionary-update-button',
  templateUrl: './dictionary-update-button.component.html',
  styleUrls: ['./dictionary-update-button.component.scss']
})
export class DictionaryUpdateButtonComponent implements OnInit {

  @ViewChild('content', {static: false}) private content;
  private isUpdating = false;
  @Input()
  private modalTitle: string;
  @Input()
  private dictionaryType: string;
  @Output()
  private onUpdate: EventEmitter<any> = new EventEmitter();

  private uploader: FileUploader;

  private importTypes = [
    { id: 'ADD_NEW', title: 'Добавить новые' },
    { id: 'REPLACE_ALL', title: 'Очистить и добавить все' }
  ];
  private importType = 'ADD_NEW';
  private sheetNumber = 1;
  private colNumber = 1;
  private startRow = 1;

  constructor(private userService: UserService, private toastrService: ToastrService, private modalService: NgbModal) { }

  ngOnInit() {
    this.uploader = new FileUploader({
      url: `${environment.getApiUrl()}/files/dictionaries/update`,
      autoUpload: false,
      authTokenHeader: 'Authorization',
      authToken: `Bearer ${this.userService.getToken()}`
    });

    this.uploader.onAfterAddingFile = ( item: FileItem ) => {
      this.uploader.clearQueue();
      this.uploader.queue.push( item );
      $('#upload').val(null);
    };
    this.uploader.onCompleteItem = ( item: FileItem, response: string, status: number, headers: ParsedResponseHeaders ) => {
      this.isUpdating = false;
      this.modalService.dismissAll();
      this.uploader.clearQueue();
      $('#upload').val(null);

      if ( status !== 200 ) {
        try {
          const errorResponse: any = JSON.parse( response );

          if ( errorResponse.responseText )
            this.toastrService.error(errorResponse.error.responseText, 'Внимание!');
          else
            this.toastrService.error('Ошибка обновления справочника!', 'Внимание!');
        }
        catch (e) {
          this.toastrService.error('Ошибка обновления справочника!', 'Внимание!');
        }
      }
      else {
        this.toastrService.success(`Справочник "${this.modalTitle}" успешно обновлен!`);
        this.onUpdate.emit();
      }
    };
  }

  promptUpdate() {
    const { importType, sheetNumber, colNumber, startRow, dictionaryType } = this;

    if ( sheetNumber == null || sheetNumber < 1 ) {
      this.toastrService.warning('Неправильно указан номер листа!');
      return;
    }
    if ( colNumber == null || colNumber < 1 ) {
      this.toastrService.warning('Неправильно указан номер столбца!');
      return;
    }
    if ( startRow == null || startRow < 1 ) {
      this.toastrService.warning('Неправильно указан номер строки!');
      return;
    }

    this.uploader.setOptions({
      additionalParameter: {
        importType,
        sheetNumber,
        colNumber,
        startRow,
        dictionaryType
      }
    });
    this.uploader.uploadAll();
    this.isUpdating = true;
  }

  removeFile(file: FileItem) {
    file.remove();
    $('#upload').val(null);
  }

  showUpload() {
    $('#upload').click();
  }

  open(content) {
    this.modalService.open(content);
  }
}
