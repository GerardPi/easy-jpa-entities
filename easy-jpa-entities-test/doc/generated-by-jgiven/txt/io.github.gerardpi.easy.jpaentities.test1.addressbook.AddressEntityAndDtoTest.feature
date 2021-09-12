
 Dto to entity copies all fields from dto regardless

   Given an address 
          ----------------------------------------------------------------------
          {
            "id" : "00000000-1111-2222-3333-444444444444",
            "etag" : null,
            "isModified" : true,
            "countryCode" : "NL",
            "city" : "Amsterdam",
            "postalCode" : "1234AB",
            "street" : "this street",
            "houseNumber" : "1a"
          }
          ----------------------------------------------------------------------

    When a DTO 
          ----------------------------------------------------------------------
          {
            "id" : "00000000-1111-2222-3333-444444444444",
            "etag" : "null",
            "countryCode" : null,
            "city" : null,
            "postalCode" : "1234AB",
            "street" : "different street",
            "houseNumber" : "1a"
          }
          ----------------------------------------------------------------------
 is used to modify that address entity copying values from all DTO fields
    Then the resulting address entity is 
          ----------------------------------------------------------------------
          {
            "id" : "00000000-1111-2222-3333-444444444444",
            "etag" : null,
            "isModified" : true,
            "countryCode" : null,
            "city" : null,
            "postalCode" : "1234AB",
            "street" : "different street",
            "houseNumber" : "1a"
          }
          ----------------------------------------------------------------------
 [Note that the etag is always null since this entity was never stored; and the isModified values is always true since the entity was never stored.]


 Dto to entity not null copies not null fields from dto

   Given an address 
          ----------------------------------------------------------------------
          {
            "id" : "00000000-1111-2222-3333-444444444444",
            "etag" : null,
            "isModified" : true,
            "countryCode" : "NL",
            "city" : "Amsterdam",
            "postalCode" : "1234AB",
            "street" : "this street",
            "houseNumber" : "1a"
          }
          ----------------------------------------------------------------------

    When a DTO 
          ----------------------------------------------------------------------
          {
            "id" : "00000000-1111-2222-3333-444444444444",
            "etag" : "null",
            "countryCode" : null,
            "city" : null,
            "postalCode" : "1234AB",
            "street" : "different street",
            "houseNumber" : "1a"
          }
          ----------------------------------------------------------------------
 is used to modify that address entity copying values from DTO fields that are not null
    Then the resulting address entity is 
          ----------------------------------------------------------------------
          {
            "id" : "00000000-1111-2222-3333-444444444444",
            "etag" : null,
            "isModified" : true,
            "countryCode" : "NL",
            "city" : "Amsterdam",
            "postalCode" : "1234AB",
            "street" : "different street",
            "houseNumber" : "1a"
          }
          ----------------------------------------------------------------------
 [Note that the etag is always null since this entity was never stored; and the isModified values is always true since the entity was never stored.]

