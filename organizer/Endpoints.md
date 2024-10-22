# Endpoint List

## Authorization

### Log In
```POST /auth/login```

Obligatory username and password in the request body.
```json
{
    "username": "newUser",
    "password": "password"
}
```
### Log Out
```POST /auth/logout```

Empty request body
```json
{}
```
### Register
```POST /auth/register```

Obligatory username and password in the request body, optional e-mail address, name.
```json
{
    "username": "someUser",
    "password": "password",
    "email": "some@email.com",
    "name": "Some User"
}
```