import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ServiceWorkDictionariesComponent} from './service-work-dictionaries.component';

describe('TablesComponent', () => {
  let component: ServiceWorkDictionariesComponent;
  let fixture: ComponentFixture<ServiceWorkDictionariesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ServiceWorkDictionariesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ServiceWorkDictionariesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
