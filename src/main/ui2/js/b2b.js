// Thumbnail slider
var slideIndex = 1;
showSlides(slideIndex);

function plusSlides(n) {
    showSlides(slideIndex += n);
}

function currentSlide(n) {
    showSlides(slideIndex = n);
}

function showSlides(n) {
    var i;
    var slides = document.getElementsByClassName("hero-img");
    var thumbnail = document.getElementsByClassName("thumb");
    if (n > slides.length) { slideIndex = 1 }
    if (n < 1) { slideIndex = slides.length }
    for (i = 0; i < slides.length; i++) {
        slides[i].style.display = "none";
    }
    for (i = 0; i < thumbnail.length; i++) {
        thumbnail[i].className = thumbnail[i].className.replace(" active", "");
    }
    slides[slideIndex - 1].style.display = "block";
    thumbnail[slideIndex - 1].className += " active";
}