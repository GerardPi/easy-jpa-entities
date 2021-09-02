
 Get person not found

   Given the the current date and time is 2021-09-01T21:11:28+02:00
    When an HTTP GET on /api/persons/ with the id for entity with id 00000200-1111-2222-3333-444444444444 is performed
    Then the HTTP status code is "404 NOT_FOUND"
     And the response contains body equals 
          ----------------------------------------------------------------------
          {
                    "traceId": "1630523488000",
                    "path": "\/api\/persons\/00000200-1111-2222-3333-444444444444",
                    "method": "GET",
                    "statusName": "Not Found",
                    "statusSeries": "CLIENT_ERROR",
                    "messages": [
                              "No 'Person' for ID '00000200-1111-2222-3333-444444444444' exists."
                    ],
                    "title": "item was not found",
                    "statusCode": 404,
                    "timestamp": "2021-09-01T21:11:28+02:00"
          }
          ----------------------------------------------------------------------



 Get persons

   Given person "1" is stored in the database with first name "Frits" and last name "Jansma" and date of birth "2001-11-23" in the database
     And person "2" is stored in the database with first name "Albert" and last name "Fles" and date of birth "2002-11-24" in the database
    When an HTTP "GET" on "/api/persons" is performed
    Then the HTTP status code is "200 OK"
     And the number of items received is 2
    When an HTTP GET on /api/persons/ with the id for entity 1 is performed
    Then the response contains body equals 
          ----------------------------------------------------------------------
          {
                    "name": {
                              "last": "Jansma",
                              "first": "Frits"
                    },
                    "etag": "0",
                    "dateOfBirth": "2001-11-23",
                    "id": "00000000-1111-2222-3333-444444444444"
          }
          ----------------------------------------------------------------------

     And in the response "id" is equal to "00000000-1111-2222-3333-444444444444"
     And in the response "etag" is equal to "0"
     And in the response "name.first" is equal to "Frits"
     And in the response "name.last" is equal to "Jansma"
     And in the response "dateOfBirth" is equal to "2001-11-23"
     And the HTTP status code is "200 OK"
     And no exception was thrown
    When an HTTP GET on /api/persons/ with the id for entity 2 is performed
    Then the response contains body equals 
          ----------------------------------------------------------------------
          {
                    "name": {
                              "last": "Fles",
                              "first": "Albert"
                    },
                    "etag": "0",
                    "dateOfBirth": "2002-11-24",
                    "id": "00000001-1111-2222-3333-444444444444"
          }
          ----------------------------------------------------------------------

     And in the response "id" is equal to "00000001-1111-2222-3333-444444444444"
     And in the response "etag" is equal to "0"
     And in the response "name.first" is equal to "Albert"
     And in the response "name.last" is equal to "Fles"
     And in the response "dateOfBirth" is equal to "2002-11-24"
     And the HTTP status code is "200 OK"
     And no exception was thrown


 Post person

   When an HTTP "POST" on "/api/persons" is performed with body 
          ----------------------------------------------------------------------
          {
                    "name": {
                              "last": "last",
                              "first": "first"
                    },
                    "dateOfBirth": "1998-10-22"
          }
          ----------------------------------------------------------------------

   Then the HTTP status code is "200 OK"
    And no exception was thrown

