console.log("Script loaded!");

const toggleSidebar = () => {
    console.log("Toggling sidebar!");
    if ($(".sidebar").hasClass("visible")) {
        $(".sidebar").removeClass("visible");
        $(".sidebar").hide();
        $(".content").css("margin-left", "0%");
    } else {
        $(".sidebar").addClass("visible");
        $(".sidebar").show();
        $(".content").css("margin-left", "20%");
    }
};

$(document).ready(function() {
    $("#sidebarToggle").click(function() {
        toggleSidebar();
    });

    $(".crossBtn").click(function() {
        toggleSidebar();
    });
});
