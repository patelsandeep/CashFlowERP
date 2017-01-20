$(function () {
    if ($.browser.msie && $.browser.version.substr(0, 1) < 7)
    {
        $('li').has('ul').mouseover(function () {
            $(this).children('ul').css('visibility', 'visible');
        }).mouseout(function () {
            $(this).children('ul').css('visibility', 'hidden');
        })
    }

    /* Mobile */
    $('#menu-wrap').prepend('<div id="menu-trigger">Menu</div>');
    $("#menu-trigger").on("click", function () {
        $("#menu").slideToggle();
    });

    // iPad
    var isiPad = navigator.userAgent.match(/iPad/i) != null;
    if (isiPad)
        $('#menu ul').addClass('no-transition');
});

$(document).ready(function () {

    $(".company-name").click(function (e) {
        e.preventDefault();
        $("#company-name").toggle();
        $(".company-name").toggleClass("menu-open");
    });

    $("#company-name").mouseup(function () {
        return false
    });
    $(document).mouseup(function (e) {
        if ($(e.target).parent("a.company-name").length == 0) {
            $(".company-name").removeClass("menu-open");
            $("#company-name").hide();
        }
    });


    $(".client-name").click(function (e) {
        e.preventDefault();
        $("#client-profile-box").toggle();
        $(".client-name").toggleClass("menu-open");
    });

    $("#client-profile-box").mouseup(function () {
        return false
    });
    $(document).mouseup(function (e) {
        if ($(e.target).parent("a.client-name").length == 0) {
            $(".client-name").removeClass("menu-open");
            $("#client-profile-box").hide();
        }
    });

});

$(function () {
    $("#datepicker-from").datepicker();
});
$(function () {
    $("#datepicker-to").datepicker();
});
$(function () {
    $("#datepicker-indate").datepicker();
});
$(function () {
    $("#datepicker-duedate").datepicker();
});
$(function () {
    $("#datepicker-shisdate").datepicker();
});

$(document).ready(function () {

    //Default Action
    $(".tab_content").hide(); //Hide all content
    $("ul.tabs li:first").addClass("active").show(); //Activate first tab
    $(".tab_content:first").show(); //Show first tab content

    //On Click Event
    $("ul.tabs li").click(function () {
        $("ul.tabs li").removeClass("active"); //Remove any "active" class
        $(this).addClass("active"); //Add "active" class to selected tab
        $(".tab_content").hide(); //Hide all tab content
        var activeTab = $(this).find("a").attr("href"); //Find the rel attribute value to identify the active tab + content
        $(activeTab).fadeIn(); //Fade in the active content
        return false;
    });

});
