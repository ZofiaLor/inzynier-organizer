# Lista Endpointów

## Authorization

### Log In
```POST /api/auth/login```

Logowanie użytkownika.

Wymagane w ciele zapytania: nazwa użytkownika (username), hasło (password)
```json
{
    "username": "newUser",
    "password": "password"
}
```

Odpowiedzi:
- Poprawne zalogowanie: obiekt użytkownika z pustym hasłem, kod 200 (OK)
- Niepoprawne hasło/login: "Incorrect credentials", kod 403 (Forbidden)
- Brak hasła/loginu: "Empty username or password", kod 400 (Bad Request)
### Log Out
```POST /api/auth/logout```

Wylogowanie użytkownika.

Brak wymaganego ciała zapytania.

```json
{}
```

Odpowiedź: "Success", puste ciasteczka w nagłówku, kod 200 (OK)
```
Success
```
### Register
```POST /api/auth/register```

Rejestracja nowego użytkownika i założenie jego katalogu bazowego Base Directory.

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
- Poprawne zarejestrowanie: obiekt utworzonego użytkownika z pustym hasłem i rolą ROLE_USER, kod 200 (OK)
- Próba zarejestrowania użytkownika o istniejącej już nazwie: kod 403 (Forbidden)
- Brak nazwy użytkownika lub hasła: kod 400 (Bad Request)

### Change Password
```PUT /api/password```

Pozwala na zmianę hasła użytkownika.

Wymaga podania starego hasła, służącego do zatwierdzenia zmiany, oraz nowego hasła.

```json
{
  "oldPassword": "pwdOld",
  "newPassword": "pwdNew"
}
```
Odpowiedzi:
- Poprawna zmiana hasła: "Success", kod 200 (OK)
- Brak podanego starego lub nowego hasła: kod 400 (Bad Request)
- Użytkownik nie istnieje: kod 404 (Not Found)
- Stare hasło jest niepoprawne: kod 403 (Forbidden)

### Grant/Revoke Admin Privilege
```PUT /api/auth/grant```
```PUT /api/auth/revoke```

Nadanie/odebranie roli Administratora użytkownikowi.

Wymagana nazwa użytkownika. Endpoint dostępny jest tylko administratorowi.
```json
{
    "username": "newUser"
}
```
Odpowiedzi:
- Poprawne nadanie/odebranie roli: obiekt użytkownika, kod 200 (OK)
- Nieistniejąca nazwa użytkownika: kod 404 (Not Found)
- Próba nadania/odebrania sobie roli: kod 400 (Bad Request)
- Użytkownik nie jest administratorem: kod 403 (Forbidden)

### Refresh Token
```POST /api/refreshtoken```

Odświeża JWT odpowiadający za autoryzację użytkownika, jeżeli refreshToken zapisany w ciasteczkach nie stracił ważności.

Ciało zapytania jest ignorowane.

Odpowiedzi:
- Poprawne odświeżenie JWT: nowe ciasteczko JWT w nagłówku, kod 200 (OK)
- refreshToken stracił ważność: kod 403 (Forbidden)
- Nie znaleziono refreshToken w bazie danych: kod 404 (Not Found)
- refreshToken w ciasteczku jest pusty lub null: kod 400 (Bad Request)

## Users

### Get All Users
```GET /api/users```

Zwraca wszystkich użytkowników z pustymi hasłami. Endpoint dostępny tylko administratorowi.

Odpowiedzi: 
- Użytkownik jest administratorem: lista obiektów użytkowników, kod 200 (OK)
- Użytkownik nie jest administratorem: kod 403 (Forbidden)

### Get All Users Safe
```GET /api/users/safe```

Zwraca ID, nazwy użytkownika i nazwy wszystkich użytkowników.

Odpowiedź: lista obiektów użytkowników, kod 200 (OK)

### Get User By ID
```GET /api/users/{id}```

Zwraca użytkownika o podanym ID z pustym hasłem. Endpoint dostępny tylko administratorowi.

Odpowiedzi:
- Użytkownik istnieje: obiekt użytkownika, kod 200 (OK)
- Użytkownik nie istnieje: kod 404 (Not Found)
- Brak ID: kod 400 (Bad Request)
- Użytkownik nie jest administratorem: kod 403 (Forbidden)

### Update User
```PUT /api/users```

Aktualizuje użytkownika.

Wymagane podanie ID użytkownika w ciele zapytania.
```json
{
    "id": 5,
    "name": "New User 2"
}
```
Parametry brane pod uwagę:
- username
- name
- email

Odpowiedzi:
- Pomyślna aktualizacja: obiekt użytkownika z pustym hasłem, kod 200 (OK)
- Nie znaleziono ID: kod 404 (Not Found)
- Próba zmiany nazwy użytkownika na już istniejącą/nie podano ID: kod 400 (Bad Request)

### Delete User

```DELETE /api/users/{id}```

Usuwa użytkownika o podanym ID, wraz z jego katalogami, głosami i powiadomieniami. Endpoint dostępny tylko administratorowi.

Odpowiedzi:
- ID istnieje: kod 200 (OK)
- ID nie istnieje: kod 404 (Not Found)
- Próba usunięcia własnego użytkownika/użytkownik nie jest administratorem: kod 403 (Forbidden)

### Delete My User

```DELETE /api/users/delete```

Wylogowuje i usuwa aktualnie zalogowanego użytkownika, wraz z jego katalogami, głosami i powiadomieniami.

Odpowiedzi:
- Poprawne usunięcie: puste ciasteczka w nagłówku, kod 200 (OK)
- Nie znaleziono użytkownika: kod 404 (Not Found)

## Directories

### Get My Base Directory
```GET /api/directories/basedirs```

Zwraca katalog bazowy aktualnie zalogowanego użytkownika.

Odpowiedź: obiekt katalogu bazowego, kod 200 (OK)

### Get Directories By Parent ID
```GET /api/directories/subdirs/{id}```

Zwraca katalogi podrzędne katalogu o podanym ID.

Odpowiedzi:
- ID istnieje: lista obiektów katalogów, kod 200 (OK)
- ID nie istnieje: kod 404 (Not Found)
- Nie podano ID: kod 400 (Bad Request)
- Brak dostępu: kod 403 (Forbidden)

### Get Directory By ID
```GET /api/directories/{id}```

Zwraca katalog o podanym ID.

Odpowiedzi:
- ID istnieje: obiekt katalogu, kod 200 (OK)
- ID nie istnieje: kod 404 (Not Found)
- Nie podano ID: kod 400 (Bad Request)
- Brak dostępu: kod 403 (Forbidden)

### Check Directory Edit Access
```GET /api/directories/check/{id}```

Sprawdza, czy aktualnie zalogowany użytkownik posiada prawa do edycji katalogu o danym ID.

Odpowiedzi:
- ID istnieje: true jeżeli użytkownik może edytować katalog, false jeżeli nie, kod 200 (OK)
- Nie podano ID: kod 400 (Bad Request)
- ID nie istnieje: kod 404 (Not Found)

### Create Directory
```POST /api/directories```

Tworzy nowy katalog.

Wymagane ID rodzica, można podać nazwę. W przypadku niepodania nazwy, domyślnie ustawiana jest ona na "Unnamed Directory".
```json
{
    "name": "Test Directory",
    "parent": 3
}
```
Odpowiedzi: 
- Poprawne utworzenie katalogu: obiekt nowego katalogu, kod 200 (OK)
- Brak ID rodzica: kod 400 (Bad Request)

### Update Directory
```PUT /api/directories```

Aktualizuje katalog.

Wymagane podanie ID katalogu w ciele zapytania.
```json
{
    "id": 2,
    "name": "Some Directory"
}
```
Parametry brane pod uwagę:
- name

Odpowiedzi:
- Pomyślna aktualizacja: obiekt katalogu, kod 200 (OK)
- Brak obiektu: kod 400 (Bad Request)
- Nie znaleziono ID: kod 404 (Not Found)
- Brak dostępu do edycji: kod 403 (Forbidden)

### Delete Directory

```DELETE /api/directories/{id}```

Usuwa katalog o podanym ID, wraz z jego plikami.

Odpowiedzi:
- ID istnieje: kod 200 (OK)
- ID nie istnieje: kod 404 (Not Found)

## Files

### Get Files In Directory
```GET /api/files/dir/{id}```

Zwraca wszystkie pliki w katalogu o podanym ID.

Odpowiedzi:
- ID istnieje: lista obiektów katalogów, kod 200 (OK)
- ID nie istnieje: kod 404 (Not Found)
- Brak dostępu: kod 403 (Forbidden)

### Get File By ID
```GET /api/files/{id}```

Zwraca katalog o podanym ID.

Odpowiedzi:
- ID istnieje: obiekt katalogu, kod 200 (OK)
- ID nie istnieje: kod 404 (Not Found)
- Nie podano ID: kod 400 (Bad Request)
- Brak dostępu: kod 403 (Forbidden)

### Check File Edit Access
```GET /api/files/check/{id}```

Sprawdza, czy aktualnie zalogowany użytkownik posiada prawa do edycji pliku o danym ID.

Odpowiedzi:
- ID istnieje: true jeżeli użytkownik może edytować plik, false jeżeli nie, kod 200 (OK)
- Nie podano ID: kod 400 (Bad Request)
- ID nie istnieje: kod 404 (Not Found)

### Create Event
```POST /api/files/event```

Tworzy nowe wydarzenie. 

Wymagane jest podanie ID katalogu rodzica. Domyślną nazwą jest "Unnamed Event".
```json
{
    "name": "My First Event",
    "parent": 3,
    "textContent": "Hello World! This is my first event",
    "location": "Katowice"
}
```
Odpowiedzi: 
- Pomyślne utworzenie: obiekt nowego wydarzenia, kod 200 (OK)
- Nie podano ID rodzica: kod 400 (Bad Request)

### Update Event
```PUT /api/files/event```

Aktualizuje wydarzenie.

Wymagane podanie ID wydarzenia w ciele zapytania.
```json
{
    "id": 2,
    "location": "Gliwice"
}
```
Parametry brane pod uwagę:
- name
- textContent
- startDate
- endDate
- location

Odpowiedzi:
- Pomyślna aktualizacja: obiekt wydarzenia, kod 200 (OK)
- Brak obiektu: kod 400 (Bad Request)
- Nie znaleziono ID: kod 404 (Not Found)
- Brak dostępu do edycji: kod 403 (Forbidden)

### Create Note
```POST /api/files/note```

Tworzy nową notatkę. 

Wymagane jest podanie ID katalogu rodzica. Domyślną nazwą jest "Unnamed Note".
```json
{
  "name": "My First Note",
  "parent": 3,
  "textContent": "Hello World! This is my first note"
}
```
Odpowiedzi:
- Pomyślne utworzenie: obiekt nowej notatki, kod 200 (OK)
- Nie podano ID rodzica: kod 400 (Bad Request)

### Update Note
```PUT /api/files/note```

Aktualizuje notatkę.

Wymagane podanie ID notatki w ciele zapytania.
```json
{
  "id": 1,
  "name": "My Note"
}
```
Parametry brane pod uwagę:
- name
- textContent

Odpowiedzi:
- Pomyślna aktualizacja: obiekt notatki, kod 200 (OK)
- Brak obiektu: kod 400 (Bad Request)
- Nie znaleziono ID: kod 404 (Not Found)
- Brak dostępu do edycji: kod 403 (Forbidden)

### Create Task
```POST /api/files/task```

Tworzy nowe zadanie. 

Wymagane jest podanie ID katalogu rodzica. Domyślną nazwą jest "Unnamed Task".
```json
{
  "name": "My First Task",
  "parent": 2,
  "textContent": "Hello World! This is my first task",
  "isFinished": false
}
```
Odpowiedzi:
- Pomyślne utworzenie: obiekt nowego zadania, kod 200 (OK)
- Nie podano ID rodzica: kod 400 (Bad Request)

### Update Task
```PUT /api/files/task```

Aktualizuje zadanie.

Wymagane podanie ID zadania w ciele zapytania.
```json
{
  "id": 3,
  "isFinished": true
}
```
Parametry brane pod uwagę:
- name
- textContent
- isFinished
- deadline

Odpowiedzi:
- Pomyślna aktualizacja: obiekt zadania, kod 200 (OK)
- Brak obiektu: kod 400 (Bad Request)
- Nie znaleziono ID: kod 404 (Not Found)
- Brak dostępu do edycji: kod 403 (Forbidden)

### Delete File

```DELETE /api/files/{id}```

Usuwa plik o podanym ID, w przypadku wydarzenia - wraz z jego obiektami EventDate.

Odpowiedzi:
- ID istnieje: kod 200 (OK)
- ID nie istnieje: kod 404 (Not Found)

## Access Directory

### Get AccessDirectory By User
```GET /api/ad/user/{user}```

Zwraca listę obiektów AccessDirectory dla podanego ID użytkownika.

Lista jest pusta dla nieistniejącego użytkownika.

Odpowiedzi:
- ID istnieje: lista obiektów AccessDirectory, kod 200 (OK)
- Brak ID: kod 400 (Bad Request)

### Get AccessDirectory By Directory
```GET /api/ad/dir/{dir}```

Zwraca listę obiektów AccessDirectory dla podanego ID katalogu.

Lista jest pusta dla nieistniejącego katalogu.

Odpowiedzi:
- ID istnieje: lista obiektów AccessDirectory, kod 200 (OK)
- Brak ID: kod 400 (Bad Request)

### Modify AccessDirectory
```POST /api/ad```

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
- Brak ID: kod 400 (Bad Request)

### Delete AccessDirectory

```DELETE /api/ad/{user}/{dir}```

Usuwa obiekt AccessDirectory o podanych ID użytkownika i katalogu.

Odpowiedzi:
- ID istnieją: kod 200 (OK)
- ID nie istnieją: kod 404 (Not Found)

## Access File

### Get AccessFile By User
```GET /api/af/user/{user}```

Zwraca listę obiektów AccessFile dla podanego ID użytkownika.

Lista jest pusta dla nieistniejącego użytkownika.

Odpowiedzi:
- ID istnieje: lista obiektów AccessFile, kod 200 (OK)
- Brak ID: kod 400 (Bad Request)

### Get AccessFile By File
```GET /api/af/file/{file}```

Zwraca listę obiektów AccessFile dla podanego ID pliku.

Lista jest pusta dla nieistniejącego pliku.

Odpowiedzi:
- ID istnieje: lista obiektów AccessFile, kod 200 (OK)
- Brak ID: kod 400 (Bad Request)

### Modify AccessFile
```POST /api/af```

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
- Brak ID: kod 400 (Bad Request)

### Delete AccessFile

```DELETE /api/af/{user}/{file}```

Usuwa obiekt AccessFile o podanych ID użytkownika i pliku.

Odpowiedzi:
- ID istnieją: kod 200 (OK)
- ID nie istnieją: kod 404 (Not Found)

## Event Dates

### Get All EventDates
```GET /api/ed```

Zwraca wszystkie obiekty EventDate.

Odpowiedź: lista obiektów EventDate, kod 200 (OK)

### Get EventDates By Event ID
```GET /api/ed?id={eventId}```

Zwraca obiekty EventDate dotyczące wydarzenia o podanym ID.

Odpowiedzi:
- ID istnieje: lista obiektów EventDate, kod 200 (OK)
- ID nie istnieje: kod 404 (Not Found)
- Nie podano ID: kod 400 (Bad Request)

### Get EventDate By ID
```GET /api/ed/{id}```

Zwraca obiekt EventDate o podanym ID.

Odpowiedzi:
- ID istnieje: obiekt EventDate, kod 200 (OK)
- ID nie istnieje: kod 404 (Not Found)
- Nie podano ID: kod 400 (Bad Request)

### Create EventDate
```POST /api/ed```

Tworzy obiekt EventDate. 

Wymagane jest podanie ID wydarzenia, a także początku i końca terminu. Wynik całkowity ustawiany jest na 0.
```json
{
    "event": 2,
    "start" : "2024-10-18T12:00:00",
    "end": "2024-10-18T12:30:00"
}
```
Odpowiedzi:
- Pomyślne utworzenie: nowy obiekt EventDate, kod 200 (OK)
- Brak ID/terminu początkowego/końcowego: kod 400 (Bad Request)

### Delete EventDate

```DELETE /api/ed/{id}```

Usuwa obiekt EventDate o podanym ID, wraz z jego głosami.

Odpowiedzi:
- ID istnieje: kod 200 (OK)
- ID nie istnieje: kod 404 (Not Found)

## Votes

### Get Votes By EventDate ID
```GET /api/votes/ed/{id}```

Zwraca głosy na termin EventDate o podanym ID.

Odpowiedzi:
- ID istnieje: lista obiektów głosów, kod 200 (OK)
- ID nie istnieje: kod 404 (Not Found)
- Nie podano ID: kod 400 (Bad Request)

### Get Current User's Vote By EventDate ID
```GET /api/votes/myvote/{id}```

Zwraca listę głosów oddanych przez aktualnie zalogowanego użytkownika na termin EventDate o podanym ID.

Odpowiedzi:
- ID istnieje: lista obiektów głosów, kod 200 (OK)
- ID nie istnieje: kod 404 (Not Found)
- Nie podano ID: kod 400 (Bad Request)

### Cast Vote
```POST /api/votes```

Sprawdza, czy zalogowany użytkownik oddał głos na dany termin, jeżeli nie, to tworzy nowy głos, jeżeli tak, to aktualizuje istniejący.
Modyfikuje wynik całkowity dla terminu EventDate.

Wymagane podanie ID EventDate.
```json
{
    "eventDate": 1,
    "score": 1
}
```
Odpowiedzi:
- Pomyślne oddanie/modyfikacja głosu: obiekt głosu, kod 200 (OK)
- Nie podano ID EventDate: kod 400 (Bad Request)
- Nie istnieje ID EventDate: kod 404 (Not Found)

### Delete Vote

```DELETE /api/vote/{id}```

Usuwa głos o podanym ID.

Odpowiedzi:
- ID istnieje: kod 200 (OK)
- ID nie istnieje: kod 404 (Not Found)

## Notifications

### Get All My Notifications
```GET /api/notifs/mynotifs```

Zwraca wszystkie wysłane powiadomienia aktualnie zalogowanego użytkownika.

Odpowiedź: lista obiektów powiadomień, kod 200 (OK)

### Get All My Read/Unread Notifications
```GET /api/notifs/mynotifs?read={read}```

Zwraca wszystkie wysłane odczytane/nieodczytane powiadomienia aktualnie zalogowanego użytkownika, w zależności od parametru read (true - odczytane, false - nieodczytane).

Odpowiedź: lista obiektów powiadomień, kod 200 (OK)

### Get Current User's Notification By File ID
```GET /api/notifs/file/{id}```

Zwraca listę niewysłanych powiadomień aktualnie zalogowanego użytkownika powiązanych z plikiem o podanym ID.

Odpowiedzi:
- ID istnieje: lista obiektów powiadomień, kod 200 (OK)
- ID nie istnieje: kod 404 (Not Found)
- Nie podano ID: kod 400 (Bad Request)

### Create Notification
```POST /api/notifs```

Tworzy powiadomienie.

Wymagane jest podanie ID użytkownika odbiorcy oraz pliku. Czas wysłania domyślnie ustawiany jest na czas utworzenia, powiadomienie jest domyślnie nieodczytane. Wysłanie odbywa się na podstawie porównania aktualnego czasu z czasem wysłania.
```json
{
  "user": 3,
  "file": 3,
  "message": "Test notif",
  "sendTimeSetting": "2024-10-28T15:30:00"
}
```
Odpowiedzi:
- Pomyślne utworzenie: nowy obiekt powiadomienia, kod 200 (OK)
- Brak ID użytkownika/pliku: kod 400 (Bad Request)
- Nie istnieje ID użytkownika/pliku: kod 404 (Not Found)

### Send Current User's Notifications
```PUT /api/notifs/send```

Wysyła powiadomienia aktualnie zalogowanego poprzez porównanie ich czasu wysłania z czasem aktualnym.

Odpowiedzi: 
- Poprawna aktualizacja powiadomień: kod 200 (OK)
- Nie znaleziono użytkownika: kod 404 (Not Found)

### Delete Notification

```DELETE /api/notifs/{id}```

Usuwa powiadomienie o podanym ID.

Odpowiedzi:
- ID istnieje: kod 200 (OK)
- ID nie istnieje: kod 404 (Not Found)