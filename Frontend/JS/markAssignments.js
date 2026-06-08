const API_BASE =
  "https://automative-graiding-system.onrender.com/api";

const lecturer =
  JSON.parse(localStorage.getItem("user"));

const assignmentsTableBody =
  document.getElementById("assignmentsTableBody");

/* =========================
   CACHE
========================= */

// assignmentId -> submissions[]
const submissionsCache = {};

// currently opened assignment
let expandedAssignmentId = null;

/* =========================
   INIT
========================= */

document.addEventListener("DOMContentLoaded", () => {

  loadAssignments();

});

/* =========================
   LOAD ASSIGNMENTS
========================= */

async function loadAssignments() {

  try {

    const response = await fetch(
      `${API_BASE}/assignment/lecturer/getAssignments/${lecturer.id}`,
      {
        method: "GET",
        credentials: "include"
      }
    );

    if (!response.ok) {
      throw new Error("Failed to load assignments");
    }

    const assignments =
      await response.json();

    renderAssignments(assignments);

  } catch (err) {

    console.error(err);

    assignmentsTableBody.innerHTML = `
      <tr>
        <td colspan="6" class="loading">
          Failed to load assignments
        </td>
      </tr>
    `;
  }
}

/* =========================
   RENDER ASSIGNMENTS
========================= */

function renderAssignments(assignments) {

  assignmentsTableBody.innerHTML = "";

  if (!assignments || assignments.length === 0) {

    assignmentsTableBody.innerHTML = `
      <tr>
        <td colspan="6" class="loading">
          No assignments found
        </td>
      </tr>
    `;

    return;
  }

  assignments.forEach(assignment => {

    const row =
      document.createElement("tr");

    row.className =
      "assignment-row";

    row.innerHTML = `
      <td>${assignment.moduleCode}</td>
      <td>${assignment.moduleName}</td>
      <td>${assignment.title}</td>
      <td>${formatDate(assignment.deadline)}</td>
      <td>${assignment.numberOfSubmissions}</td>
      <td>${assignment.numberOfUnmarkedSubmissions}</td>
    `;

    row.addEventListener("click", () => {

      toggleSubmissions(
        assignment.id,
        row
      );

    });

    assignmentsTableBody.appendChild(row);

  });

}

/* =========================
   TOGGLE SUBMISSIONS
========================= */

async function toggleSubmissions(
  assignmentId,
  parentRow
) {

  // close current
  if (expandedAssignmentId === assignmentId) {

    removeSubmissionDropdown();

    expandedAssignmentId = null;

    return;
  }

  // remove old dropdown
  removeSubmissionDropdown();

  expandedAssignmentId =
    assignmentId;

  // create dropdown row
  const dropdownRow =
    document.createElement("tr");

  dropdownRow.id =
    "submissionDropdownRow";

  dropdownRow.innerHTML = `
    <td colspan="6" class="submission-wrapper">

      <div class="submission-container">

        <div class="loading">
          Loading submissions...
        </div>

      </div>

    </td>
  `;

  parentRow.insertAdjacentElement(
    "afterend",
    dropdownRow
  );

  try {

    /* =========================
       CACHE HIT
    ========================= */

    if (submissionsCache[assignmentId]) {

      renderSubmissions(
        submissionsCache[assignmentId]
      );

      return;
    }

    /* =========================
       FETCH SUBMISSIONS
    ========================= */

    const response = await fetch(
      `${API_BASE}/assignment/getSubmissions/${assignmentId}`,
      {
        method: "GET",
        credentials: "include"
      }
    );

    if (!response.ok) {
      throw new Error("Failed to fetch submissions");
    }

    const submissions =
      await response.json();

    // save in cache
    submissionsCache[assignmentId] =
      submissions;

    renderSubmissions(submissions);

  } catch (err) {

    console.error(err);

    const container =
      document.querySelector(
        ".submission-container"
      );

    if (container) {

      container.innerHTML = `
        <div class="submission-empty">
          Failed to load submissions
        </div>
      `;
    }
  }
}

/* =========================
   RENDER SUBMISSIONS
========================= */

function renderSubmissions(submissions) {

  const container =
    document.querySelector(".submission-container");

  if (!container) return;

  if (!submissions || submissions.length === 0) {
    container.innerHTML = `
      <div class="submission-empty">
        No submissions found
      </div>
    `;
    return;
  }

  let html = `
    <table class="submission-table">

      <thead>
        <tr>
          <th>Student Name</th>
          <th>Submitted At</th>
          <th>Mark</th>
          <th>Action</th>
        </tr>
      </thead>

      <tbody>
  `;

  submissions.forEach(submission => {

    const hasMark =
      submission.mark !== null &&
      submission.mark !== undefined;

    html += `
      <tr>

        <td>
          ${submission.studentName}
        </td>

        <td>
          ${formatDateTime(submission.submittedAt)}
        </td>

        <td class="${hasMark ? "marked" : "unmarked"}">
          ${
            hasMark
              ? submission.mark
              : "Not marked"
          }
        </td>

        <td>
          <button
            class="view-btn"
            onclick="event.stopPropagation(); openSubmissionModal(${submission.id})"
          >
            View Submission
          </button>
        </td>

      </tr>
    `;
  });

  html += `
      </tbody>
    </table>
  `;

  container.innerHTML = html;
}

/* =========================
   REMOVE DROPDOWN
========================= */

function removeSubmissionDropdown() {

  const existing =
    document.getElementById(
      "submissionDropdownRow"
    );

  if (existing) {
    existing.remove();
  }
}

/* =========================
   OPEN MODAL
========================= */

function openSubmissionModal(submissionId) {

  document.getElementById("modalTitle").innerText =
    `Submission #${submissionId}`;

  document.getElementById("submissionModal").style.display =
    "flex";
}

/* =========================
   CLOSE MODAL
========================= */

function closeModal() {

  document.getElementById("submissionModal").style.display =
    "none";
}

/* =========================
   FORMAT DATE
========================= */

function formatDate(dateStr) {

  if (!dateStr) return "-";

  return new Date(dateStr)
    .toLocaleDateString("en-GB");
}

function formatDateTime(dateStr) {

  if (!dateStr) return "-";

  return new Date(dateStr)
    .toLocaleString("en-GB");
}

/* =========================
   CLOSE MODAL ON OUTSIDE CLICK
========================= */

window.onclick = function(event) {

  const modal =
    document.getElementById("submissionModal");

  if (event.target === modal) {
    closeModal();
  }
};