import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, catchError } from 'rxjs';

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
  userName: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) { }

  login(data: LoginRequest): Observable<LoginResponse> {
    const committeeLoginData = {
      contactEmail: data.email,
      password: data.password
    };

    const approverLoginData = {
      email: data.email,
      password: data.password
    };

    return this.http.post<any>(`${this.apiUrl}/api/approvers/login`, approverLoginData).pipe(
      map(response => {
        const loginResponse: LoginResponse = { 
          role: 'APPROVER', 
          userId: response.approverId,
          userName: response.name || 'Approver'
        };
        this.saveSession(loginResponse);
        return loginResponse;
      }),
      catchError(() => {
        return this.http.post<any>(`${this.apiUrl}/api/committees/login`, committeeLoginData).pipe(
          map(response => {
            const loginResponse: LoginResponse = { 
              role: 'COMMITTEE', 
              userId: response.id,
              userName: response.committeeName || 'Committee'
            };
            this.saveSession(loginResponse);
            return loginResponse;
          }),
          catchError(() => {
            throw new Error('Invalid credentials');
          })
        );
      })
    );
  }

  private saveSession(data: LoginResponse): void {
    localStorage.setItem('role', data.role);
    localStorage.setItem('userId', data.userId.toString());
    localStorage.setItem('userName', data.userName);
  }

  logout(): void {
    localStorage.removeItem('role');
    localStorage.removeItem('userId');
    localStorage.removeItem('userName');
  }

  getRole(): string | null {
    return localStorage.getItem('role');
  }

  getApproverId(): number {
    const id = localStorage.getItem('userId');
    return id ? parseInt(id, 10) : 0;
  }

  getCommitteeId(): number {
    const id = localStorage.getItem('userId');
    return id ? parseInt(id, 10) : 0;
  }

  getUserName(): string {
    return localStorage.getItem('userName') || 'User';
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem('userId');
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