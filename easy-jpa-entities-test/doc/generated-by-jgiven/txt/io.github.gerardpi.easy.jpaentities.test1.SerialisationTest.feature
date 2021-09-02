
 Test from json

   When JSON 
          ----------------------------------------------------------------------
          {
                    "name": {
                              "last": "Fritz",
                              "first": "Kees"
                    },
                    "dateOfBirth": "1998-10-22"
          }
          ----------------------------------------------------------------------
 is deserialized into a class io.github.gerardpi.easy.jpaentities.test1.web.PersonDto
   Then that PersonDto has first name "Kees" and last name "Fritz" and date of birth "1998-10-22"


 Test to json

   Given a PersonDto with first name "Frits" and last name "Jansma" and date of birth "2001-11-21"
    When that PersonDto is serialized to JSON
    Then the result JSON is 
          ----------------------------------------------------------------------
          {
                    "name": {
                              "last": "Jansma",
                              "first": "Frits"
                    },
                    "dateOfBirth": "2001-11-21"
          }
          ----------------------------------------------------------------------


