const API_BASE = "https://automative-graiding-system.onrender.com/api";

let students = [];
let lecturers = [];
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

  await loadAllData();
});

/* =========================
   LOAD DATA
========================= */

async function loadAllData() {

  try {

    // You may already have these endpoints or will map them later
    const [studentsRes, lecturersRes] = await Promise.all([
      fetch(`${API_BASE}/admin/students`),
      fetch(`${API_BASE}/admin/lecturers`)
    ]);

    if (!studentsRes.ok || !lecturersRes.ok) {
      throw new Error("Failed to load admin data");
    }

    students = await studentsRes.json();
    lecturers = await lecturersRes.json();

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
   STATUS CLASSIFICATION
========================= */

function getResult(avg) {

  if (avg < 40) return "Fail";
  if (avg < 60) return "Pass";
  if (avg < 75) return "Strong Pass";
  if (avg < 90) return "Merit";
  return "Distinction";
}

/* =========================
   STUDENTS TABLE
========================= */

function renderStudents() {

  const tbody = document.getElementById("studentsTable");
  tbody.innerHTML = "";

  students.forEach(s => {

    const fullName = `${s.firstName} ${s.lastName}`;

    const modules = s.modules ?? [];
    const email = s.email ?? "";

    const totalCredits = modules.reduce(
      (sum, m) => sum + (m.credits || 0),
      0
    );

    const completedCredits = modules.reduce(
      (sum, m) => sum + (m.completedCredits || 0),
      0
    );

    const avg = s.averageGrade ?? 0;
    const predicted = getResult(avg);

    tbody.innerHTML += `
      <tr>
        <td>${fullName}</td>
        <td>${modules.length}</td>
        <td>${totalCredits}</td>
        <td>${completedCredits}</td>
        <td>${predicted}</td>
        <td class="email">${email}</td>
      </tr>
    `;
  });
}

/* =========================
   LECTURERS TABLE
========================= */

function renderLecturers() {

  const tbody = document.getElementById("lecturersTable");
  tbody.innerHTML = "";

  lecturers.forEach(l => {

    const fullName = `${l.firstName} ${l.lastName}`;

    const workload = l.modules?.reduce(
      (sum, m) => sum + (m.credits || 0),
      0
    ) ?? 0;

    const unmarked = l.unmarkedPastDeadline ?? 0;
    const email = l.email ?? "";

    tbody.innerHTML += `
      <tr>
        <td>${fullName}</td>
        <td>${workload}</td>
        <td>${unmarked}</td>
        <td class="email">${email}</td>
      </tr>
    `;
  });
}

/* =========================
   OVERVIEW STATS
========================= */

function renderOverview() {

  const totalStudents = students.length;
  const totalLecturers = lecturers.length;

  let totalAvg = 0;
  let count = 0;
  let pendingMarking = 0;

  students.forEach(s => {

    totalAvg += (s.averageGrade || 0);
    count++;

    pendingMarking += (s.pendingCount || 0);
  });

  const systemAvg = count === 0 ? 0 : totalAvg / count;

  document.getElementById("studentCount").textContent = totalStudents;
  document.getElementById("lecturerCount").textContent = totalLecturers;
  document.getElementById("pendingMarking").textContent = pendingMarking;
  document.getElementById("systemAverage").textContent =
    systemAvg.toFixed(1) + "%";
}

/* =========================
   CHART (GRADE DISTRIBUTION)
========================= */

function renderChart() {

  let fail = 0;
  let pass = 0;
  let strong = 0;
  let merit = 0;
  let distinction = 0;

  students.forEach(s => {

    const avg = s.averageGrade ?? 0;

    if (avg < 40) fail++;
    else if (avg < 60) pass++;
    else if (avg < 75) strong++;
    else if (avg < 90) merit++;
    else distinction++;
  });

  const ctx = document.getElementById("gradeDistributionChart");

  if (!ctx) return;

  if (chartInstance) chartInstance.destroy();

  chartInstance = new Chart(ctx, {

    type: "pie",

    data: {

      labels: ["Fail", "Pass", "Strong Pass", "Merit", "Distinction"],

      datasets: [{
        data: [fail, pass, strong, merit, distinction],
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