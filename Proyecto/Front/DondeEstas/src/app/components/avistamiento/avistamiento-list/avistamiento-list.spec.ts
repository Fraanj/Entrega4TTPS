import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AvistamientoListComponent } from './avistamiento-list';

describe('AvistamientoList', () => {
  let component: AvistamientoListComponent;
  let fixture: ComponentFixture<AvistamientoListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AvistamientoListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AvistamientoListComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
