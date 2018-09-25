$(function () {
    $('.js-sweetalert').on('click', function () {
        showMessage($(this).data('type'));
    });
});

function showMessage(type) {
    if (type === 'basic') {
        showBasicMessage();
    }
    else if (type === 'with-title') {
        showWithTitleMessage();
    }
    else if (type === 'success') {
        showSuccessMessage();
    }
    else if (type === 'confirm') {
        showConfirmMessage();
    }
    else if (type === 'cancel') {
        showCancelMessage();
    }
    else if (type === 'with-custom-icon') {
        showWithCustomIconMessage();
    }
    else if (type === 'html-message') {
        showHtmlMessage();
    }
    else if (type === 'autoclose-timer') {
        showAutoCloseTimerMessage();
    }
    else if (type === 'prompt') {
        showPromptMessage();
    }
    else if (type === 'ajax-loader') {
        showAjaxLoaderMessage();
    }
}

//These codes takes from http://t4t5.github.io/sweetalert/
function showBasicMessage() {
    swal("Here's a message!");
}

function showWithTitleMessage() {
    swal("Here's a message!", "It's pretty, isn't it?");
}

function showSuccessMessage() {
    swal("Good job!", "You clicked the button!", "success");
}

function showConfirmMessage() {
    showAJAXConfirmMessage();
}
function showConfirmMessage1() {
    // swal("Hello world!");
    // swal("Here's the title!", "...and here's the text!");
    /*swal("Click on either the button or outside the modal.")
        .then((value) => {
            swal(`The returned value is: ${value}`);
        });*/

    /*swal({
        title: "Are you sure?",
        text: "You will not be able to recover this imaginary file!",
        type: "warning",
        showCancelButton: true,
        confirmButtonColor: "#dc3545",
        confirmButtonText: "Yes, delete it!",
        closeOnConfirm: false
    }, function () {
        swal("Deleted!", "Your imaginary file has been deleted.", "success");
    });*/

    /*swal({
        text: 'Search for a movie. e.g. "La La Land".',
        content: "input",
        button: {
            text: "Search!",
            closeModal: false,
        }
    })
        .then(name => {
            if (!name) throw null;

            return fetch(`https://itunes.apple.com/search?term=${name}&entity=movie`);
        })
        .then(results => {
            return results.json();
        })
        .then(json => {
            const movie = json.results[0];

            if (!movie) {
                return swal("No movie was found!");
            }

            const name = movie.trackName;
            const imageURL = movie.artworkUrl100;

            swal({
                title: "Top result:",
                text: name,
                icon: imageURL,
            });
        })
        .catch(err => {
            if (err) {
                swal("Oh noes!", "The AJAX request failed!", "error");
            } else {
                swal.stopLoading();
                swal.close();
            }
        });*/
    /*swal({
        title: 'Submit your Github username',
        input: 'text',
        inputAttributes: {
            autocapitalize: 'off'
        },
        showCancelButton: true,
        confirmButtonText: 'Look up',
        showLoaderOnConfirm: true,
        preConfirm: (login) => {
            return fetch(`//api.github.com/users/${login}`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error(response.statusText)
                    }
                    return response.json()
                })
                .catch(error => {
                    swal.showValidationMessage(
                        `Request failed: ${error}`
                    )
                })
        },
        allowOutsideClick: () => !swal.isLoading()
    }).then((result) => {
        if (result.value) {
            swal({
                title: `${result.value.login}'s avatar`,
                imageUrl: result.value.avatar_url
            })
        }
    })*/
    swal({
        title: 'Submit your Github username',
        input: 'text',
        inputAttributes: {
            autocapitalize: 'off'
        },
        showCancelButton: true,
        confirmButtonText: 'Search',
        showLoaderOnConfirm: true,
        preConfirm: (name) => {
            return fetch(`https://itunes.apple.com/search?term=${name}&entity=movie`)
            // return fetch(`//api.github.com/users/${login}`)
                .then(response => {
                    if(!response.ok) {
                        throw new Error(response.statusText);
                    }
                    return response.json()
                })
                .catch(error => {
                    swal("Oh noes!", "The AJAX request failed!", "error");
                })
        }
    })

        /*.then((result) => {

            if (result.value) {
                swal({
                    title: `${result.value.login}'s avatar`,
                    imageUrl: result.value.avatar_url
                })
            }

        })*/
        .then(json => {
            const movie = json;

            if (!movie) {
                return swal("No movie was found!");
            }

            const name = movie.trackName;
            const imageURL = movie.artworkUrl100;

            swal({
                title: "Top result:",
                text: name,
                icon: imageURL
            });
        })
        ;
    /*swal({
        title: "Are you sure?",
        text: 'Search for a movie. e.g. "La La Land".',
        content: "input",
        button: {
            text: "Search!",
            closeModal: false,
        },
    })
        .then(name => {
            if (!name) throw null;

            return fetch(`https://itunes.apple.com/search?term=${name}&entity=movie`);
        })
        .then(results => {
            return results.json();
        })
        .then(json => {
            const movie = json.results[0];

            if (!movie) {
                return swal("No movie was found!");
            }

            const name = movie.trackName;
            const imageURL = movie.artworkUrl100;

            swal({
                title: "Top result:",
                text: name,
                icon: imageURL,
            });
        })
        .catch(err => {
            if (err) {
                swal("Oh noes!", "The AJAX request failed!", "error");
            } else {
                swal.stopLoading();
                swal.close();
            }
        });*/

    /*swal.mixin({
        input: 'text',
        confirmButtonText: 'Next &rarr;',
        showCancelButton: true,
        progressSteps: ['1', '2', '3']
    }).queue([
        {
            title: 'Question 1',
            text: 'Chaining swal2 modals is easy'
        },
        'Question 2',
        'Question 3'
    ]).then((result) => {
        if (result.value) {
            swal({
                title: 'All done!',
                html:
                'Your answers: <pre><code>' +
                JSON.stringify(result.value) +
                '</code></pre>',
                confirmButtonText: 'Lovely!'
            })
        }
    })*/
}

function showAJAXConfirmMessage(url, confirmTitle, confirmMessage, successTitle, successMessage, confirmButtonText, successIcon) {
    swal({
        title: title,
        text: message,
        type: "warning",
        showCancelButton: true,
        confirmButtonColor: "#dc3545",
        confirmButtonText: confirmButtonText,

        preConfirm: (yes) => {
            return fetch(url)
                .then(response => {
                    if (!response.ok) {
                        throw new Error(response.statusText);
                    }
                    return response.json()
                })
                .catch(error => {
                    swal("Error!", "The AJAX request failed!", "error");
                })
        }
    })
        .then((result) => {

            if (result.value) {
                swal(
                    successTitle,
                    successMessage,
                    successIcon
                )
            }

        });
}

function showCancelMessage() {
    swal({
        title: "Are you sure?",
        text: "You will not be able to recover this imaginary file!",
        type: "warning",
        showCancelButton: true,
        confirmButtonColor: "#dc3545",
        confirmButtonText: "Yes, delete it!",
        cancelButtonText: "No, cancel plx!",
        closeOnConfirm: false,
        closeOnCancel: false
    }, function (isConfirm) {
        if (isConfirm) {
            swal("Deleted!", "Your imaginary file has been deleted.", "success");
        } else {
            swal("Cancelled", "Your imaginary file is safe :)", "error");
        }
    });
}

function showWithCustomIconMessage() {
    swal({
        title: "Sweet!",
        text: "Here's a custom image.",
        imageUrl: "assets/img/sm/avatar2.jpg"
    });
}

function showHtmlMessage() {
    swal({
        title: "HTML <small>Title</small>!",
        text: "A custom <span style=\"color: #CC0000\">html<span> message.",
        html: true
    });
}

function showAutoCloseTimerMessage() {
    swal({
        title: "Auto close alert!",
        text: "I will close in 2 seconds.",
        timer: 2000,
        showConfirmButton: false
    });
}

function showPromptMessage() {
    swal({
        title: "An input!",
        text: "Write something interesting:",
        type: "input",
        showCancelButton: true,
        closeOnConfirm: false,
        animation: "slide-from-top",
        inputPlaceholder: "Write something"
    }, function (inputValue) {
        if (inputValue === false) return false;
        if (inputValue === "") {
            swal.showInputError("You need to write something!"); return false
        }
        swal("Nice!", "You wrote: " + inputValue, "success");
    });
}

function showAjaxLoaderMessage() {
    swal({
        title: "Ajax request example",
        text: "Submit to run ajax request",
        type: "info",
        showCancelButton: true,
        closeOnConfirm: false,
        showLoaderOnConfirm: true,
    }, function () {
        setTimeout(function () {
            swal("Ajax request finished!");
        }, 2000);
    });
}