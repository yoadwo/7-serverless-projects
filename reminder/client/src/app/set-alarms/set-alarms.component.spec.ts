import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SetAlarmsComponent } from './set-alarms.component';

describe('SetAlarmsComponent', () => {
  let component: SetAlarmsComponent;
  let fixture: ComponentFixture<SetAlarmsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SetAlarmsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SetAlarmsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
