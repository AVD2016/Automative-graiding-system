let lecturer = null;

document.addEventListener("DOMContentLoaded", async () => {

  lecturer = JSON.parse(localStorage.getItem("user"));

  if (!lecturer) {
    window.location.href = "login.html";
    return;
  }

  document.getElementById("lecturerName").textContent =
    `${lecturer.firstName} ${lecturer.lastName}`;

  await loadModules();
});

async function loadModules() {

  const tableBody = document.getElementById("modulesTableBody");

  try {

    tableBody.innerHTML = `
      <tr><td colspan="6">Loading...</td></tr>
    `;

    const res = await fetch(
      `https://automative-graiding-system.onrender.com/api/module/overview/${lecturer.id}`
    );

    if (!res.ok) {
      throw new Error("Failed to load modules");
    }

    const modules = await res.json();

    tableBody.innerHTML = "";

    if (!modules.length) {
      tableBody.innerHTML = `
        <tr><td colspan="6">No modules found</td></tr>
      `;
      return;
    }

    modules.forEach(m => {

      const row = document.createElement("tr");

      row.innerHTML = `
        <td>${m.code}</td>
        <td>${m.name}</td>
        <td>${m.credits}</td>
        <td>${m.assignedCredits}</td>
        <td>${m.courseworkCount}</td>
        <td>${m.studentsEnrolled}</td>
      `;

      tableBody.appendChild(row);
    });

  } catch (err) {
    console.error(err);

    tableBody.innerHTML = `
      <tr><td colspan="6" style="color:red;">Error loading modules</td></tr>
    `;
  }
}

// NAV
function openViewModules() {
  window.location.href = "lecturerModules.html";
}

function openCreateAssignment() {
  window.location.href = "createAssignment.html";
}

function openMarkAssignment() {
  window.location.href = "markAssignment.html";
}

function logout() {
  localStorage.removeItem("user");
  localStorage.removeItem("role");
  window.location.href = "login.html";
}