import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, tap, BehaviorSubject, catchError } from 'rxjs';

interface LoginResponse {
  access_token: string;
  token_type: string;
  expires_in: number;
}

@Injectable({
  providedIn: 'root'
})
export class AuthServiceTs {
  private baseUrl = 'http://localhost:8080';
  private clientId = 'myclientid';
  private clientSecret = 'myclientsecret';
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasToken());

  isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(private http: HttpClient) {}

  login(credentials: { email: string, password: string }): Observable<LoginResponse> {
    // Criar o Basic Auth header
    const basicAuthHeader = 'Basic ' + btoa(`${this.clientId}:${this.clientSecret}`);

    // Configurar headers para OAuth2 com Basic Auth
    const headers = new HttpHeaders()
      .set('Content-Type', 'application/x-www-form-urlencoded')
      .set('Authorization', basicAuthHeader);

    // Criar o corpo da requisição usando HttpParams para garantir o formato correto
    const body = new HttpParams()
      .set('grant_type', 'password')
      .set('username', credentials.email)
      .set('password', credentials.password);

    console.log('Enviando requisição para:', `${this.baseUrl}/oauth2/token`);
    console.log('Headers:', headers);
    console.log('Body:', body.toString());

    return this.http.post<LoginResponse>(
      `${this.baseUrl}/oauth2/token`,
      body.toString(),
      { headers: headers }
    ).pipe(
      tap(response => {
        console.log('Resposta de login recebida:', response);
        localStorage.setItem('auth_token', response.access_token);
        localStorage.setItem('token_type', response.token_type);
        this.isAuthenticatedSubject.next(true);
      }),
      catchError(error => {
        console.error('Erro na requisição de login:', error);
        throw error;
      })
    );
  }

  logout(): void {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('token_type');
    this.isAuthenticatedSubject.next(false);
  }

  getToken(): string | null {
    return localStorage.getItem('auth_token');
  }

  getTokenType(): string {
    return localStorage.getItem('token_type') || 'Bearer';
  }

  isAuthenticated(): boolean {
    return this.hasToken();
  }

  private hasToken(): boolean {
    return !!localStorage.getItem('auth_token');
  }

  getUserInfo(): any | null {
    const userInfo = localStorage.getItem('user_info');
    return userInfo ? JSON.parse(userInfo) : null;
  }

  register(userData: { firstName: string, lastName: string, email: string, password: string }): Observable<any> {
    return this.http.post(
      `${this.baseUrl}/user/signup`,
      userData
    ).pipe(
      tap(response => {
        console.log('Registro bem-sucedido:', response);
      }),
      catchError(error => {
        console.error('Erro no registro:', error);
        throw error;
      })
    );
  }

}
