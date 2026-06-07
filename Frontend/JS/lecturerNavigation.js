// Navigation

function openViewModules() {
  window.location.href = "viewModules.html";
}


function openCreateAssignment() {
  window.location.href = "createAssignment.html";
}

function logout() {
  localStorage.clear();
  window.location.href = "/index.html";
}

function openMarkAssignment(){
window.location.href = "markAssignments.html";
}

document.addEventListener("DOMContentLoaded", () => {

      const user = JSON.parse(localStorage.getItem("user"));

      document.getElementById("lecturerName").textContent =
        user ? `${user.firstName} ${user.lastName}` : "Lecturer";

    });

    function openDashboard() {
      window.location.href = "lecturer-dashboard.html";
    }

document.addEventListener("DOMContentLoaded", () => {

  const lecturer = JSON.parse(localStorage.getItem("user"));
  const role = localStorage.getItem("role");

  // Not logged in
  if (!lecturer) {
    window.location.href = "login.html";
    return;
  }

  // Wrong role (matches your auth file: "lecturer")
  if (role !== "lecturer") {
    window.location.href = "login.html";
    return;
  }

  // Safely set lecturer name
  const nameEl = document.getElementById("lecturerName");

  if (nameEl) {
    const first = lecturer.firstName ?? "";
    const last = lecturer.lastName ?? "";

    nameEl.textContent = `${first} ${last}`.trim() || "Lecturer";
  }

});