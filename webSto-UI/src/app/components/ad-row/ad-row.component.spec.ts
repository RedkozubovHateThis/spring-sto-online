import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {AdRowComponent} from './ad-row.component';

describe('UserFilterComponent', () => {
  let component: AdRowComponent;
  let fixture: ComponentFixture<AdRowComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AdRowComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdRowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
