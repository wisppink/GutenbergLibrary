const modal = document.querySelector(".modal");
const signInBtn = document.querySelector(".signin-btn");
const logInBtn = document.querySelector(".login-btn");
const closeModalBtn = document.getElementById("closeModalBtn");
const modalInside = document.querySelector(".modal-inside");

const modalMultiButton = document.querySelector(".modal-button");
const modalMultiHeading = document.querySelector(".modal-heading");
// open-close modal start
function openModal() {
  modal.classList.remove("hidden");
}

function closeModal() {
  modal.classList.add("hidden");
}

signInBtn.addEventListener("click", function () {
  modalMultiHeading.innerHTML = "Sign In To Cbu Bookshelf";
  modalMultiButton.innerHTML = "Sign In";
  openModal();
});

logInBtn.addEventListener("click", function () {
  modalMultiHeading.innerHTML = "Login To Cbu Bookshelf";
  modalMultiButton.innerHTML = "Login";
  openModal();
});

closeModalBtn.addEventListener("click", closeModal);

window.onclick = function (event) {
  if ((event.target === modal) & (event.target !== modalInside)) {
    closeModal();
  }
};
// open-close modal end

function goToPage(page) {
  window.location.href = `html/${page}.html`;
}

modalMultiButton.addEventListener("click", function () {
  if (modalMultiButton.innerHTML == "Login") {
    goToPage("bookSearch");
  }
});
