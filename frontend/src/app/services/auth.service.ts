import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, catchError } from 'rxjs';

export interface LoginRequest {
  email: string;
  password: string;
  role: 'COMMITTEE' | 'APPROVER';
}

export interface RegisterRequest {
  email: string;
  password: string;
  role: 'COMMITTEE' | 'APPROVER';
  // Approver fields
  name?: string;
  // Committee fields
  committeeName?: string;
  facultyInChargeName?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly BASE = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  login(data: LoginRequest): Observable<LoginResponse> {
    // Create login request with correct field names for Committee endpoint
    const committeeLoginData = {
      contactEmail: data.email,
      password: data.password
    };
    
    // Create login request for Approver endpoint
    const approverLoginData = {
      email: data.email,
      password: data.password
    };
    
    // Try to login as approver first
    return this.http.post<any>(`${this.apiUrl}/api/approvers/login`, approverLoginData).pipe(
      map(response => ({ role: 'APPROVER', userId: response.approverId } as LoginResponse)),
      catchError(() => {
        // If approver login fails, try committee login
        return this.http.post<any>(`${this.apiUrl}/api/committees/login`, committeeLoginData).pipe(
          map(response => ({ role: 'COMMITTEE', userId: response.id } as LoginResponse)),
          catchError(() => {
            // Both failed, throw error
            throw new Error('Invalid credentials');
          })
        );
      })
    );
  }

  register(data: RegisterRequest): Observable<any> {
    if (data.role === 'APPROVER') {
      // POST /api/approvers
      const payload = {
        name: data.name,
        email: data.email,
        password: data.password,
        role: 'APPROVER'
      };
      return this.http.post<any>(`${this.BASE}/api/approvers`, payload);
    } else {
      // POST /api/committees
      const payload = {
        committeeName: data.committeeName,
        headOfCommittee: data.facultyInChargeName,
        contactEmail: data.email,
        password: data.password
      };
      return this.http.post<any>(`${this.BASE}/api/committees`, payload);
    }
  }

  getApproverId(): number {
    return Number(localStorage.getItem('approverId')) || 1;
  }

  getCommitteeId(): number {
    return Number(localStorage.getItem('committeeId')) || 1;
  }

  getUserName(): string {
    return localStorage.getItem('userName') || '';
  }

  getUserRole(): string {
    return localStorage.getItem('userRole') || '';
  }

  logout(): void {
    localStorage.clear();
  }
}
