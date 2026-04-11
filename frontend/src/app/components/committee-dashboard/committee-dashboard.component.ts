import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

interface Venue {
  venueId: number;
  venueName: string;
  capacity?: number;
}

interface Event {
  eventId: number;
  eventName: string;
  eventDate: string;
  expectedParticipants?: number;
  createdDate?: string;
  status?: string;
  venue?: Venue;
}

interface PermissionApplication {
  applicationId: number;
  event: Event;
  uploadDate: string;
  permissionDoc: string;
  status: string;
}

@Component({
  selector: 'app-committee-dashboard',
  templateUrl: './committee-dashboard.component.html',
  styleUrls: ['./committee-dashboard.component.css']
})
export class CommitteeDashboardComponent implements OnInit, OnDestroy {
  private readonly BASE = 'http://localhost:8080';
  private pollInterval: any;
  private readonly POLL_MS = 30000;

  committeeId: number = 1;
  committeeName: string = 'Committee';

  // Data
  events: Event[] = [];
  applications: PermissionApplication[] = [];
  venues: Venue[] = [];

  // UI State
  isLoading: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';

  // Active tab
  activeTab: 'applications' | 'events' | 'new-event' = 'applications';

  // New Event form
  newEvent = {
    eventName: '',
    eventDate: '',
    expectedParticipants: null as number | null,
    venueId: null as number | null
  };
  isSubmittingEvent: boolean = false;
  eventFormError: string = '';

  // Stats
  pendingCount: number = 0;
  approvedCount: number = 0;
  rejectedCount: number = 0;

  // Details drawer
  showDetailsDrawer: boolean = false;
  selectedApp: PermissionApplication | null = null;

  constructor(
    private http: HttpClient,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.committeeId = this.authService.getCommitteeId();
    this.committeeName = this.authService.getUserName();
    this.loadData();
    this.loadVenues();
    this.pollInterval = setInterval(() => this.loadData(), this.POLL_MS);
  }

  ngOnDestroy(): void {
    if (this.pollInterval) clearInterval(this.pollInterval);
  }

  loadData(): void {
    this.isLoading = true;

    // Load this committee's events
    this.http.get<Event[]>(`${this.BASE}/events/committee/${this.committeeId}`)
      .subscribe({
        next: (events) => {
          this.events = events || [];
          this.loadApplications();
        },
        error: (err) => {
          this.errorMessage = 'Failed to load events.';
          this.isLoading = false;
          console.error(err);
        }
      });
  }

  loadApplications(): void {
    // Fetch all permission applications, then filter to this committee's events
    this.http.get<PermissionApplication[]>(`${this.BASE}/permissions`)
      .subscribe({
        next: (apps) => {
          const myEventIds = new Set(this.events.map(e => e.eventId));
          this.applications = (apps || []).filter(a => myEventIds.has(a.event?.eventId));
          this.calculateStats();
          this.isLoading = false;
        },
        error: (err) => {
          this.errorMessage = 'Failed to load applications.';
          this.isLoading = false;
          console.error(err);
        }
      });
  }

  loadVenues(): void {
    this.http.get<Venue[]>(`${this.BASE}/api/venues`)
      .subscribe({
        next: (venues) => { this.venues = venues || []; },
        error: (err) => console.error('Could not load venues', err)
      });
  }

  calculateStats(): void {
    const s = (a: PermissionApplication) => (a.status || '').toUpperCase();
    this.pendingCount  = this.applications.filter(a => s(a) === 'PENDING' || s(a) === 'SUBMITTED').length;
    this.approvedCount = this.applications.filter(a => s(a) === 'APPROVED').length;
    this.rejectedCount = this.applications.filter(a => s(a) === 'REJECTED').length;
  }

  setTab(tab: 'applications' | 'events' | 'new-event'): void {
    this.activeTab = tab;
    this.eventFormError = '';
    this.errorMessage = '';
  }

  // ─── New Event ──────────────────────────
  submitNewEvent(): void {
    this.eventFormError = '';
    if (!this.newEvent.eventName.trim()) {
      this.eventFormError = 'Event name is required.'; return;
    }
    if (!this.newEvent.eventDate) {
      this.eventFormError = 'Event date is required.'; return;
    }

    this.isSubmittingEvent = true;

    const payload: any = {
      eventName: this.newEvent.eventName,
      eventDate: this.newEvent.eventDate,
      expectedParticipants: this.newEvent.expectedParticipants,
      status: 'Pending',
      committee: { id: this.committeeId }
    };

    if (this.newEvent.venueId) {
      payload.venue = { venueId: this.newEvent.venueId };
    }

    this.http.post<Event>(`${this.BASE}/events`, payload).subscribe({
      next: (createdEvent) => {
        this.isSubmittingEvent = false;
        this.successMessage = `Event "${createdEvent.eventName}" created successfully.`;
        this.newEvent = { eventName: '', eventDate: '', expectedParticipants: null, venueId: null };
        this.loadData();
        this.setTab('events');
        setTimeout(() => this.successMessage = '', 4000);
      },
      error: (err) => {
        this.isSubmittingEvent = false;
        this.eventFormError = 'Failed to create event. Please try again.';
        console.error(err);
      }
    });
  }

  // ─── Submit permission application ──────
  submitPermission(event: Event): void {
    if (!confirm(`Submit a permission application for "${event.eventName}"?`)) return;

    const payload = { permissionDoc: '' };
    this.http.post<PermissionApplication>(
      `${this.BASE}/permissions/submit/${event.eventId}`, payload
    ).subscribe({
      next: () => {
        this.successMessage = `Permission application submitted for "${event.eventName}".`;
        this.loadData();
        setTimeout(() => this.successMessage = '', 4000);
      },
      error: (err) => {
        this.errorMessage = 'Failed to submit permission application.';
        console.error(err);
        setTimeout(() => this.errorMessage = '', 4000);
      }
    });
  }

  hasApplication(event: Event): boolean {
    return this.applications.some(a => a.event?.eventId === event.eventId);
  }

  getApplicationForEvent(event: Event): PermissionApplication | undefined {
    return this.applications.find(a => a.event?.eventId === event.eventId);
  }

  // ─── Details drawer ──────────────────────
  openDetails(app: PermissionApplication): void {
    this.selectedApp = app;
    this.showDetailsDrawer = true;
  }

  closeDetails(): void {
    this.showDetailsDrawer = false;
    this.selectedApp = null;
  }

  // ─── Helpers ────────────────────────────
  getStatusClass(status: string): string {
    const s = (status || '').toUpperCase();
    if (s === 'APPROVED') return 'badge-approved';
    if (s === 'REJECTED')  return 'badge-rejected';
    return 'badge-pending';
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
