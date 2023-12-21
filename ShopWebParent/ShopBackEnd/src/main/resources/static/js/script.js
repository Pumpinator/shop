$(document).ready(() => {
    $("#logoutLink").on("click", (event) => {
        event.preventDefault();
        document.logoutForm.submit();
    })
})