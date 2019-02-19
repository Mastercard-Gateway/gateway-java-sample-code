/*
 * Copyright (c) 2018 MasterCard. All rights reserved.
 */

if (self === top) {
  var antiClickjack = document.getElementById("antiClickjack");
  if (antiClickjack) antiClickjack.parentNode.removeChild(antiClickjack);
} else {
  top.location = self.location;
}

const scope = $(".mb-4")[0].id;

var afterSessionUpdated;
PaymentSession.configure({
  session: $("#session-id")[0].value,
  fields: {
    // ATTACH HOSTED FIELDS TO YOUR PAYMENT PAGE FOR A CREDIT CARD
    card: {
      number: "#card-number",
      securityCode: "#security-code",
      expiryMonth: "#expiry-month",
      expiryYear: "#expiry-year"
    }
  },
  //SPECIFY YOUR MITIGATION OPTION HERE
  frameEmbeddingMitigation: ["javascript"],
  callbacks: {
    initialized: function (response) {
      if (response.status) {
        if ("ok" == response.status) {
          console.log("Session updated with data: " + response.session.id);
        }
      }
    },
    formSessionUpdate: function (response) {
      // HANDLE RESPONSE FOR UPDATE SESSION
      if (response.status) {
        if ("ok" == response.status) {
          console.log("Session updated with data: " + response.session.id);
          afterSessionUpdated();
        } else if ("fields_in_error" == response.status) {

          console.log("Session update failed with field errors.");

          if (response.errors.cardNumber) {
            handleError("Card number missing or invalid.");
          }
          if (response.errors.expiryYear) {
            handleError("Expiry year missing or invalid.");
          }
          if (response.errors.expiryMonth) {
            handleError("Expiry month missing or invalid.");
          }
          if (response.errors.securityCode) {
            handleError("Security code invalid.");
          }
        } else if ("request_timeout" == response.status) {
          handleError("Session update failed with request timeout: " + response.errors.message);
        } else if ("system_error" == response.status) {
          handleError("Session update failed with system error: " + response.errors.message)
        }
      } else {
        handleError("Session update failed: " + response);
      }
    }
  }
}, scope);

PaymentSession.setFocus('card.number', scope);

PaymentSession.setFocusStyle(["card.number", "card.securityCode"], {
  borderColor: 'red',
  borderWidth: '3px'
}, scope);

// 1.1
function updateSession(callback) {
  $("#loading-bar-spinner").show();
  // UPDATE THE SESSION WITH THE INPUT FROM HOSTED FIELDS
  afterSessionUpdated = callback;
  PaymentSession.updateSessionFromForm('card', '', scope);
}

function handleError(message) {
  $("#loading-bar-spinner").hide();
  var $errorAlert = $('#error-alert');
  console.log(message);
  $errorAlert.append("<p>" + message + "</p>");
  $errorAlert.show();
}