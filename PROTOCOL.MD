# Protocol documentation

NB: /api/ implicit in endpoints

## Account related

### Auth

> POST /auth/login/

Try to login

| param | type | description | required |
| ----- | ----- | ----- | ----- |
| username | string | the username to attempt login | yes |
| password | string | the password to attempt login | yes |

Response format:


| param | type | description |
| ----- | ----- | ----- |
| status | boolean | true if auth succeed, false otherwise |
| mfa_required | boolean | true if auth succeed and MFA is required, false otherwise |
| token | string | the token if status == true, null otherwise (not MFA token if it's required) |


> POST /auth/mfa/

Post the MFA code or backup code

| param | type | description | required |
| ----- | ----- | ----- | ----- |
| mfa | string | MFA or backup code | yes |

Response format:

| param | type | description |
| ----- | ----- | ----- |
| status | boolean | true if auth succeed, false otherwise |
| token | string | the token if status == true, null otherwise |

> TBD: Request lost password

### Account

> GET /account/info/me<br>
> GET /account/info/:account

Requests the user's information if they are availlable

Response format:
NB: Some of the params may be null if the logged in use isn't allowed to see them or they are not set

| param | type | description |
| ----- | ----- | ----- |
| firstname | string | The firstname |
| lastname | string | The lastname |
| birthday | ISO8601 | The user's birthday date |
| username | string | the username |
| mfa | boolean | whether the MFA is set or not |
| verified | boolean | if the email is verified |
| avatar | string | The avatar's ID |
| flag | int | bitwise number to define flags |
| id | string | The user's ID |


> PATCH /account/edit/

Edit the current user's account information

| param | type | description |
| ----- | ----- | ----- |
| firstname | string | The firstname |
| lastname | string | The lastname |
| birthday | ISO8601 | The user's birthday date |
| username | string | the username |
| password | string | The password may be required to change certain infos (names, username) |
| new_password | string | the new password, or null if no change was requested |

Response format:

| param | type | description |
| ----- | ----- | ----- |
| status | integer | The status code for the edit request: 1 = success; 0 = invalid password; 2 = password does not meet requirements |


> PUT /account/avatar/

Change the current user's avatar. Formats allowed: JPEG, PNG, GIF (single frame), WEBP, BMP
NB: Check headers not extensions!!!!

> DELETE /account/delete/

| param | type | description |
| ----- | ----- | ----- |
| password | string | The password |
| mfa_code | string | The current MFA code or backup code  |


Response format:

| param | type | description |
| ----- | ----- | ----- |
| success | boolean | Whether the account was deleted or not |

### MFA

> GET /mfa/init/

Request to QR code to initiate the MFA

Response format:

| param | type | description |
| ----- | ----- | ----- |
| key | string | The key for MFA |

> POST /mfa/enable/

Enable MFA using the current code

| param | type | description |
| ----- | ----- | ----- |
| code | string | The current MFA code |

Response format:

| param | type | description |
| ----- | ----- | ----- |
| success | true | Whether the enableling of MFA was successful or not |
| backup_codes | string[] | The backup codes |

> DELETE /mfa/disable/

Disable MFA. Requires password and MFA code.

| param | type | description |
| ----- | ----- | ----- |
| code | string | The current MFA code or backup |
| password | string | The password |

> GET /mfa/renew/

Request a renew of the MFA backup codes

| param | type | description |
| ----- | ----- | ----- |
| code | string | The current MFA code or backup |
| password | string | The password |

Response format:

| param | type | description |
| ----- | ----- | ----- |
| backup_codes | string[] | The backup codes. Null if the MFA code/backup or password is wrong |

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
| owner | string | The group's owner's ID |
| members | string[] | The IDs of the members of the group |

## Courses related

### Courses


### Reference cards




