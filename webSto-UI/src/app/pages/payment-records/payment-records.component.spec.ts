import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {PaymentRecordsComponent} from './payment-records.component';

describe('TablesComponent', () => {
  let component: PaymentRecordsComponent;
  let fixture: ComponentFixture<PaymentRecordsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PaymentRecordsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PaymentRecordsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
