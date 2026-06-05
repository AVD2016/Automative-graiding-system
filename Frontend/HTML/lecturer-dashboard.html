// =========================
// LOAD LECTURER INFO
// =========================

function loadLecturerInfo() {
  const lecturer = JSON.parse(localStorage.getItem("user"));

  if (!lecturer) {
    window.location.href = "login.html";
    return;
  }

  const fullName = `${lecturer.firstName} ${lecturer.lastName}`;
  document.getElementById("lecturerName").textContent = fullName;
}

// =========================
// VIEW MODULES
// =========================

function openViewModules() {
  window.location.href = "lecturer-modules.html";
}

// =========================
// CREATE ASSIGNMENT
// =========================

function openCreateAssignment() {
  window.location.href = "create-assignment.html";
}

// =========================
// MARK ASSIGNMENT
// =========================

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

// =========================
// LOAD LATEST SUBMISSIONS (PLACEHOLDER)
// =========================

async function loadLatestSubmissions() {
  const container = document.querySelector(".placeholder");

  try {
    const lecturer = JSON.parse(localStorage.getItem("user"));

    if (!lecturer) return;

    // Placeholder endpoint (you can replace later)
    const res = await fetch(
      `https://automative-graiding-system.onrender.com/api/submissions/latest/${lecturer.id}`
    );

    if (!res.ok) {
      container.innerHTML = "No submissions available.";
      return;
    }

    const submissions = await res.json();

    if (!submissions || submissions.length === 0) {
      container.innerHTML = "No submissions available.";
      return;
    }

    container.innerHTML = submissions
      .map(
        (s) => `
        <div style="padding:10px; border-bottom:1px solid #eee;">
          <strong>${s.assignmentTitle}</strong><br/>
          Student: ${s.studentName}<br/>
          Submitted: ${s.date}
        </div>
      `
      )
      .join("");

  } catch (err) {
    console.error(err);
    container.innerHTML = "Failed to load submissions.";
  }
}

// =========================
// INIT
// =========================

loadLecturerInfo();
loadLatestSubmissions();