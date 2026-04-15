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
  role: string = 'APPROVER';
  errorMessage: string = '';
  isLoading: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  onSubmit() {
    // Reset error message
    this.errorMessage = '';

    // Validation
    if (!this.email || !this.password) {
      this.errorMessage = 'Email and password are required';
      return;
    }

    this.isLoading = true;

    const loginData: LoginRequest = {
      email: this.email,
      password: this.password,
      role: this.role as 'COMMITTEE' | 'APPROVER'
    };

    this.authService.login(loginData).subscribe(
      (response) => {
        this.isLoading = false;
        if (response.role === 'COMMITTEE') {
          this.router.navigate(['/committee-dashboard']);
        } else if (response.role === 'APPROVER') {
          this.router.navigate(['/approver-dashboard']);
        }
      },
      (error) => {
        this.isLoading = false;
        this.errorMessage = 'Login failed. Please check your credentials.';
        console.error('Login error:', error);
      }
    );
  }

  navigateToRegister() {
    this.router.navigate(['/register']);
  }
}