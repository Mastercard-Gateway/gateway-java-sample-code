var WCAGScenarios = {
    authenticationCardEntryFields: {
        JAWS: {
            chrome: [1, 2, 4, 5, 8, 10],
            ie11: [1, 2, 4, 7, 8, 10, 11, 12],
            firefox: [1, 2, 4, 8]
        },
        NVDA: {
            chrome: [1, 2, 4, 5, 7, 8, 9],
            ie11: [1, 2, 4, 5, 7, 8, 9]
        },
        SuperNova: {
            chrome: [19, 20, 21, 22, 23, 30],
            ie11: [21]
        }
    },
    amexCheckout: {
        JAWS: {
            chrome: [2, 4, 6, 10, 12, 16, 21],
            ie11: [2, 4, 6, 10, 12],
            firefox: [2, 4, 6, 10, 12, 15, 21]
        },
        NVDA: {
            chrome: [2, 3, 4, 6, 9],
            ie11: [2, 33]
        },
        SuperNova: {
            chrome: [6, 19, 20]
        }
    },
    amexPaymentPlans: {
        JAWS: {
            chrome: [2, 6, 10, 16, 22],
            ie11: [2, 6, 10],
            firefox: [4, 5, 6, 10, 15, 21, 22]
        },
        NVDA: {
            chrome: [2, 3, 4, 5, 6, 9, 15],
            ie11: [31]
        },
        SuperNova: {
            chrome: [1, 6, 19, 20, 21, 22, 23],
            ie11: [21, 22, 23],
            firefox: [10]
        }
    },
    visaCheckout: {
        JAWS: {
            chrome: [1, 2, 4, 7, 8, 10, 11],
            ie11: [1, 2, 4, 7, 8, 10, 11],
            firefox: [1, 2, 4, 7, 8, 10, 11]
        },
        NVDA: {
            chrome: [2, 3, 4, 6, 7, 8, 9],
            ie11: [2, 33]
        },
        SuperNova: {
            chrome: [6, 19, 20]
        }
    },
    masterpass: {
        JAWS: {
            chrome: [1, 2, 4, 5, 7, 8, 10, 11, 13, 14],
            ie11: [1, 2, 4, 5, 7, 8, 10, 11, 13],
            firefox: [1, 2, 4, 7, 8, 10, 11, 13, 14]
        },
        NVDA: {
            chrome: [2, 3, 4, 6, 9],
            ie11: [2, 33]
        },
        SuperNova: {
            chrome: [6, 19, 20, 25]
        }
    },
    billingShippingEmailFields: {
        JAWS: {
            chrome: [1, 2, 3, 7, 8, 10, 11],
            ie11: [1, 2, 3, 7, 8, 10, 11],
            firefox: [1, 2, 3, 7, 8, 10, 11]
        },
        NVDA: {
            chrome: [1, 2, 4, 5, 7, 8, 9],
            ie11: [2, 10, 12, 15, 21, 26, 35]
        },
        SuperNova: {
            chrome: [19, 20, 21, 22, 23, 30],
            ie11: [21]
        }
    },
    interactionOperationAuthorize: {
        JAWS: {
            chrome: [1, 2, 4, 7, 8, 10, 11],
            ie11: [1, 2, 4, 7, 8, 10, 11],
            firefox: [1, 2, 4, 7, 8, 10, 11]
        },
        NVDA: {
            chrome: [1, 2, 4, 5, 7, 8, 9],
            ie11: [2, 10, 12, 15, 32]
        },
        SuperNova: {
            chrome: [19, 20, 21, 22, 23, 30],
            ie11: [21]
        }
    },
    apms: {
        JAWS: {
            chrome: [1, 2, 8, 10, 11],
            ie11: [1, 2, 8, 10, 11],
            firefox: [1, 2, 8, 10, 11]
        },
        NVDA: {
            chrome: [2, 4, 5, 6, 7, 8],
            ie11: [1, 2, 15, 33]
        },
        SuperNova: {
            chrome: [1, 6, 19, 20, 21]
        }
    },
    paypal: {
        JAWS: {
            chrome: [1, 2, 4, 7, 8, 10, 11],
            ie11: [1, 2, 4, 7, 8, 10, 11],
            firefox: [1, 2, 4, 7, 8, 10, 11]
        },
        NVDA: {
            chrome: [2, 4, 5, 6, 7],
            ie11: [2, 33]
        },
        SuperNova: {
            chrome: [6, 19, 20]
        }
    },
    banamexPaymentPlans: {
        JAWS: {
            chrome: [2, 3, 4, 6, 14],
            ie11: [2, 3, 4, 6, 14],
            firefox: [2, 3, 4, 6]
        },
        NVDA: {
            chrome: [2, 4, 5, 6, 7, 8, 9],
            ie11: [2, 32, 33, 12, 15]
        },
        SuperNova: {
            chrome: [6, 18, 19, 20],
            ie11: [18, 21, 22, 23]
        }
    },
    surchargeFields: {
        JAWS: {
            chrome: [2, 5, 10, 12, 19,],
            ie11: [2, 5, 10, 12, 19],
            firefox: [2, 4, 12, 19, 21, 27]
        },
        NVDA: {
            chrome: [2, 4, 5, 9, 11, 12],
            ie11: [2, 15, 32]
        },
        SuperNova: {
            chrome: [19, 20, 21, 22, 23, 30],
            ie11: [21]
        }
    },
    achPayment: {
        JAWS: {
            chrome: [1, 2, 6, 10, 11, 14, 15],
            ie11: [],
            firefox: []
        },
        NVDA: {
            chrome: [1, 2, 3, 5, 6, 8, 17],
            ie11: [2, 17, 26, 33, 34]
        },
        SuperNova: {
            chrome: [1, 6, 19, 20, 26],
            ie11: [26]
        }
    },
    lineItemsFields: {
        JAWS: {
            chrome: [2, 7, 10, 12],
            ie11: [2, 4, 5, 7, 10, 12],
            firefox: [2, 4, 5, 7, 10, 12, 19]
        },
        NVDA: {
            chrome: [2, 4, 5, 9, 11, 12],
            ie11: [2, 12, 15, 32]
        },
        SuperNova: {
            chrome: [19, 20, 21, 22, 23, 30],
            ie11: [21]
        }
    }
};