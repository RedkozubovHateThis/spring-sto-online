import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {VehiclesFilterComponent} from './vehicles-filter.component';

describe('InfobarComponent', () => {
  let component: VehiclesFilterComponent;
  let fixture: ComponentFixture<VehiclesFilterComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VehiclesFilterComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VehiclesFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
