
 The database contains some currencies

   When a currency with code "EUR" and name "Euro" is stored in the database
   Then that currency "Euro" can be fetched from the database using the currency code "EUR"


 The optimistic locking value is increased with each update and the is modified flag work as expected

   Given an item 1 with name is stored in the database "kaas"
    When fetching that item 1 from the database
    Then that item is not modified
     And the name of the item is "kaas"
     And the optimistic locking version value of the item is 0
    When creating a builder for modification
     And the name in the builder for the item is changed into "fromage"
     And building an Item from the builder
    Then the built item indicates that it is modified
    When the newly built item 2 is stored into the database
     And fetching that item 2 from the database
    Then that item is not modified
     And the optimistic locking version value of the item is 1
     And the name of the item is "fromage"

