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

  const lecturerNameElement =
    document.getElementById("lecturerName");

  if (lecturerNameElement) {
    lecturerNameElement.textContent =
      `${lecturer.firstName} ${lecturer.lastName}`;
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

    const response = await fetch(
      `https://automative-graiding-system.onrender.com/api/module/getLecturerModules/${lecturer.id}`
    );

    if (!response.ok) {
      throw new Error("Failed to load modules");
    }

    modules = await response.json();

    const select =
      document.getElementById("moduleSelect");

    select.innerHTML = "";

    if (modules.length === 0) {
      select.innerHTML =
        `<option value="">No modules assigned</option>`;
      return;
    }

    modules.forEach(module => {

      const option = document.createElement("option");

      const totalCredits = module.totalCredits || 0;
      const usedCredits = module.usedCredits || 0;
      const availableCredits = totalCredits - usedCredits;

      option.value = module.code;

      option.textContent =
        `${module.code} - ${module.name} (Available Credits: ${availableCredits})`;

      option.dataset.available = availableCredits;

      select.appendChild(option);
    });

    updateAvailableCredits();

  } catch (error) {

    console.error(error);
    alert("Failed to load lecturer modules");
  }
}


// =========================
// UPDATE AVAILABLE CREDITS
// =========================

function updateAvailableCredits() {

  const select =
    document.getElementById("moduleSelect");

  const selectedOption =
    select.options[select.selectedIndex];

  if (!selectedOption) return;

  const available =
    parseInt(selectedOption.dataset.available || 0);

  document.getElementById("availableCredits").value =
    available;

  // optional UX improvement: enforce max
  document.getElementById("assignmentCredits").max =
    available;
}


// =========================
// SUBMIT ASSIGNMENT
// =========================

async function submitAssignment(event) {

  event.preventDefault();

  const moduleCode =
    document.getElementById("moduleSelect").value;

  const credits =
    parseInt(document.getElementById("assignmentCredits").value);

  const deadline =
    document.getElementById("deadline").value;

  const taskDescription =
    document.getElementById("taskDescription").value;

  const markingCriteria =
    document.getElementById("markingCriteria").value;

  const pdfFile =
    document.getElementById("pdfFile").files[0];

  const availableCredits = parseInt(
    document.getElementById("availableCredits").value
  );

  // =========================
  // BASIC VALIDATION
  // =========================

  if (
    !moduleCode ||
    !credits ||
    !deadline ||
    !taskDescription ||
    !markingCriteria
  ) {
    alert("Please fill all required fields.");
    return;
  }

  if (isNaN(credits) || credits <= 0) {
    alert("Credits must be greater than 0.");
    return;
  }

  if (credits > availableCredits) {
    alert(
      `Assignment exceeds available credits (${availableCredits}).`
    );
    return;
  }

  // =========================
  // PDF OPTIONAL (FIXED)
  // =========================
  // NO validation required — file is optional

  try {

    const formData = new FormData();

    formData.append("moduleCode", moduleCode);
    formData.append("credits", credits);
    formData.append("deadline", deadline);
    formData.append("taskDescription", taskDescription);
    formData.append("markingCriteria", markingCriteria);

    // only attach file if exists
    if (pdfFile) {
      formData.append("file", pdfFile);
    }

    const response = await fetch(
      "https://automative-graiding-system.onrender.com/api/assignment/create",
      {
        method: "POST",
        body: formData
      }
    );

    const text = await response.text();

    if (!response.ok) {
      throw new Error(text);
    }

    alert("Assignment created successfully!");

    document.getElementById("assignmentForm").reset();

    await loadModules();

  } catch (error) {

    console.error(error);
    alert(error.message || "Error creating assignment.");
  }
}


// =========================
// NAVIGATION
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


// =========================
// LOGOUT
// =========================

function logout() {
  localStorage.removeItem("user");
  localStorage.removeItem("role");
  window.location.href = "login.html";
}