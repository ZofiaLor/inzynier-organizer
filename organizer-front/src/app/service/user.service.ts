import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../model/user';

const USER_URL = 'http://localhost:8080/api/user/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
};

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private readonly _http: HttpClient) { }

  getAllUsers(): Observable<User[]> {
    return this._http.get<User[]>(USER_URL);
  }

  getUserById(id: number): Observable<User | null> {
    return this._http.get<User>(`${USER_URL}/${id}`);
  }

  getUserByUsername(username: string): Observable<User | null> {
    return this._http.get<User>(`${USER_URL}/name/${username}`);
  }

  createUser(user: User): Observable<User | null> {
    return this._http.post<User>(USER_URL, user, httpOptions);
  }

  updateUser(user: User): Observable<User | null> {
    return this._http.put<User>(USER_URL, user, httpOptions);
  }
  
  deleteUser(id: number): Observable<null> {
    return this._http.delete<null>(`${USER_URL}/${id}`);
  }
}
