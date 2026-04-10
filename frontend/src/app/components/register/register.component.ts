import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService, RegisterRequest } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  email: string = '';
  password: string = '';
  role: 'COMMITTEE' | 'APPROVER' = 'COMMITTEE';
  committeeName: string = '';
  facultyInChargeName: string = '';
  name: string = '';
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
    if (!this.email || !this.password || !this.role) {
      this.errorMessage = 'Email, password, and role are required';
      return;
    }

    if (this.role === 'COMMITTEE' && (!this.committeeName || !this.facultyInChargeName)) {
      this.errorMessage = 'Committee name and faculty in charge name are required for Committee role';
      return;
    }

    if (this.role === 'APPROVER' && !this.name) {
      this.errorMessage = 'Name is required for Approver role';
      return;
    }

    this.isLoading = true;

    const registerData: RegisterRequest = {
      email: this.email,
      password: this.password,
      role: this.role
    };

    if (this.role === 'COMMITTEE') {
      registerData.committeeName = this.committeeName;
      registerData.facultyInChargeName = this.facultyInChargeName;
    } else if (this.role === 'APPROVER') {
      registerData.name = this.name;
    }

    this.authService.register(registerData).subscribe(
      (response) => {
        this.isLoading = false;
        alert('Registration successful! Please login with your credentials.');
        this.router.navigate(['/login']);
      },
      (error) => {
        this.isLoading = false;
        this.errorMessage = 'Registration failed. Please try again.';
        console.error('Registration error:', error);
      }
    );
  }

  navigateToLogin() {
    this.router.navigate(['/login']);
  }
}
