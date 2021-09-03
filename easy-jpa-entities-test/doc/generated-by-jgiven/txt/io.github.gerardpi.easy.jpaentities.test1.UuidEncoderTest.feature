
 Happy flow convert to base64 and back 1

   When encoding a UUID represented by string "00000001-1111-2222-3333-444444444444"
   Then that ID as a base64 encoded string is "AAAAARERIiIzM0RERERERA"
   When decoding a base64 encoded string "AAAAARERIiIzM0RERERERA" to UUID
   When the resulting UUID is "00000001-1111-2222-3333-444444444444"


 Happy flow convert to base64 and back 2

   When encoding a UUID represented by string "b63f85c6-8331-46d3-b8b7-f64590f99f04"
   Then that ID as a base64 encoded string is "tj-FxoMxRtO4t_ZFkPmfBA"
   When decoding a base64 encoded string "tj-FxoMxRtO4t_ZFkPmfBA" to UUID
   When the resulting UUID is "b63f85c6-8331-46d3-b8b7-f64590f99f04"


 Incomplete base64 string

   When decoding a base64 encoded string "tj-FxoMxRtO4t_ZFkPmfB" to UUID
   When and error occurs and the error message starts with Invalid length.

