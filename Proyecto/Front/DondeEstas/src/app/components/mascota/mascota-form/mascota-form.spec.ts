import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MascotaFormComponent } from './mascota-form';

describe('MascotaForm', () => {
  let component: MascotaFormComponent;
  let fixture: ComponentFixture<MascotaFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MascotaFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MascotaFormComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
