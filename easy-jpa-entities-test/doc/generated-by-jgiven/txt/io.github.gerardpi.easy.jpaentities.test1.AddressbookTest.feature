
 Optimistic locking version number increases with updates

   When person "1" is created with first name "Frits" and last name "Jansma" in the database "2001-11-23"
   Then that class io.github.gerardpi.easy.jpaentities.test1.domain.Person 1 has ID "00000000-1111-2222-3333-444444444444"
   Then that class io.github.gerardpi.easy.jpaentities.test1.domain.Person with number 1 has optimistic locking version number "0"
   When updating a person 1 with first name "Klaas"
    And updating a person 1 with date of birth 1985-01-01
   Then that class io.github.gerardpi.easy.jpaentities.test1.domain.Person with number 1 has optimistic locking version number "2"
    And the person with key 1 has date of birth 1985-01-01
   When updating a person 1 with first name "Piet"
   Then that class io.github.gerardpi.easy.jpaentities.test1.domain.Person with number 1 has optimistic locking version number "3"
   When creating an address 1 with data "NL" "Amsterdam" "1234AA" "Damstraat" "1"
   Then that class io.github.gerardpi.easy.jpaentities.test1.domain.Address 1 has ID "00000001-1111-2222-3333-444444444444"
    And that class io.github.gerardpi.easy.jpaentities.test1.domain.Address with number 1 has optimistic locking version number "0"
   When updating an address 1 with "postalCode" "1234AB"
   Then that class io.github.gerardpi.easy.jpaentities.test1.domain.Address with number 1 has optimistic locking version number "1"


 Person address can be used to link a person to an address

   Given person "1" is created with first name "Frits" and last name "Jansma" in the database "2001-11-27"
     And creating an address 1 with data "NL" "Amsterdam" "1234AA" "Damstraat" "1"
    When a relation is created "1" between person "1" and address "1" with types "[RESIDENCE, PROPERTY]"
    Then the person "1" can be found via address "1" using postal code "NL" and house number "1234AA" "1"

