let lecturer = null;
let dashboardData = null;

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

/* =========================
   LOAD FROM STATISTICS API
========================= */

async function loadModules() {

  const tableBody = document.getElementById("modulesTableBody");

  try {

    tableBody.innerHTML = `
      <tr><td colspan="6">Loading...</td></tr>
    `;

    const res = await fetch(
      'https://automative-graiding-system.onrender.com/api/module/overview/${lecturer.id}',
      { credentials: "include" }
    );

    if (!res.ok) {
      throw new Error("Failed to load modules");
    }

    dashboardData = await res.json();

    renderModules(tableBody, dashboardData);

  } catch (err) {

    console.error(err);

    tableBody.innerHTML = `
      <tr><td colspan="6" style="color:red;">Error loading modules</td></tr>
    `;
  }
}

/* =========================
   RENDER MODULE TABLE
========================= */

function renderModules(tableBody, data) {

  tableBody.innerHTML = "";

  const modules = data.modules || [];

  if (!modules.length) {
    tableBody.innerHTML = `
      <tr><td colspan="6">No modules found</td></tr>
    `;
    return;
  }

  modules.forEach(module => {

    const code = module.code;

    /* =========================
       MAIN ROW (CLICKABLE)
    ========================= */

    const mainRow = document.createElement("tr");

    mainRow.style.cursor = "pointer";

    mainRow.onclick = () => toggleModule(code);

    mainRow.innerHTML = `
      <td>${module.code}</td>
      <td>${module.name || "-"}</td>
      <td>${module.credits || "-"}</td>
      <td>${module.assignedCredits || "-"}</td>
      <td>${module.courseworkCount || 0}</td>
      <td>${module.studentsEnrolled || 0}</td>
    `;

    tableBody.appendChild(mainRow);

    /* =========================
       EXPAND ROW (STUDENTS)
    ========================= */

    const expandRow = document.createElement("tr");

    expandRow.id = `expand-${code}`;
    expandRow.style.display = "none";

    expandRow.innerHTML = `
      <td colspan="6">
        ${renderStudentTable(module.students || [])}
      </td>
    `;

    tableBody.appendChild(expandRow);
  });
}

/* =========================
   STUDENT TABLE HTML
========================= */

function renderStudentTable(students) {

  if (!students.length) {
    return `<div class="small-note">No students enrolled</div>`;
  }

  return `
    <table class="student-table">

      <thead>
        <tr>
          <th>Name</th>
          <th>Email</th>
          <th>Missed Assignments</th>
          <th>Average Grade</th>
        </tr>
      </thead>

      <tbody>

        ${students.map(s => `
          <tr>
            <td>${s.name}</td>

            <td>
              <a class="email-link" href="mailto:${s.email}">
                ${s.email}
              </a>
            </td>

            <td>${s.missedAssignments ?? 0}</td>

            <td>${(s.avgGrade ?? 0).toFixed(1)}</td>
          </tr>
        `).join("")}

      </tbody>

    </table>
  `;
}

/* =========================
   TOGGLE DROPDOWN
========================= */

function toggleModule(code) {

  const row = document.getElementById(`expand-${code}`);

  if (!row) return;

  row.style.display =
    row.style.display === "table-row" ? "none" : "table-row";
}