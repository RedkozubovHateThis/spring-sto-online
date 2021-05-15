import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';

@Component({
  selector: 'app-loading-bar',
  templateUrl: './loading-bar.component.html',
  styleUrls: ['./loading-bar.component.scss'],
})
export class LoadingBarComponent implements OnInit, OnChanges {

  @Input()
  private isLoading: boolean = false;
  @Input()
  private atTop: boolean = false;

  ngOnInit(): void {
  }

  ngOnChanges(changes: SimpleChanges): void {
  }

}
