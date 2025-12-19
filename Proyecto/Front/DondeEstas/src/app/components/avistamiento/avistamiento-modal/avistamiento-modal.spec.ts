import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AvistamientoModalComponent } from './avistamiento-modal';

describe('AvistamientoModal', () => {
  let component: AvistamientoModalComponent;
  let fixture: ComponentFixture<AvistamientoModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AvistamientoModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AvistamientoModalComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
