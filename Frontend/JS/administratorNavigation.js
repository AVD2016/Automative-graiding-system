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

document.addEventListener("DOMContentLoaded", () => {

      const user = JSON.parse(localStorage.getItem("user"));

      document.getElementById("adminName").textContent =
        user ? `${user.firstName} ${user.lastName}` : "Lecturer";

    });