const API_BASE = "https://automative-graiding-system.onrender.com/api";

let assignments = [];
let currentAssignment = null;

const student =
  JSON.parse(localStorage.getItem("user"));

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

    const res = await fetch(
      `${API_BASE}/assignment/student/getAssignments/${student.id}`,
      {
        method: "GET",
        credentials: "include"
      }
    );

    if (!res.ok) {
      throw new Error("Failed to fetch assignments");
    }

    assignments = await res.json();

    renderTables();

  } catch (err) {

    console.error("Error loading assignments:", err);

    alert("Failed to load assignments");
  }
}

/* =========================
   RENDER TABLES
========================= */

function renderTables() {

  const unsubmittedTable =
    document.getElementById("unsubmittedTable");

  const submittedTable =
    document.getElementById("submittedTable");

  unsubmittedTable.innerHTML = "";
  submittedTable.innerHTML = "";

  assignments.forEach(a => {

    const isSubmitted =
      a.submitted === true;

    if (isSubmitted) {

      submittedTable.appendChild(
        createSubmittedRow(a)
      );

    } else {

      unsubmittedTable.appendChild(
        createUnsubmittedRow(a)
      );
    }
  });
}

/* =========================
   UNSUBMITTED ROW
========================= */

function createUnsubmittedRow(a) {

  const tr = document.createElement("tr");

  tr.innerHTML = `
    <td>${a.moduleCode}</td>
    <td>${a.moduleName}</td>
    <td>${a.title}</td>
    <td>${a.credits}</td>

    <td class="${getDeadlineClass(a.deadline)}">
      ${formatDate(a.deadline)}
    </td>

    <td>
      <button
        class="action"
        onclick="openAssignment(${a.id})"
      >
        View Assignment
      </button>
    </td>
  `;

  return tr;
}

/* =========================
   SUBMITTED ROW
========================= */

function createSubmittedRow(a) {

  const tr = document.createElement("tr");

  tr.innerHTML = `
    <td>${a.moduleCode}</td>
    <td>${a.moduleName}</td>
    <td>${a.title}</td>
    <td>${a.credits}</td>

    <td>${formatDate(a.deadline)}</td>

    <td>
      ${formatDate(a.submittedDate)}
    </td>

    <td>
      ${a.mark ?? "Not graded"}
    </td>

    <td>
      <button
        class="action"
        onclick="openAssignment(${a.id})"
      >
        View Submission
      </button>
    </td>
  `;

  return tr;
}

/* =========================
   DEADLINE FORMATTING
========================= */

function getDeadlineClass(deadline) {

  const now = new Date();

  const due =
    new Date(deadline);

  const diffDays =
    (due - now) /
    (1000 * 60 * 60 * 24);

  if (diffDays < 3) {
    return "deadline-red";
  }

  if (diffDays < 7) {
    return "deadline-orange";
  }

  return "";
}

function formatDate(dateStr) {

  if (!dateStr) {
    return "-";
  }

  return new Date(dateStr)
    .toLocaleDateString("en-GB");
}

/* =========================
   OPEN MODAL
========================= */

async function openAssignment(id) {

  try {

    const res = await fetch(
      `${API_BASE}/assignment/getAssignmentDetails/${id}`,
      {
        method: "GET",
        credentials: "include"
      }
    );

    if (!res.ok) {
      throw new Error(
        "Failed to fetch assignment details"
      );
    }

    currentAssignment =
      await res.json();

    /* =========================
       SET CONTENT
    ========================= */

    document.getElementById("modalTitle")
      .innerText = currentAssignment.title;

    document.getElementById("modalDescription")
      .innerText = currentAssignment.description;

    document.getElementById("modalCriteria")
      .innerText = currentAssignment.markingCriteria;

    document.getElementById("modalCredits")
      .innerText = currentAssignment.credits;

    document.getElementById("modalDeadline")
      .innerText = formatDate(
        currentAssignment.deadline
      );

    renderFiles(
      currentAssignment.files || []
    );

    /* =========================
       TOGGLE SUBMIT / DELETE
    ========================= */

    const submitSection =
      document.getElementById("submitSection");

    const deleteSection =
      document.getElementById(
        "deleteSubmissionSection"
      );

    const submissionTitle =
      document.getElementById(
        "submissionTitle"
      );

    const isSubmitted =
      currentAssignment.submitted === true;

    if (isSubmitted) {

      submitSection.style.display =
        "none";

      deleteSection.style.display =
        "block";

      submissionTitle.innerText =
        "Submission Options";

    } else {

      submitSection.style.display =
        "block";

      deleteSection.style.display =
        "none";

      submissionTitle.innerText =
        "Submit Assignment";
    }

    /* =========================
       OPEN MODAL
    ========================= */

    document.getElementById(
      "assignmentModal"
    ).style.display = "flex";

  } catch (err) {

    console.error(err);

    alert("Failed to load assignment");
  }
}

/* =========================
   CLOSE MODAL
========================= */

function closeModal() {

  document.getElementById(
    "assignmentModal"
  ).style.display = "none";

  currentAssignment = null;

  document.getElementById(
    "submissionFile"
  ).value = "";
}

/* =========================
   FILE LIST
========================= */

function renderFiles(files) {

  const container =
    document.getElementById("modalFiles");

  container.innerHTML = "";

  if (!files || files.length === 0) {

    container.innerHTML =
      "<p>No attached files</p>";

    return;
  }

  const title =
    document.createElement("h4");

  title.innerText =
    "Assignment Files";

  container.appendChild(title);

  files.forEach(f => {

    const link =
      document.createElement("a");

    link.href = f.url;
    link.target = "_blank";

    link.innerText = f.name;

    link.style.display = "block";

    container.appendChild(link);
  });
}

/* =========================
   SUBMIT ASSIGNMENT
========================= */

async function submitAssignment() {

  const file =
    document.getElementById(
      "submissionFile"
    ).files[0];

  if (!file) {

    alert("Select a file first");

    return;
  }

  if (!currentAssignment) {
    return;
  }

  try {

    const formData =
      new FormData();

    formData.append("file", file);

    formData.append(
      "studentId",
      student.id
    );

    const res = await fetch(
      `${API_BASE}/assignment/submit/${currentAssignment.id}`,
      {
        method: "POST",
        credentials: "include",
        body: formData
      }
    );

    if (!res.ok) {

      const errorText =
        await res.text();

      console.error(
        "Backend error response:",
        errorText
      );

      throw new Error(errorText);
    }

    alert(
      "Assignment submitted successfully"
    );

    closeModal();

    await loadAssignments();

  } catch (err) {

    console.error(err);

    alert("Failed to submit assignment");
  }
}

/* =========================
   DELETE SUBMISSION
========================= */

async function deleteSubmission() {

  if (!currentAssignment) {
    return;
  }

  const confirmed = confirm(
    "Delete submission?"
  );

  if (!confirmed) {
    return;
  }

  try {

    const res = await fetch(
      `${API_BASE}/assignment/${currentAssignment.id}/submission`,
      {
        method: "DELETE",
        credentials: "include"
      }
    );

    if (!res.ok) {

      const errorText =
        await res.text();

      console.error(
        "Delete error:",
        errorText
      );

      throw new Error(errorText);
    }

    alert("Submission deleted");

    closeModal();

    await loadAssignments();

  } catch (err) {

    console.error(err);

    alert("Failed to delete submission");
  }
}

/* =========================
   OUTSIDE CLICK CLOSE
========================= */

window.onclick = function (event) {

  const modal =
    document.getElementById(
      "assignmentModal"
    );

  if (event.target === modal) {
    closeModal();
  }
};