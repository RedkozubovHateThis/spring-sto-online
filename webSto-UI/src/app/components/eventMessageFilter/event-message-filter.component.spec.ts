import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EventMessageFilterComponent } from './event-message-filter.component';

describe('event-message-filter', () => {
  let component: EventMessageFilterComponent;
  let fixture: ComponentFixture<EventMessageFilterComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EventMessageFilterComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EventMessageFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
