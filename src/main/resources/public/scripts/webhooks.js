$(function () {

    $.getJSON("list-webhook-notifications", function (data) {
        var notifications = [];
        $.each(data, function (key, val) {
            notifications.push("<tr><th scope=\"row\">" + val.timestamp + "</th><td>" + val.orderId + "</td><td>" + val.transactionId + "</td><td>" + val.orderStatus + "</td><td>" + val.amount + "</td></tr>");
        });

        console.log("##### Notifications = ", notifications);
        if (notifications.length == 0) {
            $('.no-notification').remove('.invisible');
        }
        else {
            $('.notifications').append(notifications).remove('.invisible');
        }
    });
});