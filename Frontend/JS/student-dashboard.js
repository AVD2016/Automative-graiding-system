const API_BASE = "https://automative-graiding-system.onrender.com/api";

let student = null;
let dashboardData = null;
let chart = null;

/* ========================= INIT ========================= */

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

/* ========================= LOAD BACKEND ========================= */

async function loadDashboard() {

  try {

    const res = await fetch(
      `${API_BASE}/statistics/student/${student.id}`,
      { credentials: "include" }
    );

    if (!res.ok) throw new Error("Failed to load student dashboard");

    dashboardData = await res.json();

    renderCalendar();
    renderWarnings();
    renderChart();

  } catch (err) {
    console.error(err);
    alert("Failed to load dashboard");
  }
}

/* ========================= CALENDAR ========================= */

function renderCalendar() {

  const calendar = document.getElementById("calendar");
  if (!calendar) return;

  calendar.innerHTML = "";

  const today = new Date();
  const year = today.getFullYear();
  const month = today.getMonth();

  const lastDay = new Date(year, month + 1, 0);

  for (let d = 1; d <= lastDay.getDate(); d++) {

    const date = new Date(year, month, d);
    const iso = date.toISOString().split("T")[0];

    const items = (dashboardData.assignments || [])
      .filter(a => a.deadline?.startsWith(iso));

    const div = document.createElement("div");
    div.classList.add("day");

    const now = new Date();

    /* ========================= COLOR RULES ========================= */

    let cls = "";

    if (items.some(i => i.status === "MARKED")) {
      cls = "green";

    } else if (items.some(i => i.status === "SUBMITTED")) {
      cls = "grey";

    } else if (
      items.some(i =>
        i.status === "UNSUBMITTED" &&
        new Date(i.deadline) < new Date(now.getTime() + 7 * 86400000)
      )
    ) {
      cls = "red";

    } else if (items.length > 0) {
      cls = "orange";
    }

    div.classList.add(cls);

    div.innerHTML = `<div class="day-number">${d}</div>`;

    div.onclick = () => showDay(items, iso);

    calendar.appendChild(div);
  }
}

/* ========================= DAY DETAILS ========================= */

function showDay(items, date) {

  const panel = document.getElementById("selectedDay");
  if (!panel) return;

  if (!items.length) {
    panel.innerHTML = `No assignments for <b>${date}</b>`;
    return;
  }

  panel.innerHTML =
    `<b>${date}</b><br><br>` +
    items.map(i => `
      <div>
        <b>${i.title}</b> (${i.module})<br>
        Credits: ${i.credits || 0}<br>
        <span class="badge ${getBadge(i.status)}">
          ${i.status}
        </span>
      </div><br>
    `).join("");
}

function getBadge(status) {
  if (status === "MARKED") return "badge-green";
  if (status === "SUBMITTED") return "badge-grey";
  if (status === "UNSUBMITTED") return "badge-red";
  return "badge-grey";
}

/* ========================= WARNINGS ========================= */

function renderWarnings() {

  const box = document.getElementById("warnings");
  if (!box) return;

  const assignments = dashboardData.assignments || [];

  const avg = dashboardData.avgGrade ?? 0;

  const missed = assignments.filter(a =>
    a.status === "UNSUBMITTED" &&
    new Date(a.deadline) < new Date()
  ).length;

  const pending = assignments.filter(a =>
    a.status === "SUBMITTED"
  ).length;

  const warnings = [];

  if (avg < 50) {
    warnings.push(`⚠ Average below 50% (${avg.toFixed(1)}%)`);
  }

  if (missed > 0) {
    warnings.push(`❌ ${missed} overdue unsubmitted assignments`);
  }

  if (pending > 0) {
    warnings.push(`⏳ ${pending} assignments pending marking`);
  }

  if (warnings.length === 0) {
    warnings.push("All good 👍");
  }

  box.innerHTML = warnings.map(w => `
    <div class="warning">${w}</div>
  `).join("");
}

/* ========================= PROGRESS CHART ========================= */

function renderChart() {

  const assignments = dashboardData.assignments || [];

  let markedCredits = 0;
  let pendingCredits = 0;
  let futureCredits = 0;

  const now = new Date();

  assignments.forEach(a => {

    const deadline = new Date(a.deadline);

    if (a.status === "MARKED") {
      markedCredits += a.credits || 0;

    } else if (a.status === "SUBMITTED") {
      pendingCredits += a.credits || 0;

    } else if (deadline > now) {
      futureCredits += a.credits || 0;
    }
  });

  if (chart) chart.destroy();

  chart = new Chart(document.getElementById("progressChart"), {
    type: "pie",
    data: {
      labels: ["Marked", "Pending Marking", "Future"],
      datasets: [{
        data: [
          markedCredits,
          pendingCredits,
          futureCredits
        ]
      }]
    }
  });
}