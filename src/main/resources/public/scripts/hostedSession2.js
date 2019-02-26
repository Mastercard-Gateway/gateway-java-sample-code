/*
 * Copyright (c) 2018 MasterCard. All rights reserved.
 */

if (self === top) {
    var antiClickjack = document.getElementById("antiClickjack");
    if (antiClickjack) antiClickjack.parentNode.removeChild(antiClickjack);
} else {
    top.location = self.location;
}

PaymentSession.configure({
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
            // HANDLE INITIALIZATION RESPONSE
        },
        formSessionUpdate: function (response) {
            // HANDLE RESPONSE FOR UPDATE SESSION
            if (response.status) {
                clearErrorMessages();
                if ("ok" == response.status && finalSubmit == true) {
                    console.log("Session updated with data: " + response.session.id);

                    // Submit fields
                    var data = {
                        apiOperation: JavaSample.operation(),
                        sessionId: response.session.id,
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
                    xhr.onreadystatechange = function() {
                        if (xhr.readyState == XMLHttpRequest.DONE) {
                            document.documentElement.innerHTML =this.response;
                        }
                    };
                    xhr.send(JSON.stringify(data));

                } else if ("fields_in_error" == response.status) {

                    console.log("Session update failed with field errors.");

                    if (response.errors.cardNumber && focusFields.cardNumber.focus && currentFocusField !== 'cardNumber' ) {
                        handleError("Card number missing or invalid.");
                    }
                    if (response.errors.expiryYear && focusFields.expiryYear.focus && currentFocusField !== 'expiryYear' ) {
                        handleError("Expiry year missing or invalid.");
                    }
                    if (response.errors.expiryMonth && focusFields.expiryMonth.focus && currentFocusField !== 'expiryMonth' ) {
                        handleError("Expiry month missing or invalid.");
                    }
                    if (response.errors.securityCode && focusFields.securityCode.focus && currentFocusField !== 'securityCode' ) {
                        handleError("Security code invalid.");
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


var finalSubmit = expiryMonth = expiryYear = cardNumber = securityCode = false;

var focusFields={
    expiryMonth:{},
    expiryYear:{},
    cardNumber:{},
    securityCode:{}
};

var selectors =  {
        cardNumber: "#card-number",
        securityCode: "#security-code",
        expiryMonth: "#expiry-month",
        expiryYear: "#expiry-year"
};

var currentFocusField = null;

setOnFocusField = function(fieldname){
    focusFields[fieldname].focus = true;
};

PaymentSession.onBlur(['card.number'], function(selector)
{
    finalSubmit = false;
    cardNumber = true;
    PaymentSession.updateSessionFromForm('card');
});

PaymentSession.onBlur(['card.expiryMonth'], function(selector)
{
	finalSubmit = false;
	expiryMonth = true;
    $("label[for='expiry-month']").css({fontWeight:"normal"});
	PaymentSession.updateSessionFromForm('card');
});

PaymentSession.onBlur(['card.expiryYear'], function(selector)
{
	finalSubmit = false;
	expiryYear = true;
	PaymentSession.updateSessionFromForm('card');
});

PaymentSession.onBlur(['card.securityCode'], function(selector)
{
    finalSubmit = false;
    securityCode = true;
    PaymentSession.updateSessionFromForm('card');
});


PaymentSession.onFocus(['card.number','card.expiryMonth','card.expiryYear' ,'card.securityCode'], function(selector) {
    //handle focus event
    console.log(selector);

    for(sel in selectors){
        if(selectors[sel] === selector){
            setOnFocusField(sel);
            currentFocusField = sel;
        }
    };

});

PaymentSession.onChange(['card.expiryMonth'], function(selector) {
    //handle change event
    $("label[for='expiry-month']").css({fontWeight:"bold"});
});

PaymentSession.onMouseOver(['card.number'], function(selector) {
    //handle mouse over event
    $("label[for='card-number']").css({fontWeight:"bold"});
});

PaymentSession.onMouseOut(['card.number'], function(selector) {
    //handle mouse out event
    $("label[for='card-number']").css({fontWeight:"normal"})
});


PaymentSession.setFocus('card.number');

PaymentSession.setFocusStyle(["card.number","card.expiryMonth","card.expiryYear","card.securityCode"], {
    borderColor: 'red',
    borderWidth: '3px',
    borderStyle:'solid',
});

PaymentSession.setHoverStyle(["card.number","card.expiryMonth","card.expiryYear","card.securityCode"], {
    borderColor: 'blue',
    borderWidth: '3px',
    borderStyle:'solid'
});


function pay() {
    $("#loading-bar-spinner").show();
    finalSubmit = true;
    expiryMonth = expiryYear = cardNumber = securityCode = false;
    // UPDATE THE SESSION WITH THE INPUT FROM HOSTED FIELDS
    PaymentSession.updateSessionFromForm('card');
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