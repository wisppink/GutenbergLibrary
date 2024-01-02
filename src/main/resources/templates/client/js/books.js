// profile modal open

profileLink = document.querySelector(".profile-link");
settingsLink = document.querySelector(".settings-link");

profileModal = document.querySelector(".profile-modal");
settingsModal = document.querySelector(".settings-modal");

profileLink.addEventListener("click", function () {
  profileModal.classList.remove("hidden");
  console.log(profileModal);
});
settingsLink.addEventListener("click", function () {
  settingsModal.classList.remove("hidden");
});

// profile modal close
const modals = document.querySelectorAll(".modal");
const modalInside = document.querySelector(".modal-inside");
const closeModalBtns = document.querySelectorAll(".close-modal-cross");

window.onclick = function (event) {
  modals.forEach((element) => {
    if ((event.target === element) & (event.target !== modalInside)) {
      element.classList.add("hidden");
    }
  });
};

closeModalBtns.forEach((element) => {
  element.addEventListener("click", function () {
    modals.forEach((el) => {
      el.classList.add("hidden");
    });
  });
});

// Book details modal open
const modalDetails = document.querySelector(".details-modal");
const btnDetalis = document.querySelectorAll(".details-btn");
btnDetalis.forEach((element) => {
  element.addEventListener("click", function () {
    modalDetails.classList.remove("hidden");
  });
});
