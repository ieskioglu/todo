import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import moment from 'moment';
import { BehaviorSubject, map } from 'rxjs';
import { LoginRequest } from '../models/login-request';
import { LoginResponse } from '../models/login-response';
import { RegisterRequest } from '../models/register-request';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  public userSubject: BehaviorSubject<LoginResponse>;

  constructor(private http: HttpClient) {
    this.userSubject = new BehaviorSubject<LoginResponse>({} as LoginResponse);
  }

  public get userValue(): LoginResponse {
    return this.userSubject.value;
  }

  login(request: LoginRequest) {
    return this.http.post<LoginResponse>('/api/auth/login', request).pipe(
      map((res) => {
        this.userSubject.next(res);
        this.setSession(res);
      })
    );
  }

  register(request: RegisterRequest) {
    return this.http.post('/api/auth/signup', request);
  }

  private setSession(authResult: LoginResponse) {
    localStorage.setItem('auth_token', authResult.token);
    localStorage.setItem('expires_at', JSON.stringify(authResult.expiresAt));
  }

  public isLoggedIn() {
    var expiresAt = this.getExpiration();
    if (!expiresAt) {
      return false;
    }
    return moment().isBefore(expiresAt);
  }

  logout() {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('expires_in');
  }

  getExpiration() {
    const expiration = localStorage.getItem('expires_at');
    if (expiration) {
      const expiresAt = JSON.parse(expiration);
      return moment(expiresAt);
    }
    return null;
  }
}
