$(function () {

    var notificationUrl = window.location.href.substring(0, window.location.href.lastIndexOf('/')) + "/process-webhook";
    var endpointInfoText = "In order to receive notifications, please configure the url - '<i>" + notificationUrl + "</i>' in the notification settings page within the Merchant Administration Portal.";
    $('.alert-info', '.webhooks').html(endpointInfoText);


    $.getJSON("list-webhook-notifications", function (data) {
        console.log("Webhooks Notifications - ", data);
        var notifications = [];
        $.each(data, function (key, val) {
            var timestamp = new Date(val.timestamp).toISOString();
            notifications.push("<tr><td scope=\"row\">" + timestamp + "</td><td>" + val.orderId + "</td><td>" + val.transactionId + "</td><td>" + val.orderStatus + "</td><td>" + val.amount + "</td></tr>");
        });

        if (notifications.length > 0) {
            $('.notifications').append(notifications).removeClass('invisible');
        }
    });
});