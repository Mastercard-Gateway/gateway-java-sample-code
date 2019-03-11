/*
 * Copyright (c) 2018 MasterCard. All rights reserved.
 */


/**
 *    The Hosted Session JavaScript client library enables you to collect sensitive payment details from the payer in
 *    payment form fields, hosted by the Mastercard Payment Gateway. For more information, see {@link https://secure.uat.tnspayments.com/api/documentation/integrationGuidelines/supportedFeatures/pickAdditionalFunctionality/paymentSession.html payment session}
 *    {@link https://secure.uat.tnspayments.com/api/documentation/integrationGuidelines/hostedSession/integrationModelHostedSession.html Implementing a Hosted Session Integration}
 */


// APPLY CLICK-JACKING STYLING AND HIDE CONTENTS OF THE PAGE
if (self === top) {
  var antiClickjack = document.getElementById("antiClickjack");
  if (antiClickjack) antiClickjack.parentNode.removeChild(antiClickjack);
} else {
  top.location = self.location;
}

const scope = $(".mb-4")[0].id;

var afterSessionUpdated;

// CONFIGURES A HOSTED SESSION INTERACTION
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
          console.log("Session initialized for scope: " + response.scopeId);
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

// SETS FOCUS ON THE SPECIFIED HOSTED FIELD
PaymentSession.setFocus('card.number', scope);

// SETS THE STYLING ATTRIBUTES FOR THE SPECIFIED HOSTED FIELDS WHEN THE FOCUS IS GAINED
PaymentSession.setFocusStyle(["card.number", "card.securityCode"], {
  borderColor: 'red',
  borderWidth: '3px'
}, scope);

// 1.1
function updateSession(callback) {
  $("#loading-bar-spinner").show();
  // UPDATE CALLBACK FUNCTION THAT WILL BE CALLED ONCE THE SESSION HAS BEEN UPDATED
  afterSessionUpdated = callback;

  // STORES THE INPUT FROM THE HOSTED FIELD INTO THE SESSION
  // USAGE PaymentSession.updateSessionFromForm(paymentType, [localBrand, [scope]])
  PaymentSession.updateSessionFromForm('card', null, scope);
}

function handleError(message) {
  $("#loading-bar-spinner").hide();
  var $errorAlert = $('#error-alert');
  console.log(message);
  $errorAlert.append("<p>" + message + "</p>");
  $errorAlert.show();
}