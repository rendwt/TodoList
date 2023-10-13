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
$(document).ready(function() {
    $(document).on('click', '.remove-button', function(event) {
        var id = $(this).data("id");
        $.ajax({
            type: "POST",
            url: "updateeventservlet",
            data: {
                id: id,
            },
            success: function(response) {
                location.reload();
            }
        });
    });
});
$(document).ready(function () {
    $(document).on('click', '.edit-button', function(event){
        var editForm = $(this).closest("tr").next(".edit-form");
        editForm.toggle();
        $(".edit-form").not(editForm).hide();
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
