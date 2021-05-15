import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {DictionaryUpdateButtonComponent} from './dictionary-update-button.component';

describe('DeleteButtonComponent', () => {
  let component: DictionaryUpdateButtonComponent;
  let fixture: ComponentFixture<DictionaryUpdateButtonComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DictionaryUpdateButtonComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DictionaryUpdateButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
