import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {VehicleDictionariesComponent} from './vehicle-dictionaries.component';

describe('TablesComponent', () => {
  let component: VehicleDictionariesComponent;
  let fixture: ComponentFixture<VehicleDictionariesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VehicleDictionariesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VehicleDictionariesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
