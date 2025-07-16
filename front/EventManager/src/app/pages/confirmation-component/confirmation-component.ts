import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faCalendarDay, faMapMarkerAlt, faUsers, faCheck, faUser, faPrint } from '@fortawesome/free-solid-svg-icons';

interface EventDetails {
  event: {
    id: number;
    name: string;
    description: string;
    eventDate: string;
    guestNumber: number;
    userId: number;
  };
  address: {
    id: number;
    street: string;
    number: number;
    complement: string | null;
    neighborhood: string;
    city: string;
    state: string;
    zipCode: number;
    eventId: number;
  };
  table: {
    id: number;
    description: string;
    guestNumber: number;
    eventId: number;
  };
  guestName: string;
  urlPhoto: string;
  confirmed: boolean;
}

@Component({
  selector: 'app-confirmation-component',
  standalone: true,
  imports: [CommonModule, FontAwesomeModule],
  templateUrl: './confirmation-component.html',
  styleUrl: './confirmation-component.css'
})
export class ConfirmationComponent implements OnInit {
  guestId: number | null = null;
  eventId: number | null = null;
  tableId: number | null = null;

  eventDetails: EventDetails | null = null;
  isLoading: boolean = true;
  isConfirming: boolean = false;
  hasError: boolean = false;
  errorMessage: string = '';
  confirmationSuccess: boolean = false;

  // Font Awesome icons
  faCalendarDay = faCalendarDay;
  faMapMarkerAlt = faMapMarkerAlt;
  faUsers = faUsers;
  faCheck = faCheck;
  faUser = faUser;
  faPrint = faPrint;

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    // Extrair parâmetros da URL
    this.route.queryParams.subscribe(params => {
      this.guestId = params['guestId'] ? +params['guestId'] : null;
      this.eventId = params['eventId'] ? +params['eventId'] : null;
      this.tableId = params['tableId'] ? +params['tableId'] : null;

      // Verificar se todos os parâmetros necessários estão presentes
      if (!this.guestId || !this.eventId || !this.tableId) {
        this.hasError = true;
        this.errorMessage = 'Link de confirmação inválido. Verifique se o link está correto ou entre em contato com o organizador do evento.';
        this.isLoading = false;
      } else {
        // Verificar status do convidado - se já confirmou ou não
        this.checkGuestStatus();
      }
    });
  }

  checkGuestStatus(): void {
    // Verificar se o convidado já confirmou presença - usando a rota pública
    this.http.get<{confirmed: boolean}>(`http://localhost:8080/guests/public/${this.guestId}/status`)
      .subscribe({
        next: (response) => {
          if (response.confirmed) {
            // Se já confirmou, buscar detalhes para mostrar o ticket
            this.confirmationSuccess = true;
            this.loadEventDetails();
          } else {
            // Se não confirmou, mostrar página de confirmação
            this.isLoading = false;
          }
        },
        error: (error) => {
          this.hasError = true;
          this.errorMessage = 'Erro ao verificar status do convidado. Tente novamente mais tarde.';
          this.isLoading = false;
        }
      });
  }

  confirmAttendance(): void {
    if (!this.guestId || !this.eventId || !this.tableId) return;

    this.isConfirming = true;

    // Usar a rota pública para confirmação
    this.http.post<EventDetails>(`http://localhost:8080/guests/public/${this.guestId}/confirm`, {
      eventId: this.eventId,
      tableId: this.tableId
    }).subscribe({
      next: (response) => {
        this.eventDetails = response;
        this.confirmationSuccess = true;
        this.isConfirming = false;
      },
      error: (error) => {
        this.hasError = true;
        this.errorMessage = 'Erro ao confirmar presença. Tente novamente mais tarde.';
        this.isConfirming = false;
      }
    });
  }

  loadEventDetails(): void {
    if (!this.guestId || !this.eventId || !this.tableId) return;

    // Usar a rota pública para carregar detalhes
    this.http.get<EventDetails>(`http://localhost:8080/guests/public/${this.guestId}/details`)
      .subscribe({
        next: (response) => {
          this.eventDetails = response;
          this.isLoading = false;
        },
        error: (error) => {
          this.hasError = true;
          this.errorMessage = 'Erro ao carregar detalhes do evento. Tente novamente mais tarde.';
          this.isLoading = false;
        }
      });
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('pt-BR');
  }

  printTicket(): void {
    window.print();
  }
}
