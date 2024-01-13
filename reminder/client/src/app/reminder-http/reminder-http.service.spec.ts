import { TestBed } from '@angular/core/testing';

import { ReminderHttpService } from './reminder-http.service';

describe('ReminderHttpService', () => {
  let service: ReminderHttpService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ReminderHttpService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
