import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {PaymentRecordFilterComponent} from './payment-record-filter.component';

describe('InfobarComponent', () => {
  let component: PaymentRecordFilterComponent;
  let fixture: ComponentFixture<PaymentRecordFilterComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PaymentRecordFilterComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PaymentRecordFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
