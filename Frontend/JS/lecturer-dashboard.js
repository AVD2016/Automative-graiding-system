
window.addEventListener("DOMContentLoaded", () => {

  loadLecturerInfo();
  loadLatestSubmissions();

});



// LOAD LOGGED-IN LECTURER


async function loadLecturerInfo() {

  try {

    // You should replace this with real auth/session endpoint
    const response = await fetch(
      "https://automative-graiding-system.onrender.com/api/lecturer/getCurrent"
    );

    if (!response.ok) {
      throw new Error("Failed to fetch lecturer");
    }

    const lecturer = await response.json();

    document.getElementById("lecturerName").textContent =
      `${lecturer.firstName} ${lecturer.lastName}`;

  } catch (error) {

    console.error(error);

    document.getElementById("lecturerName").textContent =
      "Lecturer";
  }
}

// LOAD LATEST SUBMISSIONS

async function loadLatestSubmissions() {

  const content = document.querySelector(".submissions-card");

  try {

    // Replace with your real backend endpoint later
    const response = await fetch(
      "https://automative-graiding-system.onrender.com/api/submissions/latest"
    );

    if (!response.ok) {
      return;
    }

    const submissions = await response.json();

    // Remove placeholder if exists
    const placeholder = document.querySelector(".placeholder");
    if (placeholder) placeholder.remove();

    if (!submissions || submissions.length === 0) {

      content.innerHTML += `
        <div class="placeholder">
          No submissions available yet.
        </div>
      `;

      return;
    }

    submissions.forEach(sub => {

      content.innerHTML += `
        <div style="
          margin-bottom:15px;
          padding:15px;
          border:1px solid #e5e7eb;
          border-radius:10px;
        ">

          <strong>${sub.assignmentTitle || "Assignment"}</strong><br>

          <span>Student: ${sub.studentName || "Unknown"}</span><br>

          <span>Module: ${sub.moduleCode || "N/A"}</span><br>

          <span>Submitted: ${sub.submittedAt || "Unknown"}</span>

        </div>
      `;
    });

  } catch (error) {

    console.error(error);
  }
}


// SIDEBAR NAVIGATION

function openViewModules() {

  window.location.href = "viewModules.html";
}


function openCreateAssignment() {

  window.location.href = "createAssignment.html";
}


function openMarkAssignment() {

  window.location.href = "markAssignment.html";
}


// LOGOUT

function logout() {

  localStorage.removeItem("token");

  window.location.href = "login.html";
}