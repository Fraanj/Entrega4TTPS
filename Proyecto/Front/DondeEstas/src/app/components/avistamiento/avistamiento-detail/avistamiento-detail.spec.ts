import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AvistamientoDetailModalComponent } from './avistamiento-detail';

describe('AvistamientoDetail', () => {
  let component: AvistamientoDetailModalComponent;
  let fixture: ComponentFixture<AvistamientoDetailModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AvistamientoDetailModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AvistamientoDetailModalComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
