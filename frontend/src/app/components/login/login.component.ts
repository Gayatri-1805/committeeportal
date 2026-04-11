import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService, LoginRequest } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  email: string = '';
  password: string = '';
  role: 'COMMITTEE' | 'APPROVER' = 'APPROVER';
  errorMessage: string = '';
  isLoading: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  onSubmit() {
    this.errorMessage = '';

    if (!this.email || !this.password) {
      this.errorMessage = 'Email and password are required';
      return;
    }

    this.isLoading = true;

    const loginData: LoginRequest = {
      email: this.email,
      password: this.password,
      role: this.role
    };

    this.authService.login(loginData).subscribe({
      next: (role: string) => {
        this.isLoading = false;
        if (role === 'COMMITTEE') {
          this.router.navigate(['/committee-dashboard']);
        } else if (role === 'APPROVER') {
          this.router.navigate(['/approver-dashboard']);
        }
      },
      error: (err) => {
        this.isLoading = false;
        if (err?.status === 401) {
          this.errorMessage = 'Invalid email or password.';
        } else {
          this.errorMessage = 'Login failed. Please check if the server is running.';
        }
        console.error('Login error:', err);
      }
    });
  }

  navigateToRegister() {
    this.router.navigate(['/register']);
  }
}
