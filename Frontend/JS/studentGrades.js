// STATE

const API_BASE =
  "https://automative-graiding-system.onrender.com/api";

let student = null;
let modules = [];

// INIT

document.addEventListener("DOMContentLoaded", () => {

  student = JSON.parse(localStorage.getItem("user"));

  if (!student) {
    window.location.href = "login.html";
    return;
  }

  document.getElementById("studentName").textContent =
    `${student.firstName} ${student.lastName}`;

  loadModules();
});

// LOAD MODULES

async function loadModules() {

  try {

    const res = await fetch(
      `${API_BASE}/statistics/student/grades/${student.id}`
    );

    if (!res.ok) throw new Error("Failed to load modules");

    modules = await res.json();

    renderAll();

  } catch (err) {

    console.error(err);

    document.getElementById("moduleBreakdown").innerHTML = `
      <tr>
        <td colspan="4" class="status-na">
          Failed to load analytics
        </td>
      </tr>
    `;
  }
}

// MAIN RENDER

function renderAll() {

  renderTable();
  renderSummary();
  renderInsights();
}

// STATUS LOGIC

function getStatus(avg) {

  if (avg === -1) return { text: "No Submissions", cls: "status-na" };

  if (avg < 40) return { text: "Critical", cls: "status-critical" };

  if (avg < 60) return { text: "Pass", cls: "status-pass" };

  if (avg < 75) return { text: "Strong Pass", cls: "status-strong" };

  if (avg < 90) return { text: "Merit", cls: "status-merit" };

  return { text: "Distinction", cls: "status-distinction" };
}

// TABLE RENDER

function renderTable() {

  const body = document.getElementById("moduleBreakdown");

  body.innerHTML = "";

  modules.forEach(m => {

    const avg = m.averageGrade ?? -1;
    const status = getStatus(avg);

    const tr = document.createElement("tr");

    tr.innerHTML = `
      <td>${m.code}</td>
      <td>${avg === -1 ? "N/A" : avg.toFixed(1) + "%"}</td>
      <td class="${status.cls}">${status.text}</td>
      <td>${m.assignmentCount ?? 0}</td>
    `;

    body.appendChild(tr);
  });
}

// SUMMARY METRICS

function renderSummary() {

  const validModules =
    modules.filter(m => m.averageGrade !== -1);

  const overallAvg =
    validModules.length === 0
      ? 0
      : validModules.reduce((a, b) => a + b.averageGrade, 0) / validModules.length;

  document.getElementById("overallAvg").textContent =
    `Overall Average: ${overallAvg.toFixed(1)}%`;

  document.getElementById("moduleCount").textContent =
    `Modules: ${modules.length}`;

  const completed = validModules.length;

  const completionRate =
    modules.length === 0
      ? 0
      : (completed / modules.length) * 100;

  document.getElementById("completionRate").textContent =
    `Completion Rate: ${completionRate.toFixed(1)}%`;

  let risk = "Low";

  const critical = validModules.filter(m => m.averageGrade < 40).length;
  const weak = validModules.filter(m => m.averageGrade < 60).length;

  if (critical > 0) risk = "High";
  else if (weak > 1) risk = "Medium";

  document.getElementById("riskLevel").textContent =
    `Risk Level: ${risk}`;
}

// insights

function renderInsights() {

  const valid = modules.filter(m => m.averageGrade !== -1);

  if (valid.length === 0) {

    document.getElementById("strongestModule").textContent =
      "Strongest Module: N/A";

    document.getElementById("weakestModule").textContent =
      "Weakest Module: N/A";

    return;
  }

  const sorted = [...valid].sort((a, b) => b.averageGrade - a.averageGrade);

  const best = sorted[0];
  const worst = sorted[sorted.length - 1];

  document.getElementById("strongestModule").textContent =
    `Strongest Module: ${best.code} (${best.averageGrade.toFixed(1)}%)`;

  document.getElementById("weakestModule").textContent =
    `Weakest Module: ${worst.code} (${worst.averageGrade.toFixed(1)}%)`;
}