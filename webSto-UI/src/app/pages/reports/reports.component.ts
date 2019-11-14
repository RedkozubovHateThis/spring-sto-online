import {Component, OnInit} from '@angular/core';
import * as moment from 'moment';
import {DocumentResponseService} from '../../api/documentResponse.service';
import {ToastrService} from 'ngx-toastr';
import {UserService} from '../../api/user.service';
import {HttpClient} from '@angular/common/http';
import {OrganizationResponse} from '../../model/firebird/organizationResponse';
import {OrganizationResponseService} from '../../api/organizationResponse.service';

@Component({
  selector: 'app-reports',
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.scss']
})
export class ReportsComponent implements OnInit {

  private isDownloading: boolean = false;
  private isLoading: boolean = false;
  private selectedMonth: number;
  private selectedYear: number;
  private reportData: object[];
  private executorsTotalRow: object;
  private clientsTotalRow: object;
  private reportType: string = 'executors';
  private organizationId: number = null;
  private organizations: OrganizationResponse[] = [];
  private reportTypeFileName = {
    executors: 'Выручка по слесарям за период с',
    clients: 'Отчет о реализации за период с'
  };
  private months = [
    { id: 0, name: 'Январь' },
    { id: 1, name: 'Февраль' },
    { id: 2, name: 'Март' },
    { id: 3, name: 'Апрель' },
    { id: 4, name: 'Май' },
    { id: 5, name: 'Июнь' },
    { id: 6, name: 'Июль' },
    { id: 7, name: 'Август' },
    { id: 8, name: 'Сентябрь' },
    { id: 9, name: 'Октябрь' },
    { id: 10, name: 'Ноябрь' },
    { id: 11, name: 'Декабрь' }
  ];

  private datePickerConfig = {
    locale: 'ru',
    firstDayOfWeek: 'mo',
    showGoToCurrent: true,
    // monthFormat: 'MM, YYYY'
  };

  constructor(private httpClient: HttpClient, private toastrService: ToastrService, private userService: UserService,
              private organizationResponseService: OrganizationResponseService) { }

  ngOnInit() {
    this.organizationResponseService.getAll().subscribe( response => {
      this.organizations = response as OrganizationResponse[];
    } );
    const date = new Date();
    this.selectedYear = date.getFullYear();
    this.selectedMonth = date.getMonth();
    this.requestReportData();
  }

  requestReportPDF() {

    const date: Date = new Date();
    date.setFullYear( this.selectedYear );
    date.setMonth( this.selectedMonth );

    const startDate: moment.Moment = moment( date ).startOf('month').set('hour', 0).set('minute', 0).set('second', 0);
    const endDate: moment.Moment = moment( date ).endOf('month').set('hour', 23).set('minute', 59).set('second', 59);

    const headers = this.userService.getHeaders();
    const params = {
      startDate: startDate.format('DD.MM.YYYY hh:mm:ss'),
      endDate: endDate.format('DD.MM.YYYY hh:mm:ss'),
      organizationId: this.organizationId != null ? this.organizationId.toString() : ''
    };

    this.isDownloading = true;
    this.httpClient.get(`${this.userService.getApiUrl()}secured/reports/${this.reportType}/PDF`,
      {headers, params, responseType: 'blob'} ).subscribe( blob => {

      this.isDownloading = false;

      const data = window.URL.createObjectURL(blob);

      const link = document.createElement('a');
      link.href = data;
      link.download = `${this.reportTypeFileName[this.reportType]} ${startDate.format('DD.MM.YYYY')} по ${endDate.format('DD.MM.YYYY')}.pdf`;
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

  requestReportData() {

    const date: Date = new Date();
    date.setFullYear( this.selectedYear );
    date.setMonth( this.selectedMonth );

    const startDate: moment.Moment = moment( date ).startOf('month').set('hour', 0).set('minute', 0).set('second', 0);
    const endDate: moment.Moment = moment( date ).endOf('month').set('hour', 23).set('minute', 59).set('second', 59);

    const headers = this.userService.getHeaders();
    const params = {
      startDate: startDate.format('DD.MM.YYYY hh:mm:ss'),
      endDate: endDate.format('DD.MM.YYYY hh:mm:ss'),
      organizationId: this.organizationId != null ? this.organizationId.toString() : ''
    };

    this.isLoading = true;
    this.httpClient.get(`${this.userService.getApiUrl()}secured/reports/${this.reportType}`,
      {headers, params} ).subscribe( reportData => {

      this.reportData = reportData as object[];
      if ( this.reportType === 'executors' ) this.setTotalByExecutors();
      else if ( this.reportType === 'clients' ) this.setTotalByClients();
      this.isLoading = false;

    }, error => {
      this.executorsTotalRow = {
        totalByNorm: 0,
        totalByPrice: 0,
        totalSum: 0,
        totalSalary: 0
      };
      this.clientsTotalRow = {
        total: 0
      };
      this.isLoading = false;
      if ( error.status === 403 )
        this.toastrService.error('Отчеты недоступны!', 'Внимание!');
      else if ( error.status !== 404 )
        this.toastrService.error('Ошибка формирования отчета!', 'Внимание!');
    } );
  }

  private setTotalByExecutors() {
    const totalRow = {
      totalByNorm: 0,
      totalByPrice: 0,
      totalSum: 0,
      totalSalary: 0
    };
    this.reportData.forEach( reportData => {
      // @ts-ignore
      totalRow.totalByNorm += reportData.totalByNorm;
      // @ts-ignore
      totalRow.totalByPrice += reportData.totalByPrice;
      // @ts-ignore
      totalRow.totalSum += reportData.totalSum;
      // @ts-ignore
      totalRow.totalSalary += reportData.salary;
    } );
    this.executorsTotalRow = totalRow;
  }

  private setTotalByClients() {
    const totalRow = {
      total: 0,
    };
    this.reportData.forEach( reportData => {
      // @ts-ignore
      totalRow.total += reportData.total;
    } );
    this.clientsTotalRow = totalRow;
  }

}
