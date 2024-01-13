import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GetAlarmsComponent } from './get-alarms.component';

describe('GetAlarmsComponent', () => {
  let component: GetAlarmsComponent;
  let fixture: ComponentFixture<GetAlarmsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GetAlarmsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(GetAlarmsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
