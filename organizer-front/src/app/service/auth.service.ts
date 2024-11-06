import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
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

  login(username: string, password: string): Observable<any> {
    return this.http.post(
      AUTH_URL + 'login',
      {"username": username, "password": password},
      httpOptions
    );
  }

  register(user: User, password: string): Observable<any> {
    return this.http.post(
      AUTH_URL + 'register',
      {"username": user.username,
        "password": password,
        "name": user.name,
        "email": user.email
      },
      httpOptions
    );
  }

  logout(): Observable<any> {
    return this.http.post(AUTH_URL + 'logout', { }, httpOptions);
  }

  grantAdmin(user: User): Observable<HttpResponse<User>> {
    return this.http.put<HttpResponse<User>>(AUTH_URL + 'grant', user, httpOptions);
  }

  revokeAdmin(user: User): Observable<HttpResponse<User>> {
    return this.http.put<HttpResponse<User>>(AUTH_URL + 'revoke', user, httpOptions);
  }
}
