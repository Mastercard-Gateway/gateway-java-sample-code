/*
 * Copyright (c) 2019 MasterCard. All rights reserved.
 */

var finalSubmit = expiryMonth = expiryYear = cardNumber = securityCode = nameOnCard = false;

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

PaymentSession.onBlur(['card.number'], function(selector) {
    console.log("Blur event executed for " + selector);
    finalSubmit = false;
    cardNumber = true;
    currentFocusField = null;
    PaymentSession.updateSessionFromForm('card');
});

PaymentSession.onBlur(['card.nameOnCard'], function(selector) {
    console.log("Blur event executed for " + selector);
    $("label[for='card-holder-name']").css({fontWeight:"normal"});
});

PaymentSession.onBlur(['card.expiryMonth'], function(selector) {
    console.log("Blur event executed for " + selector);
	finalSubmit = false;
	expiryMonth = true;
    $("label[for='expiry-month']").css({fontWeight:"normal"});
    currentFocusField = null;
	PaymentSession.updateSessionFromForm('card');
});

PaymentSession.onBlur(['card.expiryYear'], function(selector) {
    console.log("Blur event executed for " + selector);
	finalSubmit = false;
	expiryYear = true;
    $("label[for='expiry-year']").css({fontWeight:"normal"});
    currentFocusField = null;
	PaymentSession.updateSessionFromForm('card');
});

PaymentSession.onBlur(['card.securityCode'], function(selector) {
    console.log("Blur event executed for " + selector);
    finalSubmit = false;
    securityCode = true;
    currentFocusField = null;
    PaymentSession.updateSessionFromForm('card');
});

PaymentSession.onFocus(['card.nameOnCard','card.number','card.expiryMonth','card.expiryYear' ,'card.securityCode'], function(selector) {
    if (selector === "#card.nameOnCard") {
        $("label[for='card-holder-name']").css({fontWeight:"bold"});
    }
    console.log("Focus event executed for " + selector);
    for (var sel in selectors) {
        if (selectors[sel] === selector) {
            setOnFocusField(sel);
            currentFocusField = sel;
            clearErrorMessageOnFocus(sel);
        }
    }

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

PaymentSession.setFocusStyle(["card.nameOnCard","card.number","card.expiryMonth","card.expiryYear","card.securityCode"], {
    borderColor: 'red',
    borderWidth: '3px',
    borderStyle:'solid'
});

PaymentSession.setHoverStyle(["card.nameOnCard","card.number","card.expiryMonth","card.expiryYear","card.securityCode"], {
    borderColor: 'blue',
    borderWidth: '3px',
    borderStyle:'solid'
});

function handleError(message, selectorField) {
    $("#loading-bar-spinner").hide();
    if ((selectorField && focusFields[selectorField].focus && currentFocusField !== selectorField) || finalSubmit === true) {
        $(selectors[selectorField]).next().tooltip({
            title: message,
            trigger: 'manual',
            placement: 'right',
            html: true,
            template: [
                '<div class="tooltip hs-validation-tooltip" role="tooltip">',
                    '<div class="arrow"></div>',
                    '<div class="tooltip-inner"></div>',
                '</div>'
            ].join(''),
            animation: true
        });
        $(selectors[selectorField]).next().tooltip('show');
    } else if (selectorField && focusFields[selectorField].focus && currentFocusField === selectorField) {
        // clear particular error message
        $(selectors[selectorField]).next().tooltip('hide');
    }
}

function clearErrorMessageOnFocus(focusField){
    $(selectors[focusField]).next().tooltip('hide');
}