import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../model/user';
import { File } from '../model/file';

const USER_URL = 'http://localhost:8080/api/users';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json'}),
  observe: 'response' as 'body',
  withCredentials: true
};

@Injectable({
  providedIn: 'any'
})
export class UserService {

  constructor(private readonly _http: HttpClient) { }

  getAllUsers(): Observable<HttpResponse<User[]>> {
    return this._http.get<HttpResponse<User[]>>(USER_URL, httpOptions);
  }

  getUserById(id: number): Observable<HttpResponse<User | null>> {
    return this._http.get<HttpResponse<User | null>>(`${USER_URL}/${id}`, httpOptions);
  }

  getUserByUsername(username: string): Observable<HttpResponse<User | null>> {
    return this._http.get<HttpResponse<User | null>>(`${USER_URL}/name/${username}`, httpOptions);
  }

  getMyUser(): Observable<HttpResponse<User | null>> {
    return this._http.get<HttpResponse<User | null>>(USER_URL + '/myuser', httpOptions);
  }

  createUser(user: User): Observable<HttpResponse<User | null>> {
    return this._http.post<HttpResponse<User | null>>(USER_URL, user, httpOptions);
  }

  updateUser(user: User): Observable<HttpResponse<User | null>> {
    return this._http.put<HttpResponse<User | null>>(USER_URL, user, httpOptions);
  }
  
  deleteUser(id: number): Observable<HttpResponse<null>> {
    return this._http.delete<HttpResponse<null>>(`${USER_URL}/${id}`, httpOptions);
  }
}
