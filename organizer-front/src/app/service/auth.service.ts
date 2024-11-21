import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../model/user';
import { API_URL, httpOptions } from './service-utils';

const AUTH_URL = API_URL + 'auth/';

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

  refreshToken(): Observable<any> {
    return this.http.post(AUTH_URL + 'refreshtoken', { }, httpOptions);
  }

  changePassword(oldPwd: string, newPwd: string): Observable<any> {
    return this.http.put(AUTH_URL + 'password', {oldPassword: oldPwd, newPassword: newPwd}, httpOptions);
  }

  grantAdmin(user: User): Observable<HttpResponse<User>> {
    return this.http.put<HttpResponse<User>>(AUTH_URL + 'grant', user, httpOptions);
  }

  revokeAdmin(user: User): Observable<HttpResponse<User>> {
    return this.http.put<HttpResponse<User>>(AUTH_URL + 'revoke', user, httpOptions);
  }
}
