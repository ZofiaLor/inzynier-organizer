# Lista Endpointów

## Authorization

### Log In
```POST /auth/login```

Logowanie użytkownika.

Wymagane w ciele zapytania: nazwa użytkownika (username), hasło (password)
```json
{
    "username": "newUser",
    "password": "password"
}
```

Odpowiedzi:
- Poprawne zalogowanie: "Success", kod 200 (OK)
- Niepoprawne hasło/login, brak hasła/loginu: kod 401 (Unauthorized)
### Log Out
```POST /auth/logout```

Wylogowanie użytkownika.

Brak wymaganego ciała zapytania.

```json
{}
```

Odpowiedź: "Success", kod 200 (OK)
```
Success
```
### Register
```POST /auth/register```

Rejestracja nowego użytkownika.

Wymagana nazwa użytkownika i hasło, opcjonalny adres email oraz imię (name). Pozostałe parametry są ignorowane.
```json
{
    "username": "someUser",
    "password": "password",
    "email": "some@email.com",
    "name": "Some User"
}
```
Odpowiedzi:
- Poprawne zarejestrowanie: obiekt utworzonego użytkownika z zakodowanym hasłem i rolą ROLE_USER, kod 200 (OK)
- Próba zarejestrowania użytkownika o istniejącej już nazwie: kod 403 (Forbidden)
- Brak nazwy użytkownika lub hasła: kod 400 (Bad Request)

### Grant/Revoke Admin Privilege
```PUT /auth/grant```
```PUT /auth/revoke```

Nadanie/odebranie roli Administratora użytkownikowi.

Wymagana nazwa użytkownika.
```json
{
    "username": "newUser"
}
```
Odpowiedzi:
- Poprawne nadanie/odebranie roli: obiekt użytkownika, kod 200 (OK)
- Nieistniejąca nazwa użytkownika: kod 401 (Unauthorized)
- Próba nadania/odebrania sobie roli: kod 400 (Bad Request)

## Users

### Get All Users
```GET /users```

Zwraca wszystkich użytkowników.

Odpowiedź: lista obiektów użytkowników, kod 200 (OK)

### Get User By Username
```GET /users/name/{username}```

Zwraca użytkownika o podanej nazwie.

Odpowiedzi:
- Użytkownik istnieje: obiekt użytkownika, kod 200 (OK)
- Użytkownik nie istnieje: kod 404 (Not Found)

### Get User By ID
```GET /users/{id}```

Zwraca użytkownika o podanym ID.

Odpowiedzi:
- Użytkownik istnieje: obiekt użytkownika, kod 200 (OK)
- Użytkownik nie istnieje: kod 404 (Not Found)
- Brak ID: kod 400 (Bad Request)

### Update User
```PUT /users```

Aktualizuje użytkownika.

Wymagane podanie ID użytkownika w ciele zapytania.
```json
{
    "id": 5,
    "name": "New User 2"
}
```

Odpowiedzi:
- Pomyślna aktualizacja: obiekt użytkownika, kod 200 (OK)
- Nie znaleziono ID: kod 404 (Not Found)
- Próba zmiany nazwy użytkownika na już istniejącą/nie podano ID: kod 400 (Bad Request)

### Delete User

```DELETE /users/{id}```

Usuwa użytkownika o podanym ID.

Odpowiedzi:
- ID istnieje: kod 200 (OK)
- ID nie istnieje: kod 404 (Not Found)

## Directories

### Get All Directories

```GET /directories```

Zwraca wszystkie katalogi.

Odpowiedź: lista obiektów katalogów, kod 200 (OK)

### Get All My Directories
```GET /directories/mydirs```

Zwraca wszystkie katalogi aktualnie zalogowanego użytkownika.

Odpowiedź: lista obiektów katalogów, kod 200 (OK)

### Get Directories By Parent ID
```GET /directories/subdirs/{id}```

Zwraca katalogi podrzędne katalogu o podanym ID.

Odpowiedzi:
- ID istnieje: lista obiektów katalogów, kod 200 (OK)
- ID nie istnieje: kod 404 (Not Found)
- Nie podano ID: kod 400 (Bad Request)
- Brak dostępu: kod 403 (Forbidden)

### Get Directory By ID
```GET /directories/{id}```

Zwraca katalog o podanym ID.

Odpowiedzi:
- ID istnieje: obiekt katalogu, kod 200 (OK)
- ID nie istnieje: kod 404 (Not Found)
- Nie podano ID: kod 400 (Bad Request)
- Brak dostępu: kod 403 (Forbidden)

### Create Directory
```POST /directories```

Tworzy nowy katalog.

Brak wymaganych parametrów, można podać nazwę oraz ID rodzica.
```json
{
    "name": "Test Directory",
    "parent": 3
}
```
Odpowiedź: obiekt nowego katalogu, kod 200 (OK)

### Update Directory
```PUT /directories```

Aktualizuje katalog.

Wymagane podanie ID katalogu w ciele zapytania.
```json
{
    "id": 2,
    "name": "Some Directory"
}
```
Odpowiedzi:
- Pomyślna aktualizacja: obiekt katalogu, kod 200 (OK)
- Brak obiektu: kod 400 (Bad Request)
- Nie znaleziono ID: kod 404 (Not Found)
- Brak dostępu do edycji: kod 403 (Forbidden)

### Delete Directory

```DELETE /directories/{id}```

Usuwa katalog o podanym ID.

Odpowiedzi:
- ID istnieje: kod 200 (OK)
- ID nie istnieje: kod 404 (Not Found)

## Files

### Get All Files
```GET /files```

Zwraca wszystkie pliki.

Odpowiedź: lista obiektów plików, kod 200 (OK)

### Get All My Files
```GET /files/myfiles```

Zwraca wszystkie pliki aktualnie zalogowanego użytkownika.

Odpowiedź: lista obiektów plików, kod 200 (OK)

### Get Files In Directory
```GET /files/dir/{id}```

Zwraca wszystkie pliki w katalogu o podanym ID.

Odpowiedzi:
- ID istnieje: lista obiektów katalogów, kod 200 (OK)
- ID nie istnieje: kod 404 (Not Found)
- Brak dostępu: kod 403 (Forbidden)

### Get File By ID
```GET /files/{id}```

Zwraca katalog o podanym ID.

Odpowiedzi:
- ID istnieje: obiekt katalogu, kod 200 (OK)
- ID nie istnieje: kod 404 (Not Found)
- Nie podano ID: kod 400 (Bad Request)
- Brak dostępu: kod 403 (Forbidden)

### Create Event
```POST /files/event```

Tworzy nowe wydarzenie.
```json
{
    "name": "My First Event",
    "parent": 3,
    "textContent": "Hello World! This is my first event",
    "location": "Katowice"
}
```
Odpowiedź: obiekt nowego wydarzenia, kod 200 (OK)

### Update Event
```PUT /files/event```

Aktualizuje wydarzenie.

Wymagane podanie ID wydarzenia w ciele zapytania.
```json
{
    "id": 2,
    "location": "Gliwice"
}
```
Odpowiedzi:
- Pomyślna aktualizacja: obiekt wydarzenia, kod 200 (OK)
- Brak obiektu: kod 400 (Bad Request)
- Nie znaleziono ID: kod 404 (Not Found)
- Brak dostępu do edycji: kod 403 (Forbidden)

### Create Note
```POST /files/note```

Tworzy nową notatkę.
```json
{
  "name": "My First Note",
  "parent": 3,
  "textContent": "Hello World! This is my first note"
}
```
Odpowiedź: obiekt nowej notatki, kod 200 (OK)

### Update Note
```PUT /files/note```

Aktualizuje notatkę.

Wymagane podanie ID notatki w ciele zapytania.
```json
{
  "id": 1,
  "name": "My Note"
}
```
Odpowiedzi:
- Pomyślna aktualizacja: obiekt notatki, kod 200 (OK)
- Brak obiektu: kod 400 (Bad Request)
- Nie znaleziono ID: kod 404 (Not Found)
- Brak dostępu do edycji: kod 403 (Forbidden)

### Create Task
```POST /files/task```

Tworzy nowe zadanie.
```json
{
  "name": "My First Task",
  "parent": 2,
  "textContent": "Hello World! This is my first task",
  "isFinished": false
}
```
Odpowiedź: obiekt nowego zadania, kod 200 (OK)

### Update Task
```PUT /files/task```

Aktualizuje zadanie.

Wymagane podanie ID zadania w ciele zapytania.
```json
{
  "id": 3,
  "isFinished": true
}
```
Odpowiedzi:
- Pomyślna aktualizacja: obiekt zadania, kod 200 (OK)
- Brak obiektu: kod 400 (Bad Request)
- Nie znaleziono ID: kod 404 (Not Found)
- Brak dostępu do edycji: kod 403 (Forbidden)

### Delete File

```DELETE /files/{id}```

Usuwa plik o podanym ID.

Odpowiedzi:
- ID istnieje: kod 200 (OK)
- ID nie istnieje: kod 404 (Not Found)

## Access Directory

### Get All AccessDirectories
```GET /ad```

Zwraca wszystkie obiekty AccessDirectory.

Odpowiedź: lista obiektów AccessDirectory, kod 200 (OK)

### Get AccessDirectory By User And Directory
```GET /ad/{user}/{dir}```

Zwraca obiekt AccessDirectory dla podanego ID użytkownika i katalogu.

Odpowiedzi:
- ID istnieją: obiekt AccessDirectory, kod 200 (OK)
- Nie istnieje obiekt z podanymi ID: kod 404 (Not Found)
- Brak ID: kod 400 (Bad Request)

### Modify AccessDirectory
```POST /ad```

Tworzy lub aktualizuje obiekt AccessDirectory o podanych ID użytkownika i katalogu.

Wymagane podanie obydwu ID w ciele zapytania.
```json
{
    "id": {
        "userId": 4,
        "directoryId": 3
    },
    "accessPrivilege": 1
}
```
Odpowiedzi:
- Pomyślne utworzenie/aktualizacja: obiekt AccessDirectory, kod 200 (OK)
- Nie istnieje któreś z ID: kod 404 (Not Found)
- Brak ID: kod 401 (Unauthorized)

### Delete AccessDirectory

```DELETE /ad/{user}/{dir}```

Usuwa obiekt AccessDirectory o podanych ID użytkownika i katalogu.

Odpowiedzi:
- ID istnieją: kod 200 (OK)
- ID nie istnieją: kod 404 (Not Found)

## Access File

### Get All AccessFiles
```GET /af```

Zwraca wszystkie obiekty AccessFile.

Odpowiedź: lista obiektów AccessFile, kod 200 (OK)

### Get AccessFile By User And File
```GET /af/{user}/{file}```

Zwraca obiekt AccessFile dla podanego ID użytkownika i pliku.

Odpowiedzi:
- ID istnieją: obiekt AccessFile, kod 200 (OK)
- Nie istnieje obiekt z podanymi ID: kod 404 (Not Found)
- Brak ID: kod 400 (Bad Request)

### Modify AccessFile
```POST /af```

Tworzy lub aktualizuje obiekt AccessFile o podanych ID użytkownika i pliku.

Wymagane podanie obydwu ID w ciele zapytania.
```json
{
    "id": {
        "userId": 4,
        "fileId": 3
    },
    "accessPrivilege": 1
}
```
Odpowiedzi:
- Pomyślne utworzenie/aktualizacja: obiekt AccessFile, kod 200 (OK)
- Nie istnieje któreś z ID: kod 404 (Not Found)
- Brak ID: kod 401 (Unauthorized)

### Delete AccessFile

```DELETE /af/{user}/{file}```

Usuwa obiekt AccessFile o podanych ID użytkownika i pliku.

Odpowiedzi:
- ID istnieją: kod 200 (OK)
- ID nie istnieją: kod 404 (Not Found)

## Event Dates

### Get All EventDates
```GET /ed```

Zwraca wszystkie obiekty EventDate.

Odpowiedź: lista obiektów EventDate, kod 200 (OK)

### Get EventDates By Event ID
```GET /ed?id={eventId}```

Zwraca obiekty EventDate dotyczące wydarzenia o podanym ID.

Odpowiedzi:
- ID istnieje: lista obiektów EventDate, kod 200 (OK)
- ID nie istnieje: kod 404 (Not Found)
- Nie podano ID: kod 400 (Bad Request)

### Get EventDate By ID
```GET /ed/{id}```

Zwraca obiekt EventDate o podanym ID.

Odpowiedzi:
- ID istnieje: obiekt EventDate, kod 200 (OK)
- ID nie istnieje: kod 404 (Not Found)
- Nie podano ID: kod 400 (Bad Request)

### Create EventDate
```POST /ed```

Tworzy obiekt EventDate