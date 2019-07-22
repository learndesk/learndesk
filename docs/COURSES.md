# Courses

## Management 

> POST /courses

Creates a course. Each user can own up to 1000 courses (this limit is here to prevent abuse)

| field | type | description |
|----|----|----|
| name | string | name of the course |
| sequence? | object | course sequence |
| sequence?.number | number | sequence number |
| sequence?.name | string | sequence name |
| chapter? | object | course chapter |
| chapter?.number | number | chapter number |
| chapter?.name | string | chapter name |
| labels | [snowflake](REFERENCE.md#snowflakes)[] | labels of the course |

Possible response status code: 201, 400, 401

Response format: The created course, see `GET /courses/:course_id`


> GET /courses

Gets a list of all courses the user has

Possible response status code: 200, 401

Response format:

| field | type | description |
|----|----|----|
| owned | owned courses[] | see `GET /courses/:course_id` "Owned course" (will not include contents) |
| shared | shared course[] | see `GET /courses/:course_id` "Shared course" (will not include contents) |

> GET /courses/:course_id

Gets a course

Possible response status code: 200, 401, 403, 404

Response format: May vary depending on if the course is an owned course or a shared course.

 - Owned course

| field | type | description |
|----|----|----|
| id | [snowflake](REFERENCE.md#snowflakes) | course id |
| name | string | course name |
| sequence | ?object | course sequence |
| sequence.number | number | sequence number |
| sequence.name | string | sequence name |
| chapter | ?object | course chapter |
| chapter.number | number | chapter number |
| chapter.name | string | chapter name |
| labels | object[] | labels of the course |
| labels[].id | [snowflake](REFERENCE.md#snowflakes) | label id |
| labels[].name | string | label name |
| labels[].color | string | hex color code |
| edited_at | ?ISO8601 timestamp | last edit date |
| files | object | metadata about files |
| files.count | number | file count |
| files.size | number | total files size in bytes |
| owned | true | if you own the course |
| shared_with | number | the number of people who have access to the course |
| contents | TBD | course contents |

 - Shared course

| field | type | description |
|----|----|----|
| id | [snowflake](REFERENCE.md#snowflakes) | course id |
| name | string | course name |
| sequence | ?object | course sequence |
| sequence.number | number | sequence number |
| sequence.name | string | sequence name |
| chapter | ?object | course chapter |
| chapter.number | number | chapter number |
| chapter.name | string | chapter name |
| edited_at | ?ISO8601 timestamp | last time content has been updated |
| files | object | metadata about files |
| files.count | number | file count |
| files.size | number | total files size in bytes |
| owned | false | if you own the course |
| owner.id | [snowflake](REFERENCE.md#snowflakes) | owner's id |
| owner.name | string | owner's display name |
| owner.avatar | string | owner's avatar hash |
| contents | TBD | course contents |


> PUT/PATCH /courses/:course_id

Updates a course. You can only update a course you own

| field | type | description |
|----|----|----|
| name? | string | name of the course |
| sequence? | object | course sequence |
| sequence?.number | number | sequence number |
| sequence?.name | string | sequence name |
| chapter? | object | course chapter |
| chapter?.number | number | chapter number |
| chapter?.name | string | chapter name |
| labels? | [snowflake](REFERENCE.md#snowflakes)[] | labels of the course |
| contents? | TBD | course contents |

Possible response status code: 200, 400, 401, 403, 404

Response format: The updated course, see `GET /courses/:course_id`


> DELETE /courses/:course_id

Deletes a course. Will also delete attached files

Possible response status code: 204, 401, 403, 404

## Attached files

> GET /courses/:course_id/files


> POST /courses/:course_id/files


> PUT/PATCH /courses/:course_id/files/:file_id


> DELETE /courses/:course_id/files/:file_id


## Labels

> GET /courses/labels


> POST /courses/labels


> PUT/PATCH /courses/labels/:label_id


> DELETE /courses/labels/:label_id

<!--
<desc>

| field | type | description |
|----|----|----|

Possible response status code: <codes>

Response format:

| field | type | description |
|----|----|----|
-->
