import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';

export const routes: Routes = [
    { path: '', component: HomeComponent }, // Ruta por defecto
    { path: '**', redirectTo: '' } // Cualquier ruta desconocida va al home
  ];
