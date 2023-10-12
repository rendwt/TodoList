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
    $.ajax({
        url: 'displaycompletedlistservlet',
        method: 'GET',
        dataType: 'html',
        success: function(data) {
            $('#completedlist').html(data);
        },
        error: function() {
            console.log('Error fetching list');
        }
    });
});
$(document).ready(function() {
    $(document).on('click', '.status-button, .status-button-remove', function(event) {
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
$(document).ready(function() {
    $(document).on('click', '.status-button1, .status-button1-remove', function(event) {
        var id = $(this).data("id");
        var status = $(this).data("status");
        $.ajax({
            type: "POST",
            url: "updatecompletedlistservlet",
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
$(document).ready(function () {
    $(document).on('click', '.edit-button', function(event){
        var editForm = $(this).closest("tr").next(".edit-form");
        editForm.toggle();
        $(".edit-form").not(editForm).hide();
    });
});
$(document).ready(function () {
    $("#inputItemButton").click(function () {
        $("#overlay").show();
        $("#popup").show();
    });

    $("#closePopup").click(function () {
        $("#overlay").hide();
        $("#popup").hide();
    });
});
