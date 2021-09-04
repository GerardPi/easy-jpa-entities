
 Check that the id is the significant part that is used for equals and hashCode in entity Address

   Given an entity class "class io.github.gerardpi.easy.jpaentities.test1.domain.addressbook.Address"
         which has a field "id" that is never null
     And which has a field "etag" that is ignored by equals method
     And which has a field "countryCode" that is ignored by equals method
     And which has a field "city" that is ignored by equals method
     And which has a field "postalCode" that is ignored by equals method
     And which has a field "street" that is ignored by equals method
     And which has a field "houseNumber" that is ignored by equals method
    When verifying that entity class
    Then that entity class is ok


 Check that the id is the significant part that is used for equals and hashCode in entity Currency

   Given an entity class "class io.github.gerardpi.easy.jpaentities.test1.domain.webshop.Currency"
         which has a field "id" that is never null
     And which does not possess a field "etag"
     And which has a field "name" that is ignored by equals method
     And which has a field "code" that is ignored by equals method
    When verifying that entity class
    Then that entity class is ok


 Check that the id is the significant part that is used for equals and hashCode in entity Person

   Given an entity class "class io.github.gerardpi.easy.jpaentities.test1.domain.addressbook.Person"
         which has a field "id" that is never null
     And which has a field "etag" that is ignored by equals method
     And which has a field "name" that is ignored by equals method
     And which has a field "dateOfBirth" that is ignored by equals method
    When verifying that entity class
    Then that entity class is ok

