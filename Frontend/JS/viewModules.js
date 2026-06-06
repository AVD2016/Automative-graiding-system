// =========================
// STATE
// =========================

let lecturer = null;

// =========================
// INIT
// =========================

document.addEventListener("DOMContentLoaded", async () => {

  lecturer = JSON.parse(localStorage.getItem("user"));

  // SECURITY CHECK
  if (!lecturer) {
    window.location.href = "login.html";
    return;
  }

  // SHOW NAME
  const nameElement = document.getElementById("lecturerName");

  if (nameElement) {
    nameElement.textContent =
      `${lecturer.firstName} ${lecturer.lastName}`;
  }

  // LOAD MODULES
  await loadModules();
});


// =========================
// LOAD MODULES
// =========================

async function loadModules() {

  const tableBody = document.getElementById("modulesTableBody");

  try {

    tableBody.innerHTML = `
      <tr>
        <td colspan="6" class="loading">
          Loading modules...
        </td>
      </tr>
    `;

    const response = await fetch(
      `https://automative-graiding-system.onrender.com/api/module/getLecturerModules/${lecturer.id}`
    );

    if (!response.ok) {
      throw new Error("Failed to fetch modules");
    }

    const modules = await response.json();

    tableBody.innerHTML = "";

    if (!modules || modules.length === 0) {
      tableBody.innerHTML = `
        <tr>
          <td colspan="6" class="loading">
            No modules assigned
          </td>
        </tr>
      `;
      return;
    }

    modules.forEach(module => {

      // =========================
      // SAFE DEFAULTS (important)
      // =========================

      const moduleCode = module.code || "-";
      const moduleName = module.name || "-";
      const moduleCredits = module.credits || 0;

      // backend should ideally provide these:
      const assignedCredits = module.assignedCredits ?? 0;
      const courseworkCount = module.courseworkCount ?? 0;
      const studentsEnrolled = module.studentsEnrolled ?? 0;

      // =========================
      // ROW
      // =========================

      const row = document.createElement("tr");

      row.innerHTML = `
        <td>${moduleCode}</td>
        <td>${moduleName}</td>
        <td>${moduleCredits}</td>
        <td>${assignedCredits}</td>
        <td>${courseworkCount}</td>
        <td>${studentsEnrolled}</td>
      `;

      tableBody.appendChild(row);
    });

  } catch (error) {

    console.error(error);

    tableBody.innerHTML = `
      <tr>
        <td colspan="6" class="loading" style="color:red;">
          Error loading modules
        </td>
      </tr>
    `;
  }
}


// =========================
// NAVIGATION
// =========================

function openViewModules() {
  window.location.href = "lecturer-modules.html";
}

function openCreateAssignment() {
  window.location.href = "create-assignment.html";
}

function openMarkAssignment() {
  window.location.href = "mark-assignment.html";
}


// =========================
// LOGOUT
// =========================

function logout() {
  localStorage.removeItem("user");
  localStorage.removeItem("role");
  window.location.href = "login.html";
}