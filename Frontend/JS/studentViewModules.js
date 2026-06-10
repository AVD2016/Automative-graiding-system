// =========================
// STATE
// =========================

const API_BASE =
  "https://automative-graiding-system.onrender.com/api";

let student = null;
let modules = [];

// =========================
// INIT
// =========================

document.addEventListener("DOMContentLoaded", () => {

  student =
    JSON.parse(localStorage.getItem("user"));

  if (!student) {
    window.location.href = "login.html";
    return;
  }

  document.getElementById("studentName").textContent =
    `${student.firstName} ${student.lastName}`;

  loadModules();
});

// =========================
// LOAD MODULES
// =========================

async function loadModules() {

  try {

    const response = await fetch(
      `${API_BASE}/module/student/getModules/${student.id}`
    );

    if (!response.ok) {
      throw new Error("Failed to load modules");
    }

    modules = await response.json();

    renderModules();

  } catch (error) {

    console.error(error);

    document.getElementById("modulesTableBody").innerHTML = `
      <tr>
        <td colspan="5" class="loading">
          Failed to load modules
        </td>
      </tr>
    `;
  }
}

// =========================
// RENDER MODULES
// =========================
function renderModules() {

  const tableBody =
    document.getElementById("modulesTableBody");

  tableBody.innerHTML = "";

  if (modules.length === 0) {

    tableBody.innerHTML = `
      <tr>
        <td colspan="5" class="loading">
          No modules found
        </td>
      </tr>
    `;

    return;
  }

  modules.forEach(module => {

    const tr = document.createElement("tr");

    const avgGrade =
      module.averageGrade ?? 0;

    let gradeClass = "grade-green";
    let displayGrade = `${avgGrade.toFixed(1)}%`;

    // =========================
    // HANDLE -1 CASE
    // =========================
    if (avgGrade === -1) {
      displayGrade = "No submissions yet";
      gradeClass = "grade-grey"; // optional styling
    }
    else if (avgGrade < 40) {
      gradeClass = "grade-red";
    }
    else if (avgGrade < 60) {
      gradeClass = "grade-orange";
    }

    tr.innerHTML = `
      <td>${module.code}</td>

      <td>${module.name}</td>

      <td>${module.credits}</td>

      <td>
        ${module.assignmentCount ?? 0}
      </td>

      <td class="${gradeClass}">
        ${displayGrade}
      </td>
    `;

    tr.addEventListener("click", () => {
      openLecturerModal(module);
    });

    tableBody.appendChild(tr);
  });
}

// =========================
// OPEN MODAL
// =========================

function openLecturerModal(module) {

  document.getElementById("modalTitle").textContent =
    `${module.code} - Lecturers`;

  const tableBody =
    document.getElementById("lecturersTableBody");

  tableBody.innerHTML = "";

  const lecturers =
    module.lecturers || [];

  if (lecturers.length === 0) {

    tableBody.innerHTML = `
      <tr>
        <td colspan="2">
          No lecturers assigned
        </td>
      </tr>
    `;

  } else {

    lecturers.forEach(lecturer => {

      const tr = document.createElement("tr");

      tr.innerHTML = `
        <td>
          ${lecturer.firstName}
          ${lecturer.lastName}
        </td>

        <td>
          <a class="email-link"
             href="mailto:${lecturer.email}">
            ${lecturer.email}
          </a>
        </td>
      `;

      tableBody.appendChild(tr);
    });
  }

  document.getElementById("lecturerModal").style.display =
    "flex";
}

// =========================
// CLOSE MODAL
// =========================

function closeModal() {

  document.getElementById("lecturerModal").style.display =
    "none";
}

// =========================
// OUTSIDE CLICK CLOSE
// =========================

window.onclick = function(event) {

  const modal =
    document.getElementById("lecturerModal");

  if (event.target === modal) {
    closeModal();
  }
};