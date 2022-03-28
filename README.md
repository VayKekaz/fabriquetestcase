# How to start

```shell
docker build -t fabriquetestcase .
docker run -p 8080:8080 fabriquetestcase
```

Or with gradle cli (requires `JAVA_HOME` version 16+)

```shell
gradlew bootRun
```

# Endpoints

I've created postman collection for tests (`.\fabriquetestcase.postman_collection.json`).

### POST /login

Returns JWT for user for further authentication with bearer token. Token should be included into
header `Authorization: "Bearer <token>"`

Request body (correct admin user credentials):

```json
{
  "username": "adminUser11",
  "password": "adminPass"
}
```

Response example:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoiYWRtaW5Vc2VyMTEiLCJpc3MiOiJiYWNrZW5kLmZhYnJpcXVlLnN0dWRpbyIsImlhdCI6MTY0ODQ0MzA0Nn0.LW9baSww_Of9ya57Vt6ti67XSSh1C8kVkedvC8VCym8"
}
```

### GET /surveys[?pageNumber=0&pageSize=10][/{id}]

Accessible anonymously. Returns all surveys paged, or single survey.

Response body example:

```json
{
  "content": [
    {
      "id": 2,
      "title": "do you hate niggers",
      "startsOn": "2022-01-01T00:00:00Z",
      "closesOn": null,
      "questions": [
        {
          "type": "CHOOSE_ONE",
          "theQuestion": "do you love black color?",
          "options": [
            "yes",
            "no"
          ]
        }
      ]
    }
  ],
  "pageable": {
    "sort": [],
    "offset": 0,
    "pageNumber": 0,
    "pageSize": 10,
    "unpaged": false,
    "paged": true
  },
  "totalPages": 1,
  "totalElements": 3,
  "last": true,
  "size": 10,
  "number": 0,
  "sort": [],
  "numberOfElements": 3,
  "first": true,
  "empty": false
}
```

### POST /surveys

Accepts survey and saves it into database. Returns saved survey. Request body example:

```json
{
  "title": "new test survey",
  "startsOn": "2022-03-28T03:13:45Z",
  "questions": [
    {
      "type": "CHOOSE_ONE",
      "theQuestion": "choose one test",
      "options": [
        "yes",
        "no"
      ]
    }
  ]
}
```

### PUT /surveys/{id}

Accepts survey and updates it into database. Returns updated survey. Request body example:

```json
{
  "title": "new test survey",
  "questions": [
    {
      "type": "CHOOSE_ONE",
      "theQuestion": "choose one test",
      "options": [
        "yes",
        "no"
      ]
    },
    {
      "type": "CHOOSE_MULTIPLE",
      "theQuestion": "choose multiple test",
      "options": [
        "option one",
        "option two",
        "third option"
      ]
    }
  ]
}
```

### POST /survey/completions/{id}?userId

Requires admin authentication.<br/>
Accepts array of chosen options (`Int` for `CHOOSE_ONE` question; `Array<Int>` for `CHOOSE_MULTIPLE`; `String`
for `WRITE_ANSWER`). Returns `CompletedSurvey` object.

Request body example:

```json
[
  1,
  0,
  "i absolutely hate them",
  [
    1,
    3
  ]
]
```

Response body example:

```json
{
  "id": 7,
  "userId": 2,
  "survey": {
    "id": 2,
    "title": "do you hate niggers",
    "startsOn": "2022-01-01T00:00:00Z",
    "closesOn": null,
    "questions": [
      {
        "type": "CHOOSE_ONE",
        "theQuestion": "do you love black color?",
        "options": [
          "yes",
          "no"
        ]
      },
      {
        "type": "CHOOSE_ONE",
        "theQuestion": "do you hate black people?",
        "options": [
          "yes",
          "no"
        ]
      },
      {
        "type": "WRITE_ANSWER",
        "theQuestion": "describe how you hate niggers"
      },
      {
        "type": "CHOOSE_MULTIPLE",
        "theQuestion": "choose reasons to hate niggers",
        "options": [
          "they're black",
          "they smell like pigs",
          "they smell like coal",
          "they are stupid"
        ]
      }
    ]
  },
  "answers": [
    1,
    0,
    "i absolutely hate them",
    [
      1,
      3
    ]
  ]
}
```

### GET /surveys/completions[?id][?userId][?pageNumber=0&pageSize=10]

Returns page of survey completions [for specific completion] [for specific user]. Request
example: `http://localhost:8080/surveys/completions?userId=2` <br/>
Response body example:

```json
{
  "content": [
    {
      "id": 7,
      "userId": 2,
      "survey": {
        "id": 2,
        "title": "do you hate niggers",
        "startsOn": "2022-01-01T00:00:00Z",
        "closesOn": null,
        "questions": [
          {
            "type": "CHOOSE_ONE",
            "theQuestion": "do you love black color?",
            "options": [
              "yes",
              "no"
            ]
          },
          {
            "type": "CHOOSE_ONE",
            "theQuestion": "do you hate black people?",
            "options": [
              "yes",
              "no"
            ]
          },
          {
            "type": "WRITE_ANSWER",
            "theQuestion": "describe how you hate niggers"
          },
          {
            "type": "CHOOSE_MULTIPLE",
            "theQuestion": "choose reasons to hate niggers",
            "options": [
              "they're black",
              "they smell like pigs",
              "they smell like coal",
              "they are stupid"
            ]
          }
        ]
      },
      "answers": [
        1,
        0,
        "i absolutely hate them",
        [
          1,
          3
        ]
      ]
    }
  ]
  ...
}
```
