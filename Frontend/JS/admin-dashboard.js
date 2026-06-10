const API_BASE = "https://automative-graiding-system.onrender.com/api";

let dashboard = null;
let chartInstance = null;

/* =========================
   INIT
========================= */

document.addEventListener("DOMContentLoaded", async () => {

  const admin = JSON.parse(localStorage.getItem("user"));

  if (!admin) {
    window.location.href = "login.html";
    return;
  }

  document.getElementById("adminName").textContent =
    `${admin.firstName} ${admin.lastName}`;

  await loadDashboard();
});



async function loadDashboard() {

  try {

    const res = await fetch(`${API_BASE}/statistics/admin`, {
      credentials: "include"
    });

    if (!res.ok) {
      throw new Error("Failed to load admin dashboard");
    }

    dashboard = await res.json();

    renderAll();

  } catch (err) {

    console.error(err);

    document.getElementById("studentsTable").innerHTML = `
      <tr><td colspan="6">Failed to load students</td></tr>
    `;

    document.getElementById("lecturersTable").innerHTML = `
      <tr><td colspan="4">Failed to load lecturers</td></tr>
    `;
  }
}

/* =========================
   MAIN RENDER
========================= */

function renderAll() {

  renderStudents();
  renderLecturers();
  renderOverview();
  renderChart();
}

/* =========================
   STUDENTS TABLE
========================= */

function renderStudents() {

  const tbody = document.getElementById("studentsTable");

  const students = dashboard.students || [];

  if (students.length === 0) {
    tbody.innerHTML = `<tr><td colspan="6">No students found</td></tr>`;
    return;
  }

  tbody.innerHTML = students.map(s => {

    const fullName = `${s.firstName} ${s.lastName}`;

    const avg = s.averageGrade ?? 0;

    const predicted =
      avg < 40 ? "Fail" :
      avg < 60 ? "Pass" :
      avg < 75 ? "Strong Pass" :
      avg < 90 ? "Merit" :
      "Distinction";

    return `
      <tr>
        <td>${fullName}</td>
        <td>${s.moduleCount ?? 0}</td>
        <td>${s.totalCredits ?? 0}</td>
        <td>${s.completedCredits ?? 0}</td>
        <td>${predicted}</td>
        <td class="email">${s.email ?? ""}</td>
      </tr>
    `;

  }).join("");
}

/* =========================
   LECTURERS TABLE
========================= */

function renderLecturers() {

  const tbody = document.getElementById("lecturersTable");

  const lecturers = dashboard.lecturers || [];

  if (lecturers.length === 0) {
    tbody.innerHTML = `<tr><td colspan="4">No lecturers found</td></tr>`;
    return;
  }

  tbody.innerHTML = lecturers.map(l => {

    return `
      <tr>
        <td>${l.firstName} ${l.lastName}</td>
        <td>${l.workloadCredits ?? 0}</td>
        <td>${l.unmarkedPastDeadline ?? 0}</td>
        <td class="email">${l.email ?? ""}</td>
      </tr>
    `;

  }).join("");
}

/* =========================
   OVERVIEW
========================= */

function renderOverview() {

  document.getElementById("studentCount").textContent =
    dashboard.students?.length ?? 0;

  document.getElementById("lecturerCount").textContent =
    dashboard.lecturers?.length ?? 0;

  document.getElementById("pendingMarking").textContent =
    dashboard.pendingMarking ?? 0;

  document.getElementById("systemAverage").textContent =
    (dashboard.systemAverage ?? 0).toFixed(1) + "%";
}

/* =========================
   CHART
========================= */

function renderChart() {

  const dist = dashboard.gradeDistribution || {};

  const data = [
    dist.fail ?? 0,
    dist.pass ?? 0,
    dist.strong ?? 0,
    dist.merit ?? 0,
    dist.distinction ?? 0
  ];

  const ctx = document.getElementById("gradeDistributionChart");

  if (!ctx) return;

  if (chartInstance) chartInstance.destroy();

  chartInstance = new Chart(ctx, {

    type: "pie",

    data: {

      labels: [
        "Fail",
        "Pass",
        "Strong Pass",
        "Merit",
        "Distinction"
      ],

      datasets: [{
        data,
        backgroundColor: [
          "#dc2626",
          "#f59e0b",
          "#3b82f6",
          "#10b981",
          "#7c3aed"
        ]
      }]
    },

    options: {

      responsive: true,

      plugins: {
        legend: {
          position: "bottom"
        }
      }
    }
  });
}