import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../model/user';

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

  getAllUsers(): Observable<User[]> {
    return this._http.get<User[]>(USER_URL, httpOptions);
  }

  getUserById(id: number): Observable<User | null> {
    return this._http.get<User>(`${USER_URL}/${id}`, httpOptions);
  }

  getUserByUsername(username: string): Observable<User | null> {
    return this._http.get<User>(`${USER_URL}/name/${username}`, httpOptions);
  }

  createUser(user: User): Observable<User | null> {
    return this._http.post<User>(USER_URL, user, httpOptions);
  }

  updateUser(user: User): Observable<User | null> {
    return this._http.put<User>(USER_URL, user, httpOptions);
  }
  
  deleteUser(id: number): Observable<null> {
    return this._http.delete<null>(`${USER_URL}/${id}`, httpOptions);
  }
}
