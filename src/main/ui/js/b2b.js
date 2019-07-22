// Alert to confirm account creation
$(document).ready(function () {
    $('.sign-up').click(function () {
        confirm('Please click "OK" to confirm');
        window.location.href = "confirm-account.html"
    });

});