var WCAG_REPORT = {
    statuses: {
        "FIXED": {
            title: "FIXED",
            color: "SUCCESS",
            description: "Defect has been fixed and ready to be tested."
        },
        "FIXED_WAIT_QA": {
            title: "FIXED, WAITING QA",
            color: "SUCCESS",
            description: "Defect has been fixed but not yet deployed to QA environment. Not available for testing yet."
        },
        "IN_PROGRESS": {
            title: "IN PROGRESS",
            color: "WARNING",
            description: "Work in progress"
        },
        "IN_PROGRESS_LIGHTBOX": {
            title: "LIGHTBOX: IN PROGRESS",
            color: "WARNING",
            description: "Fix for Lightbox mode is in progress"
        },
        "CLOSED": {
            title: "CLOSED",
            color: "SUCCESS",
            description: "Works in read mode - can be closed. (Using arrow keys but not TAB button). Ready for testing."
        },
        "CLOSED_FULLPAGE": {
            title: "FULLPAGE CLOSED",
            color: "SUCCESS",
            description: "Works fine in Full page mode. Ready for testing."
        },
        "CLOSED_LIGHTBOX": {
            title: "LIGHTBOX CLOSED",
            color: "SUCCESS",
            description: "Works in Light box mode. Ready for testing."
        },
        "DUPLICATE": {
            title: "DUPLICATE",
            color: "SUCCESS",
            description: "Defect is duplicate of another one"
        },
        "REPORTED_BUG": {
            title: "REPORTED BUG (Screen Reader / Browser)",
            color: "DANGER",
            description: "Bug has been reported to Screen reader technical support team"
        },
        "REPORTED_BUG_SUPERNOVA_18.05": {
            title: "REPORTED BUG FROM SUPERNOVA WILL BE FIXED IN 18.05",
            color: "DANGER",
            description: "Defect has been reported and we received future release version where this defect will be fixed."
        },
        "JOHN_CONFIRM_NEEDED": {
            title: "NEEDS CONFIRMATION FROM JOHN",
            color: "DANGER",
            description: "We need confirmation from John if it is acceptable to use read mode in this scenario."
        }

    },
    defects: Wcagdefects,
    scenarios: WCAGScenarios
};