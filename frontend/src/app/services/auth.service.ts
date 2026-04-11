import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  role: 'COMMITTEE' | 'APPROVER';
  committeeName?: string;
  facultyInChargeName?: string;
  name?: string;
}

export interface LoginResponse {
  role: 'COMMITTEE' | 'APPROVER';
  userId: number;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) { }

  login(data: LoginRequest): Observable<LoginResponse> {
    // Create login request with correct field names for Committee endpoint
    const loginRequestData = {
      contactEmail: data.email,
      password: data.password
    };
    // Try to login as committee first
    return this.http.post<any>(`${this.apiUrl}/api/committees/login`, loginRequestData).pipe(
      map(response => ({ role: 'COMMITTEE', userId: response.id }))
    );
  }

  register(data: RegisterRequest): Observable<any> {
    if (data.role === 'COMMITTEE') {
      // For committee registration, send to /api/committees
      const committeeData = {
        committeeName: data.committeeName,
        contactEmail: data.email,
        password: data.password,
        headOfCommittee: data.facultyInChargeName
      };
      return this.http.post<any>(`${this.apiUrl}/api/committees`, committeeData);
    } else if (data.role === 'APPROVER') {
      // For approver registration, send to /api/approvers
      const approverData = {
        name: data.name,
        email: data.email,
        password: data.password,
        role: 'APPROVER'
      };
      return this.http.post<any>(`${this.apiUrl}/api/approvers`, approverData);
    }
    throw new Error('Invalid role');
  }
}
