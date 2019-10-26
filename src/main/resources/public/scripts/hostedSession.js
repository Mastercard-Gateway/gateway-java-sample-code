/*
 * Copyright (c) 2019 MasterCard. All rights reserved.
 */

/**
 *    The Hosted Session JavaScript client library enables you to collect sensitive payment details from the payer in
 *    payment form fields, hosted by the Mastercard Payment Gateway. For more information, see {@link https://test-gateway.mastercard.com/api/documentation/integrationGuidelines/supportedFeatures/pickAdditionalFunctionality/paymentSession.html payment session}
 *    See https://test-gateway.mastercard.com/api/documentation/integrationGuidelines/hostedSession/integrationModelHostedSession.html Implementing a Hosted Session Integration
 */

// APPLY CLICK-JACKING STYLING AND HIDE CONTENTS OF THE PAGE
if (self === top) {
    var antiClickjack = document.getElementById("antiClickjack");
    if (antiClickjack) antiClickjack.parentNode.removeChild(antiClickjack);
} else {
    top.location = self.location;
}

$("#loading-bar-spinner").show();

// HOLD THE CALLBACK FUNCTION THAT WILL BE CALLED AFTER THE HOSTED FIELDS IN THE SESSION HAVE BEEN UPDATED
// See pay(callback)
var afterSessionUpdated;
var sessionId = (!$("#session-id")[0]) ? "" : $("#session-id")[0].value;

var cardHolderNameField = document.getElementById('card-holder-name');

PaymentSession.configure({
    session: sessionId,
    fields: {
        card: {
            // ATTACH HOSTED FIELDS TO YOUR PAYMENT PAGE FOR A CREDIT CARD
            nameOnCard: cardHolderNameField ? "#card-holder-name" : null,
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
            $("#loading-bar-spinner").hide();
            if ("ok" == response.status) {
                console.log("Payment Session initialized");
            }
            // cardHolderNameField ?
            //     PaymentSession.setFocus('card.nameOnCard') : PaymentSession.setFocus('card.number');
        },
        formSessionUpdate: function (response) {
            // HANDLE RESPONSE FOR UPDATE SESSION
            if (response.status) {
                clearErrorMessages();
                if ("ok" == response.status && finalSubmit == true) {
                    console.log("Session updated with data: " + response.session.id);

                    if (!afterSessionUpdated) {
                        submitFields(response.session.id);
                    } else {
                        afterSessionUpdated();
                    }
                } else if ("fields_in_error" == response.status) {

                    console.log("Session update failed with field errors.");

                    if (response.errors.cardNumber) {
                        handleError("Card number missing or invalid.",'cardNumber');
                    }
                    if (response.errors.expiryYear) {
                        handleError("Expiry year missing or invalid.",'expiryYear');
                    }
                    if (response.errors.expiryMonth) {
                        handleError("Expiry month missing or invalid.",'expiryMonth');
                    }
                    if (response.errors.securityCode) {
                        handleError("Security code invalid.",'securityCode');
                    }
                } else if ("request_timeout" == response.status) {
                    handleError("Session update failed with request timeout: " + response.errors.message);
                } else if ("system_error" == response.status) {
                    handleError("Session update failed with system error: " + response.errors.message)
                }
                finalSubmit = false;
            } else {
                handleError("Session update failed: " + response);
                finalSubmit = false;
            }
        }
    }
});

function isMobileBrowser() {
    return navigator.userAgent.indexOf('Mobile') >= 0 || navigator.userAgent.indexOf('Android') >= 0;
}

function pay(callback) {
    if(isMobileBrowser()){
        setTimeout(payClick,200,callback);
    }else{
        payClick(callback);
    }
}

function payClick(callback){
    $("#loading-bar-spinner").show();
    finalSubmit = true;
    expiryMonth = expiryYear = cardNumber = securityCode = false;

    // UPDATE CALLBACK FUNCTION THAT WILL BE CALLED ONCE THE SESSION HAS BEEN UPDATED
    if (callback)
        afterSessionUpdated = callback;

    // UPDATE THE SESSION WITH THE INPUT FROM HOSTED FIELDS
    PaymentSession.updateSessionFromForm('card');
}

function submitFields(sessionId) {
    var data = {
        apiOperation: JavaSample.operation(),
        sessionId: sessionId,
        transactionId: $('#transaction-id').val(),
        orderId: $('#order-id').val(),
        orderAmount: $('#order-amount').val(),
        orderCurrency: $('#order-currency').val(),
        orderDescription: $('#order-description').val(),
        secureIdResponseUrl: JavaSample.secureIdResponseUrl()
    };

    var xhr = new XMLHttpRequest();
    xhr.open('POST', JavaSample.endpoint(), true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onreadystatechange = function () {
        if (xhr.readyState == XMLHttpRequest.DONE) {
            document.documentElement.innerHTML = this.response;
        }
    };
    xhr.send(JSON.stringify(data));
}

function handleError(message) {
    $("#loading-bar-spinner").hide();
    var $errorAlert = $('#error-alert');
    console.log(message);
    $errorAlert.append("<p>" + message + "</p>");
    $errorAlert.show();
}

function clearErrorMessages(){
    var $errorAlert = $('#error-alert');
    $errorAlert.html("");
    $errorAlert.hide();
}
