/* =========================
   CONFIG + USER
========================= */

const API_BASE = "https://automative-graiding-system.onrender.com/api";

const lecturer = JSON.parse(localStorage.getItem("user"));
const lecturerId = lecturer?.id;

let dashboardData = null;
let selectedModule = null;

let moduleChart = null;
let submissionChart = null;

/* =========================
   INIT
========================= */

document.addEventListener("DOMContentLoaded", async () => {
  if (!lecturerId) {
    window.location.href = "login.html";
    return;
  }

  await loadDashboard();
});

/* =========================
   MAIN API CALL
========================= */

async function loadDashboard() {
  try {
    const cached = localStorage.getItem("lecturer_dashboard");

    if (cached) {
      dashboardData = JSON.parse(cached);
    }

    const res = await fetch(
      `${API_BASE}/statistics/lecturer/${lecturerId}`,
      { credentials: "include" }
    );

    if (!res.ok) throw new Error("Failed to load dashboard");

    dashboardData = await res.json();

    localStorage.setItem(
      "lecturer_dashboard",
      JSON.stringify(dashboardData)
    );

    renderAll();

  } catch (err) {
    console.error(err);
    alert("Failed to load lecturer dashboard");
  }
}

/* =========================
   MASTER RENDER
========================= */

function renderAll() {
  renderActivityFeed();
  renderAtRiskStudents();
  renderModules();
  renderMarkingQueue();
}

/* =========================
   1. ACTIVITY FEED
========================= */

function renderActivityFeed() {
  const container = document.getElementById("activityList");
  container.innerHTML = "";

  dashboardData.activityFeed
    .sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp))
    .forEach(item => {

      const timeAgo = formatTimeAgo(item.timestamp);

      container.innerHTML += `
        <div class="feed-item">
          <b>${item.studentName}</b> submitted <b>${item.assignmentTitle}</b>
          <br>
          <small>${timeAgo}</small>
        </div>
      `;
    });
}

/* =========================
   2. AT RISK STUDENTS
========================= */

function renderAtRiskStudents() {
  const container = document.getElementById("riskList");
  container.innerHTML = "";

  dashboardData.atRiskStudents.forEach(s => {

    let label = "";
    let colorClass = "";

    if (s.avg < 30) {
      label = "🔴 Critical";
      colorClass = "badge-red";
    } else if (s.avg < 45) {
      label = "🟠 At risk";
      colorClass = "badge-orange";
    } else if (s.avg < 50) {
      label = "🟡 Needs attention";
      colorClass = "badge-yellow";
    }

    container.innerHTML += `
      <div class="feed-item">
        <b>${s.name}</b> (${s.email})
        <br>
        <span class="${colorClass}">${label}</span>
      </div>
    `;
  });
}

/* =========================
   3. MODULES + SWITCHING
========================= */

function renderModules() {
  const selector = document.getElementById("moduleSelector");

  selector.innerHTML = "";

  dashboardData.modules.forEach(m => {
    selector.innerHTML += `
      <option value="${m.code}">
        ${m.code}
      </option>
    `;
  });

  selectedModule = dashboardData.modules[0];

  selector.onchange = (e) => {
    selectedModule = dashboardData.modules.find(
      m => m.code === e.target.value
    );

    updateCharts();
  };

  updateCharts();
}

/* =========================
   4. CHARTS (PIE)
========================= */

function updateCharts() {

  if (!selectedModule) return;

  const module = selectedModule;

  // destroy old charts
  if (moduleChart) moduleChart.destroy();
  if (submissionChart) submissionChart.destroy();

  // MODULE OVERVIEW PIE
  moduleChart = new Chart(
    document.getElementById("moduleChart"),
    {
      type: "pie",
      data: {
        labels: ["Submission Rate", "Missing"],
        datasets: [{
          data: [
            module.submissionRate,
            100 - module.submissionRate
          ]
        }]
      }
    }
  );

  // MARKING STATUS PIE
  submissionChart = new Chart(
    document.getElementById("submissionChart"),
    {
      type: "pie",
      data: {
        labels: ["Marked", "Unmarked"],
        datasets: [{
          data: [
            module.markedCount,
            module.unmarkedCount
          ]
        }]
      }
    }
  );
}

/* =========================
   5. MARKING QUEUE TIMELINE
========================= */

function renderMarkingQueue() {
  const container = document.getElementById("markingTimeline");

  container.innerHTML = `<div class="timeline"></div>`;
  const timeline = container.querySelector(".timeline");

  const sorted = dashboardData.markingQueue
    .sort((a, b) => new Date(a.deadline) - new Date(b.deadline));

  sorted.forEach(item => {

    const isLate = new Date(item.deadline) < new Date();

    timeline.innerHTML += `
      <div class="timeline-item ${isLate ? "late" : ""}">
        <b>${item.title}</b>
        <br>
        Unmarked: ${item.unmarkedCount}
        <br>
        Deadline: ${formatDate(item.deadline)}
      </div>
    `;
  });
}

/* =========================
   6. TIME HELPERS
========================= */

function formatTimeAgo(date) {
  const diff = Date.now() - new Date(date);
  const mins = Math.floor(diff / 60000);

  if (mins < 60) return `${mins} min ago`;

  const hours = Math.floor(mins / 60);
  return `${hours} hours ago`;
}

function formatDate(date) {
  return new Date(date).toLocaleString();
}
