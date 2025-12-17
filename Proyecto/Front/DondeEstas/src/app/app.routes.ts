import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { MascotaFormComponent } from './components/mascota/mascota-form/mascota-form';
import { MascotaListComponent } from './components/mascota/mascota-list/mascota-list';
import { MascotaDetailComponent } from './components/mascota/mascota-detail/mascota-detail';
import { MainLayout } from './layout/main-layout/main-layout';

export const routes: Routes = [
    {
      path: '',
      component: MainLayout,
      children: [
        { path: '', component: HomeComponent }, // Ruta por defecto
        { path: 'mascota/nuevo', component: MascotaFormComponent },
        { path: 'mascotas', component: MascotaListComponent },
        { path: 'mascota/:id', component: MascotaDetailComponent },
        { path: '**', redirectTo: '' } // Cualquier ruta desconocida va al home
      ]
    }
  ];
