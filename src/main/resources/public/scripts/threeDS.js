if (self === top) {
  var antiClickjack = document.getElementById("antiClickjack");
  if (antiClickjack) antiClickjack.parentNode.removeChild(antiClickjack);
} else {
  top.location = self.location;
}

// THREEDS.configure({
//   sessionId: JavaSample.sessionId,
//   merchantId: JavaSample.merchantId,
//   hostedSessionId: JavaSample.sessionId,
//   containerId: '3DSUI',
//   configuration: {userLanguage: "en-AU", wsVersion: 50},
//   callback: function (data) {
//     if(THREEDS.configured)
//       console.log("Done with configure");
//   }
// });
//
// function pay() {
//   // UPDATE THE SESSION WITH THE INPUT FROM HOSTED FIELDS
//   THREEDS.initiate3DS(JavaSample.cardNumber(), JavaSample.orderId, JavaSample.transactionId());
// }
