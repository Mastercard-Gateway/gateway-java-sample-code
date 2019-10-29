var Wcagdefects = [
    {
        id: 1,
        fixed: false,
        status: ["IN_PROGRESS", "JOHN_CONFIRM_NEEDED"],
        description: "Required fields error messages are not being read neither when navigating into the " +
            "fields nor when using the Enter button LIGHTBOX and FULLPAGE"
    },
    {
        id: 2,
        fixed: true,
        status: ["CLOSED_FULLPAGE", "FIXED"],
        description: "Cancel button not being read. Cannot navigate into the element LIGHTBOX only"
    },
    {
        id: 3,
        status: ["CLOSED"],
        fixed: true,
        description: "Radio button tab/arrow keys navigation LIGHTBOX and FULLPAGE"
    },
    {
        id: 4,
        fixed: true,
        status: ["CLOSED"],
        description: "Order review details are not being read LIGHTBOX and FULLPAGE"
    },
    {
        id: 5,
        fixed: true,
        status: ["CLOSED"],
        description: "Receipt page details are not being read"
    },
    {
        id: 6,
        fixed: false,
        status: ["CLOSED_FULLPAGE", "IN_PROGRESS_LIGHTBOX"],
        description: "\"Please select your payment method\" error message not being read when clicking on " +
            "Next without having selected any payment method"
    },
    {
        id: 7,
        fixed: false,
        status: ["CLOSED_FULLPAGE", "IN_PROGRESS_LIGHTBOX"],
        description: "\"Your transaction was unsuccessful\" message not being read LIGHTBOX and FULLPAGE"
    },
    {
        id: 8,
        fixed: true,
        status: ["CLOSED"],
        description: "Required field tag is not being read LIGHTBOX and FULLPAGE"
    },
    {
        id: 9,
        fixed: true,
        status: ["CLOSED_FULLPAGE", "CLOSED_LIGHTBOX"],
        description: "Pay Now button not working when pressing Enter key"
    },
    {
        id: 10,
        fixed: true,
        status: ["FIXED"],
        description: "Print button is not read in receipt. Focus doesn’t work in Print link and after push " +
            "the action is not launched. LIGHTBOX"
    },
    {
        id: 11,
        fixed: true,
        status: ["CLOSED"],
        description: "Transaction receipt don't have a close or done button. Push escape key does not close it. (Expected behavior)"
    },
    {
        id: 12,
        fixed: false,
        status: ["CLOSED_FULLPAGE", "IN_PROGRESS_LIGHTBOX"],
        description: "3 digits on back of your card is not read LIGHTBOX "
    },
    {
        id: 13,
        fixed: false,
        status: ["IN_PROGRESS"],
        description: "Can't read any element on the screen from Masterpass"
    },
    {
        id: 14,
        fixed: false,
        status: ["IN_PROGRESS"],
        description: "Close button not works after push enter key"
    },
    {
        id: 15,
        fixed: true,
        status: ["CLOSED_FULLPAGE", "FIXED_WAIT_QA"],
        description: "Previous button not being read. Cannot navigate into the element."
    },
    {
        id: 16,
        fixed: false,
        status: ["IN_PROGRESS"],
        description: "Radio button cannot be marked with <space> key"
    },
    {
        id: 17,
        fixed: true,
        status: ["FIXED"],
        description: "Messages below Routing number, Account number, and Confirm account number fields " +
            "are not being read when focusing on the elements"
    },
    {
        id: 18,
        fixed: false,
        status: ["IN_PROGRESS"],
        description: "\"3 digit on back of your card\" is being read 3 times after focusing into the Security Code input field"
    },
    {
        id: 19,
        fixed: false,
        status: ["REPORTED_BUG"],
        description: "Unable to type in the lighbox input elements"
    },
    {
        id: 20,
        fixed: false,
        status: ["REPORTED_BUG"],
        description: "Unable to TAB between the lightbox element"
    },
    {
        id: 21,
        fixed: true,
        status: ["FIXED"],
        description: "\"Star\" is being read instead of \"Required\""
    },
    {
        id: 22,
        fixed: false,
        status: ["REPORTED_BUG_SUPERNOVA_18.05"],
        description: "Edit mode. Dropdown elements' name is not being read, instead, the value is being read"
    },
    {
        id: 23,
        fixed: false,
        status: ["REPORTED_BUG_SUPERNOVA_18.05"],
        description: "Read mode. Dropdown value is being read instead of the element name when doing forward tabbing navigation."
    },
    {
        id: 24,
        fixed: false,
        status: ["IN_PROGRESS"],
        description: "Elements' names are not being read when clicking on the elements."
    },
    {
        id: 25,
        fixed: false,
        status: ["IN_PROGRESS"],
        description: "\"Learn more\" link is getting read as \"link\". Should be read as \"learn more link\""
    },
    {
        id: 26,
        fixed: true,
        status: ["FIXED"],
        description: "Required attribute is missing on mandatory fields"
    },
    {
        id: 27,
        fixed: false,
        status: ["IN_PROGRESS"],
        description: "The titles Surcharge and New total and their the values under them and also \"Payment type\" text is not read."
    },
    {
        id: 28,
        fixed: true,
        status: ["CLOSED"],
        description: "Pay with: Plan Amex. Cannot navigate into the elements. Tab or Arrow keys."
    },
    {
        id: 29,
        fixed: true,
        status: ["CLOSED"],
        description: "Amex Payment Plans select fields are disabled by default with screen readers."
    },
    {
        id: 30,
        fixed: false,
        status: ["DUPLICATE"],
        description: "Can't switch to payment form iFrame related to #20"
    },
    {
        id: 31,
        fixed: false,
        status: ["IN_PROGRESS"],
        description: "Surcharge text is being read in a page but is not displaying in HCO (lightbox only)."
    },
    {
        id: 32,
        fixed: false,
        status: ["DUPLICATE"],
        description: "Required fields error messages are not being read when tabbing into dropdown elements."
    },
    {
        id: 33,
        fixed: false,
        status: ["IN_PROGRESS"],
        description: "Focus is not visible in radio buttons (payment selection page)"
    },
    {
        id: 34,
        fixed: false,
        status: ["IN_PROGRESS"],
        description: "Mandatory fields are missing the Required ( * ) span label & it doesn't read as required field."
    },
    {
        id: 35,
        fixed: false,
        status: ["IN_PROGRESS"],
        description: "After getting mandatory fields error messages, the elements names are not being read when tabbing into such elements"
    },
    {
        id: 36,
        fixed: false,
        status: ["IN_PROGRESS"],
        description: "Color contrast issue: Order review page - Pay Now button"
    },
    {
        id: 37,
        fixed: false,
        status: ["IN_PROGRESS"],
        description: "Color contrast issue: Continue button (ACH Costco payment options)"
    }
];