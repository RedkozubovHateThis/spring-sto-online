import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {DocumentFilterComponent} from './document-filter.component';

describe('InfobarComponent', () => {
  let component: DocumentFilterComponent;
  let fixture: ComponentFixture<DocumentFilterComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DocumentFilterComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DocumentFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
