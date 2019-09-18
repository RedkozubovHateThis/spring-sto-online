import { Component, OnInit } from '@angular/core';
import * as moment from 'moment';
import {DocumentResponseService} from '../../api/documentResponse.service';
import {ToastrService} from 'ngx-toastr';
import {UserService} from '../../api/user.service';
import {HttpClient} from '@angular/common/http';

@Component({
  selector: 'app-reports',
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.scss']
})
export class ReportsComponent implements OnInit {

  private isDownloading: boolean = false;
  private startDate: moment.Moment = moment( new Date() );
  private endDate: moment.Moment = moment( new Date() );
  private reportType: string = 'executors';
  private reportTypeFileName = {
    executors: 'Выручка по слесарям за период с',
    clients: 'Отчет о реализации за период с'
  };

  private datePickerConfig = {
    locale: 'ru',
    firstDayOfWeek: 'mo',
    showGoToCurrent: true,
    // monthFormat: 'MM, YYYY'
  };

  constructor(private httpClient: HttpClient, private toastrService: ToastrService, private userService: UserService) { }

  ngOnInit() {
    this.startDate.startOf('month').set('hour', 0).set('minute', 0).set('second', 0);
    this.endDate.endOf('month').set('hour', 0).set('minute', 0).set('second', 0);
  }

  requestReport() {

    const headers = this.userService.getHeaders();
    const params = {
      startDate: this.startDate.format('DD.MM.YYYY hh:mm:ss'),
      endDate: this.endDate.format('DD.MM.YYYY hh:mm:ss')
    };

    this.isDownloading = true;
    this.httpClient.get(`http://localhost:8181/secured/reports/${this.reportType}`,
      {headers, params, responseType: 'blob'} ).subscribe( blob => {

      this.isDownloading = false;

      const data = window.URL.createObjectURL(blob);

      const link = document.createElement('a');
      link.href = data;
      link.download = `${this.reportTypeFileName[this.reportType]} ${this.startDate.format('DD.MM.YYYY')} по ${this.endDate.format('DD.MM.YYYY')}.xlsx`;
      link.dispatchEvent(new MouseEvent('click', { bubbles: true, cancelable: true, view: window }));

      setTimeout( () => {
        window.URL.revokeObjectURL(data);
        link.remove();
      }, 100 );

    }, error => {
      this.isDownloading = false;
      if ( error.status === 403 )
        this.toastrService.error('Отчеты недоступны!', 'Внимание!');
      else if ( error.status === 404 )
        this.toastrService.warning('Данные не найдены!', 'Внимание!');
      else
        this.toastrService.error('Ошибка формирования отчета!', 'Внимание!');
    } );
  }

}
