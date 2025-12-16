import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { MascotaFormComponent } from './components/mascota/mascota-form/mascota-form';

export const routes: Routes = [
    { path: '', component: HomeComponent }, // Ruta por defecto
    { path: 'mascota/nuevo', component: MascotaFormComponent },
    { path: '**', redirectTo: '' } // Cualquier ruta desconocida va al home
  ];
