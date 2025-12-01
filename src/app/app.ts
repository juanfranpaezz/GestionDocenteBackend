import { Component, effect, inject, OnInit, signal } from '@angular/core';
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';
import { FooterComponent } from './components/footers/footer-component/footer-component';
import { NavComponent } from './components/headers/nav-component/nav-component';
import { AuthService } from './core/services/auth-service';
import { ThemeService } from './core/services/theme-service';
import { filter } from 'rxjs/operators';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, FooterComponent, NavComponent, CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App 
{
  protected readonly title = signal('GestionDocente');

  private authService = inject(AuthService);
  private router = inject(Router);
  private themeService = inject(ThemeService); // Inicializar servicio de tema

  // SIGNAL que el template usa
  currentUser = signal(this.authService.currentProfessor());
  showHeader = signal(false);
  showFooter = signal(true);

  constructor() {
    // Sync del authService -> app
    effect(() => {
      this.currentUser.set(this.authService.currentProfessor());
      this.updateHeaderVisibility();
    });

    // Escuchar cambios de ruta
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.updateHeaderVisibility();
    });

    // Verificar inicialmente
    this.updateHeaderVisibility();
  }

  private updateHeaderVisibility() {
    const currentRoute = this.router.url;
    const isAuthenticated = this.authService.isAuthenticated();
    const isAuthRoute = currentRoute.includes('/auth/login') || 
                       currentRoute.includes('/auth/register') || 
                       currentRoute.includes('/auth/verify-email');
    
    // Mostrar header solo si está autenticado y NO está en login/register/verify-email
    this.showHeader.set(isAuthenticated && !isAuthRoute);
  }

  getUserImage(): string {
    const photoUrl = this.currentUser()?.photoUrl;
    // Si no hay photoUrl o es null/vacío, usar la imagen por defecto
    if (!photoUrl || photoUrl.trim() === '' || photoUrl.includes('default-profile')) {
      return '/assets/default-profile.svg';
    }
    return photoUrl;
  }

}
