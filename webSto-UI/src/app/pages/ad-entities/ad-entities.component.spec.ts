import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {AdEntitiesComponent} from './ad-entities.component';

describe('TablesComponent', () => {
  let component: AdEntitiesComponent;
  let fixture: ComponentFixture<AdEntitiesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AdEntitiesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdEntitiesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
