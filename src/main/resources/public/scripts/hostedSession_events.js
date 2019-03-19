/*
 * Copyright (c) 2019 MasterCard. All rights reserved.
 */

var finalSubmit = expiryMonth = expiryYear = cardNumber = securityCode = false;


PaymentSession.onBlur(['card.number'], function(selector)
{
    console.log("Blur event executed for " + selector);
    finalSubmit = false;
    cardNumber = true;
    PaymentSession.updateSessionFromForm('card');
});

PaymentSession.onBlur(['card.expiryMonth'], function(selector)
{
    console.log("Blur event executed for " + selector);
	finalSubmit = false;
	expiryMonth = true;
    $("label[for='expiry-month']").css({fontWeight:"normal"});
	PaymentSession.updateSessionFromForm('card');
});

PaymentSession.onBlur(['card.expiryYear'], function(selector)
{
    console.log("Blur event executed for " + selector);
	finalSubmit = false;
	expiryYear = true;
    $("label[for='expiry-year']").css({fontWeight:"normal"});
	PaymentSession.updateSessionFromForm('card');
});

PaymentSession.onBlur(['card.securityCode'], function(selector)
{
    console.log("Blur event executed for " + selector);
    finalSubmit = false;
    securityCode = true;
    PaymentSession.updateSessionFromForm('card');
});


PaymentSession.onFocus(['card.expiryYear'], function(selector) {
    console.log(selector);
    console.log("Focus event executed for " + selector);
    $("label[for='expiry-year']").css({fontWeight:"bold"});

});

PaymentSession.onChange(['card.expiryMonth'], function(selector) {
    console.log("Change event executed for " + selector);
    $("label[for='expiry-month']").css({fontWeight:"bold"});
});

PaymentSession.onMouseOver(['card.number'], function(selector) {
    console.log("MouseOver event executed for " + selector);
    $("label[for='card-number']").css({fontWeight:"bold"});
});

PaymentSession.onMouseOut(['card.number'], function(selector) {
    console.log("MouseOut event executed for " + selector);
    $("label[for='card-number']").css({fontWeight:"normal"})
});

PaymentSession.setFocus('card.number');

PaymentSession.setFocusStyle(["card.number","card.expiryMonth","card.expiryYear","card.securityCode"], {
    borderColor: 'red',
    borderWidth: '3px',
    borderStyle:'solid',
},scope);

PaymentSession.setHoverStyle(["card.number","card.expiryMonth","card.expiryYear","card.securityCode"], {
    borderColor: 'blue',
    borderWidth: '3px',
    borderStyle:'solid'
});