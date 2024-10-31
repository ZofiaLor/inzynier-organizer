import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../model/user';

const AUTH_URL = 'http://localhost:8080/auth/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient) { }

  login(user: User): Observable<any> {
    return this.http.post(
      AUTH_URL + 'login',
      user,
      httpOptions
    );
  }

  register(username: string, name: string | null, email: string | null, password: string): Observable<any> {
    return this.http.post(
      AUTH_URL + 'register',
      {
        username,
        name,
        email,
        password,
      },
      httpOptions
    );
  }

  logout(): Observable<any> {
    return this.http.post(AUTH_URL + 'logout', { }, httpOptions);
  }
}
