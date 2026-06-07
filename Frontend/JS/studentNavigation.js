function openViewModules(){
 window.location.href = "studentViewModules.html";
}

function openGrades(){
 window.location.href = "studentGrades.html";
}

function logout() {
  localStorage.clear();
  window.location.href = "/index.html";
}

function openDashboard(){
window.location.href = "lecturer-dashboard.html";
}

function openAssignments(){
window.location.href = "studentAssignments.html";
}

document.addEventListener("DOMContentLoaded", () => {

  const student = JSON.parse(localStorage.getItem("user"));
  const role = localStorage.getItem("role");

  // Not logged in
  if (!student) {
    window.location.href = "login.html";
    return;
  }

  // Wrong role (matches your auth file: "lecturer")
  if (role !== "student") {
    window.location.href = "login.html";
    return;
  }

  // Safely set lecturer name
  const nameEl = document.getElementById("studentName");

  if (nameEl) {
    const first = student.firstName ?? "";
    const last = student.lastName ?? "";

    nameEl.textContent = `${first} ${last}`.trim() || "Student";
  }

});