import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth-service';

@Component({
  selector: 'app-verify-email-page',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './verify-email-page.html',
  styleUrls: ['./verify-email-page.css']
})
export class VerifyEmailPage implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private authService = inject(AuthService);

  loading = signal(true);
  verified = signal(false);
  errorMessage = signal<string | null>(null);

  ngOnInit() {
    // Obtener el token de la query string
    this.route.queryParams.subscribe(params => {
      const token = params['token'];
      
      if (!token) {
        this.errorMessage.set('No se proporcionó un token de verificación');
        this.loading.set(false);
        return;
      }

      // Verificar el email con el token
      this.authService.verifyEmail(token).subscribe({
        next: (response) => {
          console.log('✅ Email verificado:', response);
          this.verified.set(true);
          this.loading.set(false);
        },
        error: (err) => {
          console.error('❌ Error al verificar email:', err);
          const errorMsg = err?.error?.error || 'Error al verificar el email. El token puede haber expirado o ser inválido.';
          this.errorMessage.set(errorMsg);
          this.loading.set(false);
        }
      });
    });
  }
}

