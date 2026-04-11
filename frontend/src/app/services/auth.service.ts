import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

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

  login(data: LoginRequest): Observable<string> {
    if (data.role === 'APPROVER') {
      // POST /api/approvers/login  →  returns Approver object
      return this.http.post<any>(`${this.BASE}/api/approvers/login`, {
        email: data.email,
        password: data.password
      }).pipe(
        map(approver => {
          localStorage.setItem('userRole', 'APPROVER');
          localStorage.setItem('approverId', approver.approverId.toString());
          localStorage.setItem('userName', approver.name || 'Approver');
          return 'APPROVER';
        }),
        catchError(err => throwError(() => err))
      );
    } else {
      // POST /api/committees/login  →  returns Committee object
      return this.http.post<any>(`${this.BASE}/api/committees/login`, {
        contactEmail: data.email,
        password: data.password
      }).pipe(
        map(committee => {
          localStorage.setItem('userRole', 'COMMITTEE');
          localStorage.setItem('committeeId', committee.id.toString());
          localStorage.setItem('userName', committee.committeeName || 'Committee');
          return 'COMMITTEE';
        }),
        catchError(err => throwError(() => err))
      );
    }
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
