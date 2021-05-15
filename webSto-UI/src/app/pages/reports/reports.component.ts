import {Component, OnInit} from '@angular/core';
import * as moment from 'moment';
import {ToastrService} from 'ngx-toastr';
import {UserService} from '../../api/user.service';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {DocumentCollection} from 'ngx-jsonapi';
import {ProfileResource} from '../../model/resource/profile.resource.service';
import {ProfileService} from '../../api/profile.service';

@Component({
  selector: 'app-reports',
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.scss']
})
export class ReportsComponent implements OnInit {

  private isDownloading = false;
  private isLoading = false;
  private byDate = true;
  private selectedMonth: number;
  private selectedYear: number;
  private vinNumber = '';
  private reportData: any[];
  private clientsTotalRow = {
    total: 0
  };
  private vehiclesTotalRow = {
    total: 0
  };
  private reportType: string = 'clients';
  private organizationId: string = null;
  private organizations: DocumentCollection<ProfileResource> = new DocumentCollection<ProfileResource>();
  private reportTypeFileName = {
    clients: 'Отчет о реализации',
    registered: 'Отчет о зарегистрированных клиентах',
    vehicles: 'Отчет о ремонтах автомобиля',
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
              private profileService: ProfileService) { }

  ngOnInit() {
    this.profileService.getAllExecutors().subscribe( response => {
      this.organizations = response;
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

    let params: any;
    switch ( this.reportType ) {
      case 'vehicles': {
        params = {
          organizationId: this.organizationId != null ? this.organizationId.toString() : '',
          vinNumber: this.vinNumber
        };
        if ( this.byDate ) {
          params.startDate = startDate.format('DD.MM.YYYY hh:mm:ss');
          params.endDate = endDate.format('DD.MM.YYYY hh:mm:ss');
        }
        break;
      }
      case 'registered': {
        params = {
          organizationId: this.organizationId != null ? this.organizationId.toString() : ''
        };
        break;
      }
      default: {
        params = {
          organizationId: this.organizationId != null ? this.organizationId.toString() : ''
        };
        if ( this.byDate ) {
          params.startDate = startDate.format('DD.MM.YYYY hh:mm:ss');
          params.endDate = endDate.format('DD.MM.YYYY hh:mm:ss');
        }
      }
    }

    this.isDownloading = true;
    this.httpClient.get(`${environment.getApiUrl()}reports/${this.reportType}/PDF`,
      {params, responseType: 'blob'} ).subscribe( blob => {

      this.isDownloading = false;

      const data = window.URL.createObjectURL(blob);

      let download;
      if ( this.reportType === 'registered' )
        download = this.reportTypeFileName[this.reportType];
      else {
        if ( this.byDate )
          download = `${this.reportTypeFileName[this.reportType]} за период с ${startDate.format('DD.MM.YYYY')} по ${endDate.format('DD.MM.YYYY')}`;
        else
          download = this.reportTypeFileName[this.reportType];
      }

      const link = document.createElement('a');
      link.href = data;
      link.download = `${download}.pdf`;
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

    let params: any;
    switch ( this.reportType ) {
      case 'vehicles': {
        params = {
          organizationId: this.organizationId != null ? this.organizationId.toString() : '',
          vinNumber: this.vinNumber
        };
        if ( this.byDate ) {
          params.startDate = startDate.format('DD.MM.YYYY hh:mm:ss');
          params.endDate = endDate.format('DD.MM.YYYY hh:mm:ss');
        }
        break;
      }
      case 'registered': {
        params = {
          organizationId: this.organizationId != null ? this.organizationId.toString() : ''
        };
        break;
      }
      default: {
        params = {
          organizationId: this.organizationId != null ? this.organizationId.toString() : ''
        };
        if ( this.byDate ) {
          params.startDate = startDate.format('DD.MM.YYYY hh:mm:ss');
          params.endDate = endDate.format('DD.MM.YYYY hh:mm:ss');
        }
      }
    }

    this.isLoading = true;
    this.httpClient.get<any[]>(`${environment.getApiUrl()}reports/${this.reportType}`,
      {params} ).subscribe( reportData => {

      this.reportData = reportData;
      if ( this.reportType === 'clients' ) this.setTotalByClients();
      if ( this.reportType === 'vehicles' ) this.setTotalByVehicles();
      this.isLoading = false;

    }, error => {
      this.clientsTotalRow = {
        total: 0
      };
      this.vehiclesTotalRow = {
        total: 0
      };
      this.reportData = [];
      this.isLoading = false;
      if ( error.status === 403 )
        this.toastrService.error('Отчеты недоступны!', 'Внимание!');
      else if ( error.status !== 404 )
        this.toastrService.error('Ошибка формирования отчета!', 'Внимание!');
    } );
  }

  private setTotalByClients() {
    const totalRow = {
      total: 0,
    };
    this.reportData.forEach( reportData => {
      totalRow.total += reportData.total;
    } );
    this.clientsTotalRow = totalRow;
  }

  private setTotalByVehicles() {
    const totalRow = {
      total: 0,
    };
    this.reportData.forEach( reportData => {
      totalRow.total += reportData.total;
    } );
    this.vehiclesTotalRow = totalRow;
  }

  private toggleData(isVisible: boolean) {
    if ( this.reportData == null || this.reportData.length === 0 ) return;

    this.reportData.forEach( data => {
      data.isVisible = isVisible;
    } );
  }

}
