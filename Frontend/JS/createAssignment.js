// =========================
// STATE
// =========================

let lecturer = null;
let modules = [];

// =========================
// INIT
// =========================

document.addEventListener("DOMContentLoaded", () => {
  lecturer = JSON.parse(localStorage.getItem("user"));

  if (!lecturer) {
    window.location.href = "login.html";
    return;
  }

  loadModules();

  document
    .getElementById("moduleSelect")
    .addEventListener("change", updateAvailableCredits);

  document
    .getElementById("assignmentForm")
    .addEventListener("submit", submitAssignment);
});

// =========================
// LOAD MODULES
// =========================

async function loadModules() {
  try {
    const res = await fetch(
      `https://automative-graiding-system.onrender.com/api/module/getLecturerModules/${lecturer.id}`
    );

    if (!res.ok) throw new Error("Failed to load modules");

    modules = await res.json();

    const select = document.getElementById("moduleSelect");
    select.innerHTML = "";

    modules.forEach((m) => {
      const option = document.createElement("option");

      // expected backend fields:
      // code, name, totalCredits, usedCredits
      const available = (m.totalCredits || 0) - (m.usedCredits || 0);

      option.value = m.code;
      option.textContent = `${m.code} - ${m.name} (Available: ${available})`;

      option.dataset.available = available;

      select.appendChild(option);
    });

    updateAvailableCredits();
  } catch (err) {
    console.error(err);
    alert("Failed to load modules");
  }
}

// =========================
// UPDATE AVAILABLE CREDITS
// =========================

function updateAvailableCredits() {
  const select = document.getElementById("moduleSelect");
  const selected = select.options[select.selectedIndex];

  if (!selected) return;

  document.getElementById("availableCredits").value =
    selected.dataset.available || 0;
}

// =========================
// SUBMIT ASSIGNMENT
// =========================

async function submitAssignment(e) {
  e.preventDefault();

  const moduleCode = document.getElementById("moduleSelect").value;
  const credits = document.getElementById("assignmentCredits").value;
  const deadline = document.getElementById("deadline").value;
  const task = document.getElementById("taskDescription").value;
  const marking = document.getElementById("markingCriteria").value;
  const file = document.getElementById("pdfFile").files[0];

  // validation
  if (!moduleCode || !credits || !deadline || !task || !marking) {
    alert("Please fill all fields");
    return;
  }

  if (!file) {
    alert("Please upload a PDF file");
    return;
  }

  if (file.type !== "application/pdf") {
    alert("Only PDF files are allowed");
    return;
  }

  try {
    const formData = new FormData();

    formData.append("moduleCode", moduleCode);
    formData.append("lecturerId", lecturer.id);
    formData.append("credits", credits);
    formData.append("deadline", deadline);
    formData.append("taskDescription", task);
    formData.append("markingCriteria", marking);
    formData.append("file", file);

    const res = await fetch(
      "https://automative-graiding-system.onrender.com/api/assignment/create",
      {
        method: "POST",
        body: formData
      }
    );

    if (!res.ok) {
      throw new Error("Failed to create assignment");
    }

    alert("Assignment created successfully!");

    document.getElementById("assignmentForm").reset();
    updateAvailableCredits();
  } catch (err) {
    console.error(err);
    alert("Error creating assignment");
  }
}

// =========================
// NAV FUNCTIONS (FROM SIDEBAR)
// =========================

function openViewModules() {
  window.location.href = "lecturer-modules.html";
}

function openCreateAssignment() {
  window.location.href = "create-assignment.html";
}

function openMarkAssignment() {
  window.location.href = "mark-assignment.html";
}

function logout() {
  localStorage.removeItem("user");
  localStorage.removeItem("role");
  window.location.href = "login.html";
}