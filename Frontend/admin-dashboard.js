
const user = JSON.parse(localStorage.getItem("user"));


if (user) {
  document.getElementById("adminName").textContent =
    user.firstName + " " + user.lastName;
}

function showLecturerPanel() {
  alert("Open Lecturer Management Window");
}

function showStudentPanel() {
  alert("Open Student Management Window");
}

function openAddStudent() {
  window.location.href = "addStudent.html";
}

function logout() {
  localStorage.clear();
  window.location.href = "/index.html";
}