import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../model/user';

const AUTH_URL = 'http://localhost:8080/api/auth/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json'}),
  observe: 'response' as 'body',
  withCredentials: true
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

  register(user: User): Observable<any> {
    return this.http.post(
      AUTH_URL + 'register',
      user,
      httpOptions
    );
  }

  logout(): Observable<any> {
    return this.http.post(AUTH_URL + 'logout', { }, httpOptions);
  }
}
