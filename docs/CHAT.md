# Chat

For more documentation about the gateway, wait for us to write docs lol

## Groups

### Administration

> POST /groups

Creates a group. Note: An user can only own up to 10 groups. Teachers can own up to 50 groups.

| field | type | description |
|----|----|----|
| name | string | group name |
| icon? | [avatar data](REFERENCE.md#avatar-data) | group icon |
| members? | [user identifier](REFERENCE.md#user-identifier)[] | members of the group |

Possible response status code: 201, 400, 401

Response format: The created group, see `/groups/:group_id` plus extra fields:

| field | type | description |
|----|----|----|
| failed_invites | [user identifier](REFERENCE.md#user-identifier)[] | member that cannot be invited due to their privacy settings


> GET /groups/:group_id

Requests information about a certain group.

Possible response status code: 200, 401, 403, 404

Response format:

| field | type | description |
| ----- | ----- | ----- |
| id | [snowflake](REFERENCE.md#snowflakes) | group id |
| name | string | group's name |
| owner | string | the group's owner's ID |
| members | [snowflake](REFERENCE.md#snowflakes)[] | member IDs (excludes owner) |
| icon | ?string | group's icon hash |


> PUT/PATCH /groups/:group_id

Updates group details. Can only be performed by the group owner.

| field | type | description |
| ----- | ----- | ----- |
| name? | string | new group name |
| icon? | [avatar data](REFERENCE.md#avatar-data) | new group icon |

Possible response status code: 200, 400, 401, 403, 404, 413, 415

Response format: the updated user, see `GET /group/:group_id`


> DELETE /groups/:group_id

Deletes a group. Can only be performed by the group owner.

Possible response status code: 204, 401, 403, 404

### Members management

> POST /groups/:group_id/members

Adds members to the group. Can only be performed by the group owner.<br>
**Note**: If a single member in the request can't be invited, the whole batch will fail

| field | type | description |
|----|----|----|
| members | [user identifier](REFERENCE.md#user-identifier) \| [user identifier](REFERENCE.md#user-identifier)[] | user or users to add to the group | 

Possible response status code: 200, 400, 401, 403, 404

Response format:

| field | type | description |
|----|----|----|
| members | [snowflake](REFERENCE.md#snowflakes)[] | updated list of group members |
| failed_invites | [user identifier](REFERENCE.md#user-identifier)[] | member that cannot be invited due to their privacy settings |


> DELETE /groups/:group_id/members/:user_id

Kicks a member from the group. Can only be performed by the group owner.

Possible response status code: 204, 401, 403, 404


> DELETE /groups/:group_id/members/me

Leaves the group. Cannot be performed by the group owner.

Possible response status code: 204, 401, 403, 404

## Channels

Channels are a virtual resource that represents the place where are messages in a group or with an user

### Metadata

> GET /channels/groups/:group_id<br>
> GET /channels/users/:user_id

Gets information about the number of pings and the last read message id

Possible response status code: 200, 401, 403, 404

Response format:

| field | type | description |
|----|----|----|
| read | [snowflake](REFERENCE.md#snowflakes) | last message read id |
| pings | number | the number of pings in the channel |


> POST /channels/groups/:group_id/ack<br>
> POST /channels/users/:user_id/ack

Marks a channel as read

Possible response status code: 204, 401, 403, 404

### Messages

> POST /channels/groups/:group_id/messages<br>
> POST /channels/users/:user_id/messages

Posts a message in the group DM

| field | type | description |
|----|----|----|
| content | string | message contents. can't be > 2000 chars |

Possible response status code: 204, 400, 401, 403, 404

> GET /channels/groups/:group_id/messages<br>
> GET /channels/users/:user_id/messages

Returns the messages for the channel.

| field | type | description |
|----|----|----|
| before? | [snowflake](REFERENCE.md#snowflakes) | fetch messages before this snowflake |
| after? | [snowflake](REFERENCE.md#snowflakes) | fetch messages after this snowflake. overridden is before is present |
| limit? | number | number of messages to fetch. between 1 and 100, defaults to 50 |

Possible response status code: 200, 400, 401, 403, 404

Response format: **array** of messages

| field | type | description |
|----|----|----|
| id | [snowflake](REFERENCE.md#snowflakes) | message id |
| author | object | partial user object of the message author |
| author.id | [snowflake](REFERENCE.md#snowflakes) | author's id |
| author.name | [snowflake](REFERENCE.md#snowflakes) | author's display name |
| author.avatar | string | author's avatar hash |
| contents | string | message contents |
| edited_at | ?ISO8601 timestamp | when this message was edited (or null if never) |
| pokes_me | boolean | whether this message pokes you |
| reactions | object[] | array of reactions |
| reactions[].name | string | emote name |
| reactions[].count | number | reaction count |
| type | number | the message type |

**Message types**
 - 0: Classic text message
 - 1: Invite to share a course or a sheet
 - 2: Invite accepted or refused
 - 3: Access to a course or a sheet revoked
 - 4: Someone joined/left the group

**Extra message fields**

 - For type 1, 2 and 3

| field | type | description |
|----|----|----|
| document | object | partial document object |
| document.id | [snowflake](REFERENCE.md#snowflakes) | document id |
| document.name | string | document name |
| document.owner | [snowflake](REFERENCE.md#snowflakes) | document name |
| document.invite | string | invite id (only for type 1) |

 - For type 4

| field | type | description |
|----|----|----|
| member | string | user name (frozen to what it was at the moment of message creation) |
| joined | boolean | if the user joined, or left |
 

> GET /channels/groups/:group_id/messages/pinned<br>
> GET /channels/users/:user_id/messages/pinned

Gets messages that should be pinned at the top of the chat box

Possible response status code: 200, 400, 401, 403, 404

Response format: array of messages, see `GET /channels/groups/:group_id/messages`


> PUT/PATCH /channels/groups/:group_id/messages/:message_id<br>
> PUT/PATCH /channels/users/:user_id/messages/:message_id

Edits a message. Can only be performed by the author and only type 0 messages can be updated.

| field | type | description |
|----|----|----|
| contents | string | new contents of the message |

Possible response status code: 200, 400, 401, 403, 404

Response format: The updated message object, see `GET /channels/groups/:group_id/messages`


> DELETE /channels/groups/:group_id/messages/:message_id<br>
> DELETE /channels/users/:user_id/messages/:message_id

Deletes a message. Can be only performed by the author.

Possible response status code: 204, 401, 403, 404

### Reactions

> POST /channels/groups/:group_id/messages/:message_id/reactions<br>
> POST /channels/users/:user_id/messages/:message_id/reactions

Adds a reaction to a message. A message can have up to 10 different reactions

| field | type | description |
|----|----|----|
| reaction | string | reaction name from our emote pack |

Possible response status code: 201, 400, 401, 403, 404

Response format: updated **array** of reactions

| field | type | description |
|----|----|----|
| name | string | emote name |
| count | number | reaction count |


> DELETE /channels/groups/:group_id/messages/:message_id/reactions/:reaction_name<br>
> DELETE /channels/users/:user_id/messages/:message_id/reactions/:reaction_name

Removes your reaction on a message

Possible response status code: 204, 401, 403, 404

Response format: updated **array** of reactions, see `POST /channels/groups/:group_id/messages/:message_id/reactions`
