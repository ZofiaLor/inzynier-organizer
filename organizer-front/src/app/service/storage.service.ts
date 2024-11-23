import { Injectable } from '@angular/core';
import { User } from '../model/user';
import { Directory } from '../model/directory';

const USER_KEY = 'auth-user';
const DIR_KEYS = ['my-curr, local-base, shared-curr']
const TAB__KEY = 'last-tab';
// https://developer.mozilla.org/en-US/docs/Web/API/Window/sessionStorage
// https://developer.mozilla.org/en-US/docs/Web/API/Window/localStorage

@Injectable({
  providedIn: 'root'
})
export class StorageService {

  constructor() { }

  clean(): void {
    window.localStorage.clear();
    window.sessionStorage.clear();
  }

  public saveUser(user: User): void {
    window.localStorage.removeItem(USER_KEY);
    window.localStorage.setItem(USER_KEY, JSON.stringify(user));
  }

  public getUser(): User | undefined {
    const user = window.localStorage.getItem(USER_KEY);
    if (user) {
      return JSON.parse(user);
    }

    return;
  }

  public saveCurrentDir(currentDir: Directory | undefined): void {
    window.sessionStorage.removeItem(DIR_KEYS[0]);
    if (currentDir) window.sessionStorage.setItem(DIR_KEYS[0], JSON.stringify(currentDir));
  }

  public getCurrentDir(): Directory | undefined {
    const currentDir = window.sessionStorage.getItem(DIR_KEYS[0]);
    if (currentDir) {
      return JSON.parse(currentDir);
    }
    return undefined;
  }

  public saveLocalBase(localBase: number | undefined): void {
    window.sessionStorage.removeItem(DIR_KEYS[1]);
    if (localBase) window.sessionStorage.setItem(DIR_KEYS[1], JSON.stringify(localBase));
  }

  public getLocalBase(): number | undefined {
    const localBase = window.sessionStorage.getItem(DIR_KEYS[1]);
    if (localBase) {
      return JSON.parse(localBase);
    }
    return undefined;
  }

  public saveSharedDir(currentShared: Directory | undefined): void {
    window.sessionStorage.removeItem(DIR_KEYS[2]);
    if (currentShared) window.sessionStorage.setItem(DIR_KEYS[2], JSON.stringify(currentShared));
  }

  public getCurrentShared(): Directory | undefined {
    const currentShared = window.sessionStorage.getItem(DIR_KEYS[2]);
    if (currentShared) {
      return JSON.parse(currentShared);
    }
    return undefined;
  }

  public saveTab(tab: number): void {
    window.sessionStorage.removeItem(TAB__KEY);
    window.sessionStorage.setItem(TAB__KEY, JSON.stringify(tab));
  }

  public getTab(): number {
    const tab = window.sessionStorage.getItem(TAB__KEY);
    if (tab) {
      return JSON.parse(tab);
    }
    window.sessionStorage.setItem(TAB__KEY, JSON.stringify(0));
    return 0;
  }

  public isLoggedIn(): boolean {
    const user = window.localStorage.getItem(USER_KEY);
    if (user) {
      return true;
    }

    return false;
  }
}
