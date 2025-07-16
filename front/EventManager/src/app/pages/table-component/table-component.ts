import { Component, OnInit } from '@angular/core';
import { CommonModule, NgFor, NgIf } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

interface Table {
  id: number;
  description: string;
  guestNumber: number;
  eventId: number;
}

interface Guest {
  id?: number;
  name: string;
  email?: string;
  phone?: string;
  tableId: number;
  urlPhoto?: string;
  confirmed?: boolean;
}

interface Notification {
  message: string;
  type: 'success' | 'error';
}

@Component({
  selector: 'app-table-component',
  standalone: true,
  imports: [CommonModule, NgFor, NgIf, ReactiveFormsModule],
  templateUrl: './table-component.html',
  styleUrl: './table-component.css'
})
export class TableComponent implements OnInit {
  tableId?: number;
  eventId?: number;
  table?: Table;
  guests: Guest[] = [];
  maxGuests: number = 10; // Valor padrão que será atualizado com table.guestNumber

  // Modal state
  isGuestFormModalOpen: boolean = false;
  isGuestDetailsModalOpen: boolean = false;
  isDeleteConfirmModalOpen: boolean = false;
  selectedGuest?: Guest;
  editingGuest?: Guest;
  notification?: Notification;

  guestForm: FormGroup;

  constructor(
    private http: HttpClient,
    private router: Router,
    private route: ActivatedRoute,
    private fb: FormBuilder
  ) {
    this.guestForm = this.fb.group({
      name: ['', Validators.required],
      email: ['', Validators.email],
      phone: [''],
      urlPhoto: ['']
    });
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      const eventId = params.get('eventId');

      if (id) {
        this.tableId = +id;
        this.loadTable(this.tableId);
        this.loadGuests(this.tableId);
      }

      if (eventId) {
        this.eventId = +eventId;
      }
    });
  }

  loadTable(id: number): void {
    this.http.get<Table>(`http://localhost:8080/table-entities/${id}`)
      .subscribe(response => {
        this.table = response;
        this.maxGuests = response.guestNumber;
      });
  }

  loadGuests(tableId: number): void {
    this.http.get<Guest[]>(`http://localhost:8080/guests/all-by-table/${tableId}`)
      .subscribe(response => {
        this.guests = response;
      }, error => {
        this.showNotification('Erro ao carregar convidados', 'error');
      });
  }

  getEmptySlots(): number[] {
    const emptyCount = Math.max(0, this.maxGuests - this.guests.length);
    return Array(emptyCount).fill(0);
  }

  // Modal de formulário de convidado (adicionar/editar)
  openGuestFormModal(guest?: Guest): void {
    this.editingGuest = guest;

    if (guest) {
      // Editar convidado existente
      this.guestForm.patchValue({
        name: guest.name,
        email: guest.email || '',
        phone: guest.phone || '',
        urlPhoto: guest.urlPhoto || ''
      });
      this.closeGuestDetailsModal();
    } else {
      // Adicionar novo convidado
      this.guestForm.reset({
        name: '',
        email: '',
        phone: '',
        urlPhoto: ''
      });
    }

    this.isGuestFormModalOpen = true;
  }

  closeGuestFormModal(): void {
    this.isGuestFormModalOpen = false;
    this.editingGuest = undefined;
    this.guestForm.reset();
  }

  // Modal de detalhes do convidado
  openGuestDetailsModal(guest: Guest): void {
    this.selectedGuest = guest;
    this.isGuestDetailsModalOpen = true;
  }

  closeGuestDetailsModal(): void {
    this.isGuestDetailsModalOpen = false;
  }

  // Modal de confirmação de exclusão
  confirmDeleteGuest(guest: Guest): void {
    this.selectedGuest = guest;
    this.isDeleteConfirmModalOpen = true;
    this.closeGuestDetailsModal(); // Fechar o modal de detalhes se estiver aberto
  }

  closeDeleteConfirmModal(): void {
    this.isDeleteConfirmModalOpen = false;
  }

  // Ações com convidados
  saveGuest(): void {
    if (this.guestForm.invalid || !this.tableId) return;

    const formData = this.guestForm.value;
    const guest: Guest = {
      ...formData,
      tableId: this.tableId,
      confirmed: this.editingGuest?.confirmed || false
    };

    if (this.editingGuest?.id) {
      // Atualizar convidado existente
      guest.id = this.editingGuest.id;
      this.http.put<Guest>(`http://localhost:8080/guests/${guest.id}`, guest)
        .subscribe(response => {
          const index = this.guests.findIndex(g => g.id === guest.id);
          if (index !== -1) {
            this.guests[index] = response;
          }
          this.closeGuestFormModal();
          this.showNotification('Convidado atualizado com sucesso', 'success');
        }, error => {
          this.showNotification('Erro ao atualizar convidado', 'error');
        });
    } else {
      // Adicionar novo convidado
      this.http.post<Guest>('http://localhost:8080/guests', guest)
        .subscribe(response => {
          this.guests.push(response);
          this.closeGuestFormModal();
          this.showNotification('Convidado adicionado com sucesso', 'success');
        }, error => {
          this.showNotification('Erro ao adicionar convidado', 'error');
        });
    }
  }

  deleteGuest(): void {
    if (!this.selectedGuest?.id) return;

    this.http.delete(`http://localhost:8080/guests/${this.selectedGuest.id}`)
      .subscribe(() => {
        this.guests = this.guests.filter(g => g.id !== this.selectedGuest?.id);
        this.closeDeleteConfirmModal();
        this.showNotification('Convidado removido com sucesso', 'success');
      }, error => {
        this.showNotification('Erro ao remover convidado', 'error');
      });
  }

  sendConfirmationEmail(guest: Guest): void {
    if (!guest.id) return;

    this.http.post(`http://localhost:8080/guests/${guest.id}/send-confirmation`, {})
      .subscribe(() => {
        this.showNotification('Email de confirmação enviado com sucesso', 'success');
      }, error => {
        this.showNotification('Erro ao enviar email de confirmação', 'error');
      });
  }

  saveTable(): void {
    // Implementar a lógica para salvar alterações na mesa
    this.showNotification('Mesa salva com sucesso', 'success');
  }

  goBack(): void {
    if (this.eventId) {
      this.router.navigate(['/event', this.eventId]);
    } else {
      this.router.navigate(['/home']);
    }
  }

  // Utilitários
  showNotification(message: string, type: 'success' | 'error'): void {
    this.notification = { message, type };
    setTimeout(() => {
      this.notification = undefined;
    }, 3000);
  }
}
