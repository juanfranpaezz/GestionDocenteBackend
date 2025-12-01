import { Component, ElementRef, inject, Input, OnInit, OnDestroy, signal, ViewChild, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth-service';
import { ThemeService } from '../../../core/services/theme-service';
import { Role } from '../../../enums/roles';

@Component({
  selector: 'app-nav-component',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './nav-component.html',
  styleUrls: ['./nav-component.css']
})
export class NavComponent implements OnInit, OnDestroy {

  @Input() userName!: string | null;        // <-- OK
  @Input() userImage!: string;       // <-- OK
  @Input() userRole?: Role;          // <-- Rol del usuario

  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;

  private router = inject(Router);
  private authService = inject(AuthService);
  private themeService = inject(ThemeService);

  menuOpen = signal(false);
  mobileMenuOpen = signal(false);
  searchQuery = signal('');
  
  // Exponer el tema actual para el template
  currentTheme = this.themeService.currentTheme;

  get isAdmin(): boolean {
    return this.userRole === Role.ADMIN;
  }

  onSearchChange(query: string) {
    this.searchQuery.set(query);
    // Detectar la ruta actual y navegar con query params
    const currentUrl = this.router.url;
    if (currentUrl.includes('/courses/archived')) {
      this.router.navigate(['/courses/archived'], { queryParams: { search: query || null } });
    } else {
      this.router.navigate(['/course/list'], { queryParams: { search: query || null } });
    }
  }

  goToArchived(event?: Event) {
    if (event) {
      event.preventDefault();
      event.stopPropagation();
    }
    this.router.navigate(['/courses/archived']);
  }

  goToGradeScales(event?: Event) {
    if (event) {
      event.preventDefault();
      event.stopPropagation();
    }
    this.router.navigate(['/grade-scales']);
  }

  // ESTA señal es solo para actualizar la imagen localmente
  localImage = signal<string | null>(null);
  imageError = signal(false);

  ngOnInit() {
    // Sincronizar searchQuery con query params actuales
    const currentUrl = this.router.url;
    const urlTree = this.router.parseUrl(currentUrl);
    const searchParam = urlTree.queryParams['search'];
    if (searchParam) {
      this.searchQuery.set(searchParam);
    }
  }

  ngOnChanges() {
    // cuando cambien los @Input, actualizamos localImage solo si hay una imagen válida
    // Si userImage es null, undefined, o string vacío, usar null para que use el default
    const validImage = this.userImage && this.userImage.trim() !== '' ? this.userImage : null;
    this.localImage.set(validImage);
    this.imageError.set(false); // Reset error state cuando cambia la imagen
  }

  openFilePicker(event: Event) {
    event.stopPropagation();
    this.fileInput.nativeElement.click();
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    const reader = new FileReader();

    reader.onload = () => {
      const base64 = reader.result as string;

      this.localImage.set(base64);  // actualizar imagen local

      const prof = this.authService.currentProfessor();
      if (!prof) return;

      this.authService.updatePhoto(prof.id!, base64).subscribe({
        next: () => console.log("Foto actualizada"),
        error: () => console.error("Error al actualizar foto")
      });
    };

    reader.readAsDataURL(file);
  }

  getImageSrc(): string {
    // Si hay una imagen local (recién cargada), usarla
    if (this.localImage()) {
      return this.localImage()!;
    }
    // Si hay una imagen del usuario válida y no es la default, usarla
    if (this.userImage && this.userImage.trim() !== '' && !this.userImage.includes('default-profile')) {
      return this.userImage;
    }
    // Si hay error o no hay imagen, usar la imagen por defecto
    return '/assets/default-profile.svg';
  }

  handleImageError(event: Event) {
    const img = event.target as HTMLImageElement;
    // Solo cambiar si no es ya la imagen por defecto para evitar loops
    if (img.src && !img.src.includes('default-profile.svg')) {
      img.src = '/assets/default-profile.svg';
      this.imageError.set(true);
    }
  }

  toggleMenu() {
    this.menuOpen.update(v => !v);
  }

  toggleMobileMenu() {
    this.mobileMenuOpen.update(v => !v);
  }

  closeMobileMenu() {
    this.mobileMenuOpen.set(false);
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }

  toggleTheme() {
    this.themeService.toggleTheme();
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    if (!this.menuOpen()) return;
    
    const target = event.target as HTMLElement;
    const userSection = target.closest('.user-section');
    const dropdown = target.closest('.dropdown');
    
    // Si el clic fue fuera del menú desplegable y de la sección de usuario, cerrar el menú
    if (!userSection && !dropdown) {
      this.menuOpen.set(false);
    }
  }

  ngOnDestroy() {
    // Cleanup si es necesario
  }
}
