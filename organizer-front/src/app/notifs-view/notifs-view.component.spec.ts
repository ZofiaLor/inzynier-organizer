import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NotifsViewComponent } from './notifs-view.component';

describe('NotifsViewComponent', () => {
  let component: NotifsViewComponent;
  let fixture: ComponentFixture<NotifsViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NotifsViewComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(NotifsViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
