import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageNotifsComponent } from './manage-notifs.component';

describe('ManageNotifsComponent', () => {
  let component: ManageNotifsComponent;
  let fixture: ComponentFixture<ManageNotifsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManageNotifsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ManageNotifsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
