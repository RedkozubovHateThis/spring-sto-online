import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {PasswordChangeButtonComponent} from './password-change-button.component';

describe('PasswordChangeButtonComponent', () => {
  let component: PasswordChangeButtonComponent;
  let fixture: ComponentFixture<PasswordChangeButtonComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PasswordChangeButtonComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PasswordChangeButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
