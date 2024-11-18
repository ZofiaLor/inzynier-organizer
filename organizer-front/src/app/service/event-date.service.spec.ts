import { TestBed } from '@angular/core/testing';

import { EventDateService } from './event-date.service';

describe('EventDateService', () => {
  let service: EventDateService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EventDateService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
