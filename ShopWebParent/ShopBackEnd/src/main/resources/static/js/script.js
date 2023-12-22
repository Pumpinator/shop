$(document).ready(() => {
    $("#logoutLink").on("click", (event) => {
        event.preventDefault();
        document.logoutForm.submit();
    })
    $(".navbar .dropdown").hover(
        () => {
            $(".navbar .dropdown").find('.dropdown-menu').first().stop(true, true).delay(200).slideDown();
        },
        () => {
            $(".navbar .dropdown").find('.dropdown-menu').first().stop(true, true).delay(100).slideUp();
        });
    $(".dropdown > a").click(() => {
        location.href = $(".dropdown-toggle").attr("href");
    });
});