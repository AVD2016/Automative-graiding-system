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

      document.getElementById("adminName").textContent =
        user ? `${user.firstName} ${user.lastName}` : "Lecturer";

    });

    function openDashboard() {
      window.location.href = "lecturer-dashboard.html";
    }

//login check
document.addEventListener("DOMContentLoaded", async () => {

  const lecturer = JSON.parse(localStorage.getItem("user"));
  const role = localStorage.getItem("role");

  // Redirect if not logged in
  if (!lecturer) {
    window.location.href = "login.html";
    return;
  }

  // Redirect if logged in user is not a lecturer
  if (role !== "lecturer") {
    window.location.href = "login.html";
    return;
  }

  document.getElementById("lecturerName").textContent =
    `${lecturer.firstName} ${lecturer.lastName}`;

  await loadModules();

});