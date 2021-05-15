import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {DictionariesFilterComponent} from './dictionaries-filter.component';

describe('InfobarComponent', () => {
  let component: DictionariesFilterComponent;
  let fixture: ComponentFixture<DictionariesFilterComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DictionariesFilterComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DictionariesFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
