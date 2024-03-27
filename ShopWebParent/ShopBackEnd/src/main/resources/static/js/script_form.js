$(document).ready(() => {
    $("#buttonCancel").on("click", () => {
        window.location = moduleUrl;
    });
});

$("#fileImage").change((event) => {
    imageInput = event.currentTarget;
    fileSize = imageInput.files[0].size;
    if (fileSize > 1048576) {
        imageInput.setCustomValidity("You must choose an image less than 1 mb.");
        imageInput.reportValidity();
    } else {
        imageInput.setCustomValidity("");
        showThumbnail(imageInput);
    }
})

function showThumbnail(imageInput) {
    var image = imageInput.files[0];
    var fileReader = new FileReader();
    fileReader.onload = (event) => {
        $(".thumbnail").attr("src", event.target.result);
    }
    fileReader.readAsDataURL(image);
}

function showAlert(title, text, icon) {
    Swal.fire({
        title: title,
        text: text,
        icon: icon,
        confirmButtonColor: "#000",
    });
}