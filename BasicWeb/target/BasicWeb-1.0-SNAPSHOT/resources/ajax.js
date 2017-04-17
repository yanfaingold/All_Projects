function register() {
    var userName = jQuery('#userName').val();
    var password = jQuery('#password').val();

    jQuery.ajax({
        url: "AddUser",
        data: "userName=" + userName + "&password=" + password,
        success: function(response){
            // we have the response
            jQuery('#info').html(response);
            jQuery('#userName').val('');
            jQuery('#password').val('');
            jQuery('#info').css('color', 'green');
        },
        error: function(e){
            jQuery('#info').html(e.responseText);
            jQuery('#info').css('color', 'red');
        }
    });
}