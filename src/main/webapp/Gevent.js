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