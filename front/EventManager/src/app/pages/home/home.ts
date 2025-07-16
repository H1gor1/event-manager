import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { Card } from '../../components/card/card';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { DatePickerComponent } from '../../components/date-picker/date-picker.component';

interface Event {
  id: number;
  name: string;
  description: string;
  eventDate: string;
  guestNumber: number;
  userId: number;
}

interface EventPage {
  content: Event[];
  pageable: {
    pageNumber: number;
    pageSize: number;
  };
  totalElements: number;
  totalPages: number;
  number: number;
}

interface EventCreate {
  name: string;
  description: string;
  eventDate: string;
  guestNumber: number;
}

@Component({
  selector: 'app-home',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    DatePickerComponent
  ],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home implements OnInit {
  events: Event[] = [];
  currentPage = 0;
  totalPages = 0;
  pageSize = 10;
  eventForm: FormGroup;

  constructor(
    private http: HttpClient,
    private router: Router,
    private fb: FormBuilder
  ) {
    this.eventForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      eventDate: ['', Validators.required],
      guestNumber: [null, [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit(): void {
    this.loadEvents();
  }

  loadEvents(page: number = 0): void {
    // Substitua a URL pela sua API real
    this.http.get<EventPage>(`http://localhost:8080/events/user?page=${page}&size=${this.pageSize}`)
      .subscribe(response => {
        this.events = response.content;
        this.currentPage = response.number;
        this.totalPages = response.totalPages;
      });
  }

  changePage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadEvents(page);
    }
  }

  getPageNumbers(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString();
  }

  openAddEventModal(): void {
    this.resetEventForm();
    const modal = document.getElementById('add_event_modal') as HTMLDialogElement;
    if (modal) {
      modal.showModal();
    }
  }

  closeAddEventModal(): void {
    const modal = document.getElementById('add_event_modal') as HTMLDialogElement;
    if (modal) {
      modal.close();
    }
  }

  resetEventForm(): void {
    this.eventForm.reset();
  }

  saveEvent(): void {
    if (this.eventForm.valid) {
      const eventData: EventCreate = this.eventForm.value;

      // Substitua a URL pela sua API real
      this.http.post<Event>('http://localhost:8080/events', eventData)
        .subscribe({
          next: (response) => {
            this.closeAddEventModal();
            this.loadEvents(this.currentPage); // Recarrega a página atual
          },
          error: (error) => {
            console.error('Erro ao salvar evento:', error);
            // Implemente uma mensagem de erro aqui
          }
        });
    }
  }

  viewEventDetails(eventId: number): void {
    // Navegar para a página de detalhes do evento
    this.router.navigate(['/event', eventId]);
  }
}
