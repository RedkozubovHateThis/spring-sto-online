import { Component, OnInit } from '@angular/core';
import Chart from 'chart.js';

// core components
import {
  chartOptions,
  parseOptions,
  chartExample1,
  chartExample2
} from "../../variables/charts";
import {DocumentResponse} from "../../model/firebird/documentResponse";
import {DocumentResponseService} from "../../api/documentResponse.service";
import {Pageable} from "../../model/Pageable";
import { Router } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {

  public last5:Pageable<DocumentResponse>;
  isLoading:boolean = false;

  public datasets: any;
  public data: any;
  public salesChart;
  public clicked: boolean = true;
  public clicked1: boolean = false;

  constructor(private documentResponseService:DocumentResponseService, private router:Router) { }

  ngOnInit() {
    this.requestData();

    // this.datasets = [
    //   [0, 20, 10, 30, 15, 40, 20, 60, 60],
    //   [0, 20, 5, 25, 10, 30, 15, 40, 40]
    // ];
    // this.data = this.datasets[0];
    //
    //
    // var chartOrders = document.getElementById('chart-orders');
    //
    // parseOptions(Chart, chartOptions());
    //
    //
    // var ordersChart = new Chart(chartOrders, {
    //   type: 'bar',
    //   options: chartExample2.options,
    //   data: chartExample2.data
    // });
    //
    // var chartSales = document.getElementById('chart-sales');
    //
    // this.salesChart = new Chart(chartSales, {
		// 	type: 'line',
		// 	options: chartExample1.options,
		// 	data: chartExample1.data
		// });
  }

  private requestData() {
    this.isLoading = true;
    this.documentResponseService.getLast5().subscribe( data => {
      this.last5 = data as Pageable<DocumentResponse>;
      this.isLoading = false;
    }, error => {
      this.isLoading = false;
    } );
  }

  public updateOptions() {
    // this.salesChart.data.datasets[0].data = this.data;
    // this.salesChart.update();
  }

  private navigate(documentResponse:DocumentResponse) {
    this.documentResponseService.exchangingModel = documentResponse;
    this.router.navigate(['/documents', documentResponse.id]);
  }

}
