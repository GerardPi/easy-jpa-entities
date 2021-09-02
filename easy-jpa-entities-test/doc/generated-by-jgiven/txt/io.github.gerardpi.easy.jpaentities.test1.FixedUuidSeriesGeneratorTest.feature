
 UUIDs generated are formatted with sequence of integers starting at 0

   When generating 10 UUIDs
   Then the width of the index part is 8 in the zero based series of 10 UUIDs that was created


 UUIDs generated are unique

   When generating 3 UUIDs
   Then UUID 0 matches UUID 0
    And UUID 1 matches UUID 1
    And UUID 2 matches UUID 2
    But UUID 0 does not match UUID 1
        UUID 2 does not match UUID 0
        UUID 1 does not match UUID 0


 UUIDs generated have expected values

   When generating 5 UUIDs
   Then the UUID at index 0 is "00000000-1111-2222-3333-444444444444" when represented as text
   Then the UUID at index 1 is "00000001-1111-2222-3333-444444444444" when represented as text
   Then the UUID at index 2 is "00000002-1111-2222-3333-444444444444" when represented as text
   Then the UUID at index 3 is "00000003-1111-2222-3333-444444444444" when represented as text
   Then the UUID at index 4 is "00000004-1111-2222-3333-444444444444" when represented as text

