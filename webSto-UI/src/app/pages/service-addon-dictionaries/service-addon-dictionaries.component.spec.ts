import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ServiceAddonDictionariesComponent} from './service-addon-dictionaries.component';

describe('TablesComponent', () => {
  let component: ServiceAddonDictionariesComponent;
  let fixture: ComponentFixture<ServiceAddonDictionariesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ServiceAddonDictionariesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ServiceAddonDictionariesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
