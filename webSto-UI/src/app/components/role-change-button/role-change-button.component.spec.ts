import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RoleChangeButtonComponent } from './role-change-button.component';

describe('RoleChangeButtonComponent', () => {
  let component: RoleChangeButtonComponent;
  let fixture: ComponentFixture<RoleChangeButtonComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RoleChangeButtonComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RoleChangeButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
