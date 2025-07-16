import {Component, OnInit, OnDestroy} from '@angular/core';
import {DatePickerComponent} from "../../components/date-picker/date-picker.component";
import {NgForOf, NgIf} from "@angular/common";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute, Router} from '@angular/router';
import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';
import {faTrash} from '@fortawesome/free-solid-svg-icons';
import {interval, Subscription} from 'rxjs';

interface Event {
  id: number;
  name: string;
  description: string;
  eventDate: string;
  guestNumber: number;
  userId: number;
}

interface Address {
  id: number;
  street: string;
  number: number;
  complement: string | null;
  neighborhood: string;
  city: string;
  state: string;
  zipCode: number;
  eventId: number;
}

interface AddressCreate {
  street: string;
  number: number;
  complement: string | null;
  neighborhood: string;
  city: string;
  state: string;
  zipCode: number;
  eventId: number;
}

interface Table {
  id: number;
  description: string;
  guestNumber: number;
  eventId: number;
}

interface TablePage {
  content: Table[];
  pageable: {
    pageNumber: number;
    pageSize: number;
  };
  totalElements: number;
  totalPages: number;
  number: number;
}

interface TableCreate {
  description: string;
  guestNumber: number;
  eventId: number;
}

interface CountdownTimer {
  days: number;
  hours: number;
  minutes: number;
  seconds: number;
}

@Component({
  selector: 'app-event-component',
  imports: [
    NgForOf,
    NgIf,
    ReactiveFormsModule,
    FontAwesomeModule,
  ],
  templateUrl: './event-component.html',
  styleUrl: './event-component.css'
})
export class EventComponent implements OnInit, OnDestroy {
  saveEvent() {
    throw new Error('Method not implemented.');
  }
  eventId!: number;
  event!: Event;
  address: Address | null = null;
  tables: Table[] = [];

  currentPage = 0;
  totalPages = 0;
  pageSize = 10;

  tableForm: FormGroup;
  addressForm: FormGroup;

  isEditingAddress = false;

  countdown: CountdownTimer = {
    days: 0,
    hours: 0,
    minutes: 0,
    seconds: 0
  };

  private countdownSubscription?: Subscription;

  constructor(
    private http: HttpClient,
    private router: Router,
    private route: ActivatedRoute,
    private fb: FormBuilder
  ) {
    this.tableForm = this.fb.group({
      description: ['', Validators.required],
      guestNumber: [null, [Validators.required, Validators.min(1)]]
    });

    this.addressForm = this.fb.group({
      street: ['', Validators.required],
      number: [null, Validators.required],
      complement: [null],
      neighborhood: ['', Validators.required],
      city: ['', Validators.required],
      state: ['', Validators.required],
      zipCode: [null, Validators.required],
    });
  }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');

      if (id) {
        this.eventId = +id;
        this.loadEvent(this.eventId);
        this.loadAddress(this.eventId);
        this.loadTables(0, this.eventId);
      }
    });
  }

  ngOnDestroy() {
    if (this.countdownSubscription) {
      this.countdownSubscription.unsubscribe();
    }
  }

  loadEvent(id: number = 0) {
    this.http.get<Event>(`http://localhost:8080/events/${id}`)
      .subscribe(response => {
        this.event = response;
        this.startCountdown(new Date(this.event.eventDate));
      });
  }

  loadAddress(eventId: number) {
    this.http.get<Address>(`http://localhost:8080/addresses/event/${eventId}`)
      .subscribe({
        next: (response) => {
          if (response) {
            this.address = response;
          } else {
            this.address = null;
          }
        },
        error: (error) => {
          console.error('Erro ao carregar endereço:', error);
          this.address = null;
        }
      });
  }

  editAddress() {
    if (this.address) {
      this.isEditingAddress = true;
      this.addressForm.patchValue({
        street: this.address.street,
        number: this.address.number,
        complement: this.address.complement,
        neighborhood: this.address.neighborhood,
        city: this.address.city,
        state: this.address.state,
        zipCode: this.address.zipCode
      });
      this.openAddAddressModal();
    }
  }


  loadTables(page: number = 0, id: number): void {
    this.http.get<TablePage>(`http://localhost:8080/table-entities/by-event/${id}?page=${page}&size=${this.pageSize}`)
      .subscribe(response => {
        this.tables = response.content;
        this.currentPage = response.number;
        this.totalPages = response.totalPages;
      });
  }

  startCountdown(eventDate: Date) {
    // Cancelar inscrição anterior se existir
    if (this.countdownSubscription) {
      this.countdownSubscription.unsubscribe();
    }

    this.countdownSubscription = interval(1000).subscribe(() => {
      const now = new Date().getTime();
      const eventTime = eventDate.getTime();
      const distance = eventTime - now;

      if (distance > 0) {
        this.countdown = {
          days: Math.floor(distance / (1000 * 60 * 60 * 24)),
          hours: Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)),
          minutes: Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60)),
          seconds: Math.floor((distance % (1000 * 60)) / 1000)
        };
      } else {
        // O evento já ocorreu
        this.countdown = {
          days: 0,
          hours: 0,
          minutes: 0,
          seconds: 0
        };
        this.countdownSubscription?.unsubscribe();
      }
    });
  }

  changePage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadTables(page, this.eventId);
    }
  }

  getPageNumbers(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString();
  }

  editTableDetails(tableId: number): void {
    //redirecionar para o table-component
    this.router.navigate(['/event', this.eventId, 'table', tableId]);
  }

  resetEventForm(): void {
    this.tableForm.reset();
  }

  resetAddressForm(): void {
    this.addressForm.reset();
  }

  openAddTableModal() {
    const modal = document.getElementById('add_table_modal') as HTMLDialogElement;
    if (modal) {
      modal.showModal();
    }
  }

  closeAddTableModal() {
    const modal = document.getElementById('add_table_modal') as HTMLDialogElement;
    if (modal) {
      modal.close();
      this.resetEventForm();
    }
  }

  openAddAddressModal() {
    const modal = document.getElementById('add_address_modal') as HTMLDialogElement;
    if (modal) {
      modal.showModal();
    }
  }

  closeAddAddressModal() {
    const modal = document.getElementById('add_address_modal') as HTMLDialogElement;
    if (modal) {
      modal.close();
      this.resetAddressForm();
      this.isEditingAddress = false;
    }
  }

  saveTable(): void {
    if (this.tableForm.valid) {
      const tableData: TableCreate = {
        ...this.tableForm.value,
        eventId: this.eventId
      };

      this.http.post<Table>('http://localhost:8080/table-entities', tableData)
        .subscribe({
          next: (response) => {
            this.closeAddTableModal();
            this.loadTables(this.currentPage, this.eventId); // Recarrega a página atual
          },
          error: (error) => {
            console.error('Erro ao salvar mesa:', error);
          }
        });
    }
  }

  saveAddress(): void {
    if (this.addressForm.valid) {
      const addressData: AddressCreate = {
        ...this.addressForm.value,
        eventId: this.eventId
      };

      if (this.isEditingAddress && this.address) {
        // Atualizar endereço existente
        this.http.put<Address>(`http://localhost:8080/addresses/${this.address.id}`, addressData)
          .subscribe({
            next: (response) => {
              this.closeAddAddressModal();
              this.loadAddress(this.eventId);
            },
            error: (error) => {
              console.error('Erro ao atualizar endereço:', error);
            }
          });
      } else {
        // Criar novo endereço
        this.http.post<Address>('http://localhost:8080/addresses', addressData)
          .subscribe({
            next: (response) => {
              this.closeAddAddressModal();
              this.loadAddress(this.eventId);
            },
            error: (error) => {
              console.error('Erro ao salvar endereço:', error);
            }
          });
      }
    }
  }

  deleteTable(id: number) {
    //TODO
  }

  protected readonly faTrash = faTrash;
}
