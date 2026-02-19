import { Routes } from '@angular/router';
import { MainLayout } from './layout/main-layout/main-layout';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/auth/login/login';
import { RegisterComponent } from './components/auth/register/register';
import { MascotaListComponent } from './components/mascota/mascota-list/mascota-list';
import { MascotaFormComponent } from './components/mascota/mascota-form/mascota-form';
import { MascotaDetailComponent } from './components/mascota/mascota-detail/mascota-detail';
import { AuthGuard } from './guards/auth.guard';
import { AdminGuard } from './guards/admin.guard';
import { ProfileComponent } from './components/user/profile/profile.component';
import { MisMascotasComponent } from './components/mascota/mis-mascotas/mis-mascotas';
import { AvistamientoFormComponent } from './components/avistamiento/avistamiento-form/avistamiento-form';
import { AdminUsuariosComponent } from './components/admin/admin-usuarios/admin-usuarios';
import { AdminPublicacionesComponent } from './components/admin/admin-publicaciones/admin-publicaciones';
import { MapaComponent } from './components/mapa/mapa.component';

export const routes: Routes = [
  { 
    path: '', 
    component: MainLayout,
    children: [
      { path: '', redirectTo: 'home', pathMatch: 'full' },
      { path: 'home', component: HomeComponent },
      { path: 'login', component: LoginComponent },
      { path: 'register', component: RegisterComponent },
      

      { 
        path: 'perfil', 
        component: ProfileComponent,
        canActivate: [AuthGuard] // 👈 Solo usuarios logueados
      },
      // 🎯 Rutas de mascotas
      { 
        path: 'mascotas', 
        component: MascotaListComponent
      },
      { 
        path: 'mapa', 
        component: MapaComponent
      },
      { 
        path: 'mascotas/nueva',  // 👈 Esta es la que falta
        component: MascotaFormComponent,
        canActivate: [AuthGuard]
      },
      { 
        path: 'mis-mascotas', 
        component: MisMascotasComponent,
        canActivate: [AuthGuard] 
      },
      { 
        path: 'mascotas/:id', 
        component: MascotaDetailComponent
      },
      { 
        path: 'mascotas/editar/:id', 
        component: MascotaFormComponent,
        canActivate: [AuthGuard]
      },
      // 🎯 Rutas de avistamientos
      { 
        path: 'avistamiento/reportar/:mascotaId', 
        component: AvistamientoFormComponent,
        canActivate: [AuthGuard]
      },
      // 🎯 Rutas de administración (solo ADMINISTRADOR)
      { 
        path: 'admin/usuarios', 
        component: AdminUsuariosComponent,
        canActivate: [AuthGuard, AdminGuard]
      },
      { 
        path: 'admin/publicaciones', 
        component: AdminPublicacionesComponent,
        canActivate: [AuthGuard, AdminGuard]
      }
    ]
  },
  { path: '**', redirectTo: '/home' }
];