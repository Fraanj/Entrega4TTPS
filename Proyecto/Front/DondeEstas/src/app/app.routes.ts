import { Routes } from '@angular/router';
import { MainLayout } from './layout/main-layout/main-layout';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/auth/login/login';
import { RegisterComponent } from './components/auth/register/register';
import { MascotaListComponent } from './components/mascota/mascota-list/mascota-list';
import { MascotaFormComponent } from './components/mascota/mascota-form/mascota-form';
import { MascotaDetailComponent } from './components/mascota/mascota-detail/mascota-detail';
import { AuthGuard } from './guards/auth.guard';

export const routes: Routes = [
  { 
    path: '', 
    component: MainLayout, // 👈 Todas las rutas usan el layout
    children: [
      { path: '', redirectTo: 'home', pathMatch: 'full' },
      { path: 'home', component: HomeComponent },
      { path: 'login', component: LoginComponent },
      { path: 'register', component: RegisterComponent },
      
      // Rutas protegidas
      { 
        path: 'mascotas', 
        component: MascotaListComponent
      },
      { 
        path: 'mascotas/nueva', 
        component: MascotaFormComponent,
        canActivate: [AuthGuard]
      },
      { 
        path: 'mascotas/:id', 
        component: MascotaDetailComponent,
        canActivate: [AuthGuard]
      },
      { 
        path: 'mascotas/:id/editar', 
        component: MascotaFormComponent,
        canActivate: [AuthGuard]
      }
    ]
  },
  { path: '**', redirectTo: '/home' }
];