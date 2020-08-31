import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {AdEntityFilterComponent} from './ad-entity-filter.component';

describe('UserFilterComponent', () => {
  let component: AdEntityFilterComponent;
  let fixture: ComponentFixture<AdEntityFilterComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AdEntityFilterComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdEntityFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
