$(document).ready(function() {
    $.ajax({
        url: 'geteventservlet',
        method: 'GET',
        dataType: 'html',
        success: function(data) {
            $('#events').html(data);
        },
        error: function() {
            console.log('Error fetching list');
        }
    });
});
$(document).ready(function () {
    $("#createEventButton").click(function () {
        $("#overlay").show();
        $("#popup").show();
    });

    $("#closePopup").click(function () {
        $("#overlay").hide();
        $("#popup").hide();
    });
});
