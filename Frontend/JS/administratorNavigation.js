// Navigation

function openAddStudent() {
  window.location.href = "addStudent.html";
}


function openViewAllStudents() {
  window.location.href = "viewStudents.html";
}

function logout() {
  localStorage.clear();
  window.location.href = "/index.html";
}

function openAddNewLecturer(){
window.location.href = "addLecturer.html";
}

function openViewLecturers(){
  window.location.href = "viewLecturers.html"
}

function openDashboard(){
  window.location.href = "admin-dashboard.html"
}

document.addEventListener("DOMContentLoaded", async () => {

  const administrator = JSON.parse(localStorage.getItem("user"));
  const role = localStorage.getItem("role");

  // Redirect if not logged in
  if (!administrator) {
    window.location.href = "login.html";
    return;
  }

  // Redirect if wrong role
  if (role !== "administrator") {
    window.location.href = "login.html";
    return;
  }

  document.getElementById("administratorName").textContent =
    `${administrator.firstName} ${administrator.lastName}`;

  await loadModules();

});