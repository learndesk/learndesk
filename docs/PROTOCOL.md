# Protocol documentation

**Note**: We're only documenting API scheme, without adding base path. It may vary depending on your configuration.

## General

### API Versioning

Learndesk exposes different versions of the API. You can specify it by prefixing the endpoint by `v{version_number}/`.
Omitting it will make you use the latest stable version of the API.

| version | status |
|----|----|
| 1 | soon:tm: |

### Errors

The API may return errors, and those errors are sent in the same format all the time.

| field | type | description |
| ----- | ----- | ----- |
| status | number | HTTP status of the request |
| code | number | error code, see [CODES.md](https://github.com/learndesk/backend/blob/master/docs/CODES.md) for more details |
| message | string | error message associated with the code |

### Rate limiting

To prevent abuses, the API have rate limits in place. As a good practice, **do not hardcode rate limits in your
application**! Make sure to properly parse headers and make sure you won't hit limits too often. Doing so may result
in IP bans, so be careful!

### Authentication

#### Tokens

Authentication is an extremely important part of an application, as it ensures your data is kept safe. As we provide
Two Factor Authentication (2FA or MFA), we designed our authentication flow to be secure and aware of this factor in
our token structure.

Here is how our tokens are structured:
```
OTQ3NjI0OTI5MjM3NDgzNTI.NjY2NDIwNjk.dGhpcyBpcyBhIHZlcnkgc2VjdXJlIHNpZ25hdHVyZSE
----------------------- ----------- -------------------------------------------
Account ID              Generation  Signature of everything that preceeds
                        Date
```

MFA tokens are just prefixed with `mfa.` (both in raw token and in signature). The only case where a non-MFA token is
allowed to perform requests to the API on behalf of an user that has MFA enabled is to finalize authentication.

#### Authorization

You must be authenticated to perform request to the API (except while logging in). For all authentication types,
this is performed with the `Authorization` HTTP header
```
Authorization: TOKEN
```

### Optional and nullable field

Fields that are optional (may be omitted in payload) are suffixed with a question mark, and fields that can contain
`null` have their type prefixed with a question mark.

| field | type |
|----|----|
| optional? | string |
| nullable | ?string |
| optional_and_nullable? | ?string |

## Accounts

### Auth

> POST /auth/login

Attempts to login

| field | type | description |
| ----- | ----- | ----- |
| username | string | the username to attempt login |
| password | string | the password to attempt login |

Response format:

| field | type | description |
| ----- | ----- | ----- |
| mfa_required | boolean | true if MFA is required, false otherwise |
| token | string | the non-MFA token (see [Authentication](#Authentication)) |


> POST /auth/mfa

Attempts to pass MFA challenge, if required

| field | type | description | required |
| ----- | ----- | ----- | ----- |
| mfa | string | MFA or backup code | yes |

Response format:

| field | type | description |
| ----- | ----- | ----- |
| token | string | the MFA token (see [Authentication](#Authentication)) |


> TBD: Request lost password

### Account

> GET /account/me

Requests the current user's information

Response format:

| field | type | description |
| ----- | ----- | ----- |
| id | string | the user's ID |
| username | string | the username |
| firstname | ?string | the firstname |
| lastname | ?string | the lastname |
| birthday | ?ISO8601 timestamp | The user's birthday date |
| avatar | ?string | the avatar's ID |
| verified | boolean | if the email is verified |
| flag | number | bitwise number to define flags |
| mfa | boolean | whether the MFA is set or not |


> GET /account/:account_id

Requests the user's information

Response format:

| field | type | description |
| ----- | ----- | ----- |
| id | string | the user's ID |
| username | string | the username |
| firstname | ?string | the firstname |
| lastname | ?string | the lastname |
| birthday | ?ISO8601 timestamp | the user's birthday date |
| avatar | ?string | the avatar's ID |
| flag | number | bitwise number to define flags |


> PATCH /account

Edit the current user's account information

| field | type | description |
| ----- | ----- | ----- |
| firstname? | string | the firstname |
| lastname? | string | the lastname |
| birthday? | ISO8601 | the user's birthday date |
| username? | string | the username |
| new_password? | string | the new password |
| password | string | the current password |


> PUT /account/avatar

Change the current user's avatar. If an animated avatar is sent, it'll be cropped to the 1st frame (GIFs, APNGs, ...)<br>
Allowed formats: JPEG, PNG, GIF, WEBP, BMP


> DELETE /account

Schedules an account for deletion in the following 14 days

| field | type | description |
| ----- | ----- | ----- |
| password | string | the current password |
| mfa_code | string | the current MFA code or backup code, if enabled |

### MFA

> GET /account/mfa

Requests a key to initiate the MFA. QR code will have to be generated client-side

Response format:

| field | type | description |
| ----- | ----- | ----- |
| key | string | the key for MFA |


> POST /account/mfa

Enable MFA using the current code

| field | type | description |
| ----- | ----- | ----- |
| code | string | the current MFA code |

Response format:

| field | type | description |
| ----- | ----- | ----- |
| backup_codes | string[] | the backup codes |


> DELETE /account/mfa

Disable MFA. Requires password and MFA code

| field | type | description |
| ----- | ----- | ----- |
| code | string | the current MFA code or backup |
| password | string | the password |


> POST /account/mfa/renew

Request a renew of the MFA backup codes. Note: old ones will be invalidated

| field | type | description |
| ----- | ----- | ----- |
| password | string | the password |

Response format:

| field | type | description |
| ----- | ----- | ----- |
| backup_codes | string[] | the backup codes |

### GDPR

TBD

## Groups related

### Group administration

> GET /group/info/:group

Request information about a certain group.
NB: Certain infos may be null if the user doesn't have the permissions to see them, or if they are not set.

Response format:

| param | type | description |
| ----- | ----- | ----- |
| name | string | Group's name |
| id | string | Group's ID |
| owner | string | The group's owner's ID |
| members | string[] | The IDs of the members of the group (must NOT contain owner) |
| image | string | Group's image ID |
| creation_date | ISO8601 | Group's creation date |


> PUT /group/info/

## Courses related

### Courses


### Reference cards
