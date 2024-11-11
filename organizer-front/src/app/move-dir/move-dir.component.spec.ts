import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MoveDirComponent } from './move-dir.component';

describe('MoveDirComponent', () => {
  let component: MoveDirComponent;
  let fixture: ComponentFixture<MoveDirComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MoveDirComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(MoveDirComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
