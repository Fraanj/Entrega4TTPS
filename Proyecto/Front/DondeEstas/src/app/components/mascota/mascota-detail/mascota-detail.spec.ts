import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MascotaDetailComponent } from './mascota-detail';

describe('MascotaDetail', () => {
  let component: MascotaDetailComponent;
  let fixture: ComponentFixture<MascotaDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MascotaDetailComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MascotaDetailComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
