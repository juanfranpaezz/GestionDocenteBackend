import { Routes } from '@angular/router';
import { RegisterPage } from './pages/register-page/register-page';
import { LoginPage } from './pages/login-page/login-page';
import { VerifyEmailPage } from './pages/verify-email-page/verify-email-page';
import { CoursesPage } from './pages/courses/courses-page/courses-page';
import { CourseEditPage } from './pages/courses/course-edit-page/course-edit-page';
import { CourseCreatePage } from './pages/courses/course-create-page/course-create-page';
import { CourseDetailPage } from './pages/courses/course-detail-page/course-detail-page';
import { EditProfessorPage } from './pages/edit-professor-page/edit-professor-page';
import { ProfessorsListPage } from './pages/professors-list-page/professors-list-page';
import { ArchivedCoursesPage } from './pages/courses/archived-courses-page/archived-courses-page';
import { GradeScalesPage } from './pages/grade-scales-page/grade-scales-page';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';
import { Role } from './enums/roles';

export const routes: Routes = [
    // Rutas públicas (sin autenticación)
    { path: '', redirectTo: 'auth/login', pathMatch: 'full' },
    { path: 'auth/login', component: LoginPage },
    { path: 'auth/register', component: RegisterPage },
    { path: 'auth/verify-email', component: VerifyEmailPage },
    
    // Rutas protegidas (requieren autenticación y rol PROFESSOR)
    { 
        path: 'course/list', 
        component: CoursesPage,
        canActivate: [authGuard, roleGuard],
        data: { roles: [Role.PROFESSOR] }
    },
    { 
        path: 'course/create', 
        component: CourseCreatePage,
        canActivate: [authGuard, roleGuard],
        data: { roles: [Role.PROFESSOR] }
    },
    { 
        path: 'course/edit/:id', 
        component: CourseEditPage,
        canActivate: [authGuard, roleGuard],
        data: { roles: [Role.PROFESSOR] }
    },
    { 
        path: 'course/detail/:id', 
        component: CourseDetailPage,
        canActivate: [authGuard, roleGuard],
        data: { roles: [Role.PROFESSOR] }
    },
    { 
        path: 'courses/archived', 
        component: ArchivedCoursesPage,
        canActivate: [authGuard, roleGuard],
        data: { roles: [Role.PROFESSOR] }
    },
    { 
        path: 'grade-scales', 
        component: GradeScalesPage,
        canActivate: [authGuard, roleGuard],
        data: { roles: [Role.PROFESSOR] }
    },
    { 
        path: 'auth/edit', 
        component: EditProfessorPage,
        canActivate: [authGuard]
    },
    
    // Rutas protegidas por rol (solo ADMIN)
    { 
        path: 'professors/list', 
        component: ProfessorsListPage,
        canActivate: [authGuard, roleGuard],
        data: { roles: [Role.ADMIN] }
    },
    
    // Ruta catch-all: redirigir a login si la ruta no existe
    { path: '**', redirectTo: 'auth/login' }
];
