# Accounts

## Auth

> POST /auth/register

Registers an account

| field | type | description |
| ----- | ----- | ----- |
| email | string | email address |
| username | string | username for the new account |
| password | string | password of the new account |

Possible response status code: 201, 400

Response format: the created account, see `GET /account/me`

> POST /auth/login

Attempts to login

| field | type | description |
| ----- | ----- | ----- |
| username | string | the username to attempt login |
| password | string | the password to attempt login |

Possible response status code: 200, 401, 403, 404

Response format:

| field | type | description |
| ----- | ----- | ----- |
| mfa_required | boolean | true if MFA is required, false otherwise |
| reset_required? | boolean | true if password reset is required, false otherwise. not present if mfa_required is true |
| token | string | the non-MFA token (see [Authentication](REFERENCE.md#authentication)) |


> POST /auth/mfa

Attempts to pass MFA challenge, if required

| field | type | description |
| ----- | ----- | ----- |
| mfa | string | MFA or backup code |

Possible response status code: 200, 401

Response format:

| field | type | description |
| ----- | ----- | ----- |
| token | string | the MFA token (see [Authentication](REFERENCE.md#authentication)) |
| reset_required | boolean | true if password reset is required, false otherwise |


> POST /auth/reset

Requests a password reset. An email will be sent to the user with a link to the password reset page

| field | type | description |
|-----|-----|-----|
| email | string | Email of the account |

Possible response status codes: 204, 400, 404


> POST /auth/reset/execute

Performs a password reset

| field | type | description |
|-----|-----|-----|
| code | string | Unique code associated with the reset request |
| password | string | The new password |

Possible response status codes: 204, 400, 403

## Account

> GET /account/me

Requests the current user's information

Possible response status code: 200, 401

Response format:

| field | type | description |
| ----- | ----- | ----- |
| id | [snowflake](REFERENCE.md#snowflakes) | the user's ID |
| username | string | the username |
| firstname | ?string | the firstname |
| lastname | ?string | the lastname |
| birthday | ?ISO8601 timestamp | The user's birthday date |
| avatar | ?string | the avatar's ID |
| verified | boolean | if the email is verified |
| locale | string | the user's chosen locale |
| flag | number | the [flags](REFERENCE.md#user-flags) of this user |
| mfa | boolean | whether the MFA is set or not |


> GET /account/:account_id

Requests the user's information

Possible response status code: 200, 401, 404

Response format:

| field | type | description |
| ----- | ----- | ----- |
| id | [snowflake](REFERENCE.md#snowflakes) | the user's ID |
| username | string | the username |
| firstname | ?string | the firstname |
| lastname | ?string | the lastname |
| birthday | ?ISO8601 timestamp | the user's birthday date |
| avatar | ?string | the avatar's hash |
| locale | string | the user's chosen locale |
| flag | number | the [flags](REFERENCE.md#user-flags) of this user |


> PUT/PATCH /account

Edit the current user's account information

| field | type | description |
| ----- | ----- | ----- |
| username? | string | the username |
| firstname? | string | the firstname |
| lastname? | string | the lastname |
| birthday? | ISO8601 timestamp | the user's birthday date |
| avatar? | [avatar data](REFERENCE.md#avatar-data) | the new user's avatar |
| new_password? | string | the new password |
| password | string | the current password |

Possible response status code: 200, 400, 401, 413, 415

Response format: the updated user, see `GET /account/me`


> DELETE /account

Schedules an account for deletion in the following 14 days

| field | type | description |
| ----- | ----- | ----- |
| password | string | the current password |
| mfa_code | string | the current MFA code or backup code, if enabled |

Possible response status code: 204, 400, 401


> POST /account/harvest

Queues a data harvest request. Note: if a request is pending you can't queue another request.

Possible response status code: 202, 429


> GET /account/harvest

Gets data harvest status

Possible response status code: 200, 401

Response format:

| field | type | description |
|----|----|----|
| harvesting | boolean | if a harvest request is pending |
| file | ?string | link to the last data harvest archive, if exists |

## Settings

> GET /account/me/settings

Retrieves settings for the current user

Possible response status codes: 200, 401

Response format:

| field | type | description |
|-----|-----|-----|
| locale | string | user's selected locale |
| theme | string | user's selected theme |
| birthday.date | boolean | if the birthday day and month are public |
| birthday.year | boolean | if the birthday year is public |
| name.first | boolean | if the first name is public |
| name.last | boolean | if the last name is public. Note: setting last name to public will automatically publicize 1st name |
| name.display | boolean | if the real name should be used as display name. first name must be true to be enabled |
| privacy.add_friend | number | who can add you as friend - 0: no one, you have to send; 1: friends of friends; 2: anyone |
| privacy.receive_dm | number | who can send you dms - 0: friends; 1: friends of friends; 2: anyone. Note: Teachers bypass this |


> PUT/PATCH /account/me/settings

Updates account settings

See `GET /account/me/settings` response format to get all possible fields. All fields are optional on that endpoint.

Possible response status codes: 200, 401

Response format: Updated settings, see `GET /account/me/settings`

## MFA

> GET /account/mfa

Requests a key to initiate the MFA. QR code will have to be generated client-side

Response format:

| field | type | description |
| ----- | ----- | ----- |
| key | string | the key for MFA |

Possible response status code: 200, 400, 401


> POST /account/mfa

Enable MFA using the current code

| field | type | description |
| ----- | ----- | ----- |
| code | string | the current MFA code |

Possible response status code: 200, 400, 401

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

Possible response status code: 204, 400, 401


> POST /account/mfa/renew

Request a renew of the MFA backup codes. Note: old ones will be invalidated

| field | type | description |
| ----- | ----- | ----- |
| password | string | the password |

Possible response status code: 200, 400, 401

Response format:

| field | type | description |
| ----- | ----- | ----- |
| backup_codes | string[] | the backup codes |
