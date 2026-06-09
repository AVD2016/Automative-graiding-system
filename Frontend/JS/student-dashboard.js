const API_BASE = "https://automative-graiding-system.onrender.com/api";

let student = null;
let dashboardData = null;
let chart = null;

/* =========================
   INIT
========================= */

document.addEventListener("DOMContentLoaded", async () => {

  student = JSON.parse(localStorage.getItem("user"));

  if (!student) {
    window.location.href = "login.html";
    return;
  }

  document.getElementById("studentName").textContent =
    `${student.firstName} ${student.lastName}`;

  await loadDashboard();
});

/* =========================
   LOAD BACKEND
========================= */

async function loadDashboard() {

  try {

    const res = await fetch(
      `${API_BASE}/statistics/student/${student.id}`,
      {
        credentials: "include"
      }
    );

    if (!res.ok) {
      throw new Error("Failed to load dashboard");
    }

    dashboardData = await res.json();

    renderCalendar();
    renderWarnings();
    renderChart();

  } catch (err) {

    console.error(err);

    alert("Failed to load dashboard");
  }
}

/* =========================
   CALENDAR
========================= */

function renderCalendar() {

  const calendar = document.getElementById("calendar");

  if (!calendar) return;

  calendar.innerHTML = "";

  const today = new Date();

  const year = today.getFullYear();
  const month = today.getMonth();

  const firstDay = new Date(year, month, 1).getDay();
  const lastDay = new Date(year, month + 1, 0).getDate();

  /* =========================
     EMPTY START CELLS
  ========================= */

  for (let i = 0; i < firstDay; i++) {

    const empty = document.createElement("div");
    empty.className = "empty-day";

    calendar.appendChild(empty);
  }

  /* =========================
     DAYS
  ========================= */

  for (let d = 1; d <= lastDay; d++) {

    const date = new Date(year, month, d);

    const iso = date.toISOString().split("T")[0];

    const items = (dashboardData.assignments || [])
      .filter(a => a.deadline?.startsWith(iso));

    const div = document.createElement("div");

    div.classList.add("day");

    /* =========================
       STATUS COLORS
    ========================= */

    let cls = "";

    const oneWeekAhead = new Date(
      today.getTime() + (7 * 24 * 60 * 60 * 1000)
    );

    if (items.some(i => i.status === "MARKED")) {

      cls = "green";

    } else if (items.some(i => i.status === "SUBMITTED")) {

      cls = "grey";

    } else if (
      items.some(i =>
        i.status === "UNSUBMITTED" &&
        new Date(i.deadline) <= oneWeekAhead
      )
    ) {

      cls = "red";

    } else if (items.length > 0) {

      cls = "orange";
    }

    if (cls) {
      div.classList.add(cls);
    }

    div.innerHTML = `
      <div class="day-number">${d}</div>
    `;

    div.onclick = () => showDay(items, iso);

    calendar.appendChild(div);
  }
}

/* =========================
   DAY DETAILS
========================= */

function showDay(items, date) {

  const panel = document.getElementById("selectedDay");

  if (!panel) return;

  if (!items.length) {

    panel.innerHTML = `
      <div class="empty-selection">
        No assignments for <b>${date}</b>
      </div>
    `;

    return;
  }

  panel.innerHTML = `
    <div class="selected-title">
      Assignments for ${date}
    </div>

    ${items.map(i => `

      <div class="assignment-item">

        <div class="assignment-title">
          ${i.title}
        </div>

        <div class="assignment-module">
          ${i.module}
        </div>

        <div class="assignment-meta">
          Credits: ${i.credits || 0}
        </div>

        <span class="badge ${getBadge(i.status)}">
          ${formatStatus(i.status)}
        </span>

      </div>

    `).join("")}
  `;
}

function getBadge(status) {

  if (status === "MARKED") {
    return "badge-green";
  }

  if (status === "SUBMITTED") {
    return "badge-grey";
  }

  if (status === "UNSUBMITTED") {
    return "badge-red";
  }

  return "badge-grey";
}

function formatStatus(status) {

  if (status === "MARKED") {
    return "Marked";
  }

  if (status === "SUBMITTED") {
    return "Pending Marking";
  }

  if (status === "UNSUBMITTED") {
    return "Not Submitted";
  }

  return status;
}

/* =========================
   WARNINGS
========================= */

function renderWarnings() {

  const box = document.getElementById("warnings");

  if (!box) return;

  const assignments = dashboardData.assignments || [];

  const avg = Number(dashboardData.avgGrade || 0);

  const missedAssignments = assignments.filter(a =>
    a.status === "UNSUBMITTED" &&
    new Date(a.deadline) < new Date()
  );

  const pendingAssignments = assignments.filter(a =>
    a.status === "SUBMITTED"
  );

  const warnings = [];

  /* =========================
     LOW AVERAGE WARNING
  ========================= */

  if (avg < 50) {

    warnings.push(`
      <div class="warning-card critical">
        <div class="warning-title">
          Average Grade Warning
        </div>

        <div class="warning-text">
          Your current average is
          <b>${avg.toFixed(1)}%</b>.
          You may be at risk of failing modules.
        </div>
      </div>
    `);
  }

  /* =========================
     MISSED ASSIGNMENTS
  ========================= */

  if (missedAssignments.length > 0) {

    warnings.push(`
      <div class="warning-card danger">
        <div class="warning-title">
          Missed Deadlines
        </div>

        <div class="warning-text">
          ${missedAssignments.length}
          assignment(s) were not submitted before the deadline.
        </div>
      </div>
    `);
  }

  /* =========================
     PENDING MARKING
  ========================= */

  if (pendingAssignments.length > 0) {

    warnings.push(`
      <div class="warning-card pending">
        <div class="warning-title">
          Pending Marking
        </div>

        <div class="warning-text">
          ${pendingAssignments.length}
          assignment(s) are waiting to be marked.
        </div>
      </div>
    `);
  }

  /* =========================
     NO WARNINGS
  ========================= */

  if (warnings.length === 0) {

    warnings.push(`
      <div class="success-card">
        Everything looks good 👍
      </div>
    `);
  }

  box.innerHTML = warnings.join("");
}

/* =========================
   PROGRESS CHART
========================= */

function renderChart() {

  const assignments = dashboardData.assignments || [];

  let markedCredits = 0;
  let pendingCredits = 0;
  let futureCredits = 0;

  const now = new Date();

  assignments.forEach(a => {

    const deadline = new Date(a.deadline);

    const credits = Number(a.credits || 0);

    if (a.status === "MARKED") {

      markedCredits += credits;

    } else if (a.status === "SUBMITTED") {

      pendingCredits += credits;

    } else if (
      a.status === "UNSUBMITTED" &&
      deadline > now
    ) {

      futureCredits += credits;
    }
  });

  if (chart) {
    chart.destroy();
  }

  chart = new Chart(
    document.getElementById("progressChart"),
    {
      type: "pie",

      data: {

        labels: [
          "Completed",
          "Pending Marking",
          "Future Work"
        ],

        datasets: [{
          data: [
            markedCredits,
            pendingCredits,
            futureCredits
          ],

          backgroundColor: [
            "#22c55e",
            "#94a3b8",
            "#fb923c"
          ],

          borderWidth: 0
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
    }
  );
}