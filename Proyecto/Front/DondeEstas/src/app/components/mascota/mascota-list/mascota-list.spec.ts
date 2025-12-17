import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MascotaListComponent } from './mascota-list';

describe('MascotaList', () => {
  let component: MascotaListComponent;
  let fixture: ComponentFixture<MascotaListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MascotaListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MascotaListComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
