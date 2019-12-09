import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ReportOpenComponent } from './report-open.component';

describe('ReportOpenComponent', () => {
  let component: ReportOpenComponent;
  let fixture: ComponentFixture<ReportOpenComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ReportOpenComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ReportOpenComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
