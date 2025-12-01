import { Injectable, signal, effect } from '@angular/core';

export type Theme = 'light' | 'dark';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  private readonly THEME_KEY = 'app-theme';
  
  // Signal para el tema actual
  currentTheme = signal<Theme>(this.getInitialTheme());

  constructor() {
    // Aplicar tema al inicializar
    this.applyTheme(this.currentTheme());
    
    // Efecto para aplicar tema cuando cambia
    effect(() => {
      const theme = this.currentTheme();
      this.applyTheme(theme);
      this.saveTheme(theme);
    });
  }

  private getInitialTheme(): Theme {
    // Intentar obtener del localStorage
    const saved = localStorage.getItem(this.THEME_KEY) as Theme;
    if (saved === 'light' || saved === 'dark') {
      return saved;
    }
    
    // Si no hay guardado, usar preferencia del sistema
    if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
      return 'dark';
    }
    
    return 'light';
  }

  private applyTheme(theme: Theme): void {
    const html = document.documentElement;
    if (theme === 'dark') {
      html.classList.add('dark-theme');
      html.classList.remove('light-theme');
    } else {
      html.classList.add('light-theme');
      html.classList.remove('dark-theme');
    }
  }

  private saveTheme(theme: Theme): void {
    localStorage.setItem(this.THEME_KEY, theme);
  }

  toggleTheme(): void {
    const newTheme: Theme = this.currentTheme() === 'light' ? 'dark' : 'light';
    console.log('🔄 Cambiando tema de', this.currentTheme(), 'a', newTheme);
    this.currentTheme.set(newTheme);
    // Aplicar inmediatamente para asegurar que funcione
    this.applyTheme(newTheme);
    this.saveTheme(newTheme);
  }

  setTheme(theme: Theme): void {
    this.currentTheme.set(theme);
  }
}

