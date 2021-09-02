
 The database contains orders for a person

   Given a person "1" with first name "A" and last name "B"
         an item 1 with name "kaas"
    When an order 1 with date and time "2021-05-10T18:15:33.901351+02:00" is stored for person 1
     And that order 1 contains 1 pieces of 1 which cost "10.12" a piece
     And an order 2 with date and time "2021-05-10T19:40:02.901351+02:00" is stored for person 1
     And that order 2 contains 1 pieces of 2 which cost "12.73" a piece
    Then person 1 has 2 orders with a total amount of "35.58"
     And the order 1 has date and time "2021-05-10T18:15:33.901351+02:00"
     And the order 2 has date and time "2021-05-10T19:40:02.901351+02:00"

