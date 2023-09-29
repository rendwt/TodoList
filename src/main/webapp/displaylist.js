$(document).ready(function() {
    $.ajax({
        url: 'displaylistservlet',
        method: 'GET',
        dataType: 'html',
        success: function(data) {
            $('#list').html(data);
        },
        error: function() {
            console.log('Error fetching list');
        }
    });
});
$(document).ready(function() {
    $(document).on('click', '.status-button', function(event) {
        var id = $(this).data("id");
        var status = $(this).data("status");
        $.ajax({
            type: "POST",
            url: "updatelistservlet",
            data: {
                id: id,
                status: status
            },
            success: function(response) {
                location.reload();
            }
        });
    });
});