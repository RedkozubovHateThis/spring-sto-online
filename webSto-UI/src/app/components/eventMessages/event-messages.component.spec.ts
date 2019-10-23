import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EventMessagesComponent } from './event-messages.component';

describe('EventMessageComponent', () => {
  let component: EventMessagesComponent;
  let fixture: ComponentFixture<EventMessagesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EventMessagesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EventMessagesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
