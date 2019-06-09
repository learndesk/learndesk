# Chat

For more documentation about the gateway, wait for us to write docs lol

## Groups

### Administration

> POST /groups

Creates a group. Note: An user can only own up to 10 groups. Teachers can own up to 50 groups


> GET /groups/:group_id

Requests information about a certain group.

Possible response status code: 200, 401, 403, 404

Response format:

| field | type | description |
| ----- | ----- | ----- |
| id | [snowflake](REFERENCE.md#snowflakes) | group id |
| name | string | group's name |
| owner | string | the group's owner's ID |
| members | [snowflake](REFERENCE.md#snowflakes)[] | members IDs (excludes owner) |
| icon | ?string | group's icon hash |


> PUT/PATCH /groups/:group_id

Updates group details. Can only be performed by the group owner

| field | type | description |
| ----- | ----- | ----- |
| name | ?string | new group name |
| icon | ?[avatar data](REFERENCE.md#avatar-data) | new group icon hash |

Possible response status code: 200, 400, 401, 403, 404, 413, 415

Response format: the updated user, see `GET /group/:group_id`


> DELETE /groups/:group_id

Deletes a group. Can only be performed by the group owner

Possible response status code: 204, 401, 403, 404

### Members management

> POST /groups/:group_id/members

Adds members to the group. **Note**: If a single member in the request can't be invited, the whole batch will fail


> DELETE /groups/:group_id/members/:user_id

## Channels

Channels are a virtual resource that represents the place where are DMs in a group or with an user

> GET /channels/groups/:group_id<br>
> GET /channels/users/:user_id

### Messages

> POST /channels/groups/:group_id/messages<br>
> POST /channels/users/:user_id/messages


> GET /channels/groups/:group_id/messages<br>
> GET /channels/users/:user_id/messages


> GET /channels/groups/:group_id/messages/pinned<br>
> GET /channels/users/:user_id/messages/pinned


> PUT/PATCH /channels/groups/:group_id/messages/:message_id<br>
> PUT/PATCH /channels/users/:user_id/messages/:message_id


> DELETE /channels/groups/:group_id/messages/:message_id<br>
> DELETE /channels/users/:user_id/messages/:message_id


> POST /channels/groups/:group_id/messages/:message_id/report<br>
> POST /channels/users/:user_id/messages/:message_id/report

### Reactions

> POST /channels/groups/:group_id/messages/:message_id/reactions<br>
> POST /channels/users/:user_id/messages/:message_id/reactions


> DELETE /channels/groups/:group_id/messages/:message_id/reactions<br>
> DELETE /channels/users/:user_id/messages/:message_id/reactions


> DELETE /channels/groups/:group_id/messages/:message_id/reactions/all<br>
> DELETE /channels/users/:user_id/messages/:message_id/reactions/all


<!--
<desc>

| field | type | description |
|-----|-----|----- |

Possible response status code: <codes>

Response format:

| field | type | description |
|----|----|----|
-->

