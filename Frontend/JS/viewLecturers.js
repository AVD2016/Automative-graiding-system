const lecturersTableBody = document.getElementById("lecturersTableBody");

// ======================================
// LOAD LECTURERS
// ======================================

async function loadLecturers() {

  try {

    const response = await fetch(
      "https://automative-graiding-system.onrender.com/api/lecturer/getLecturers"
    );

    if (!response.ok) {
      throw new Error("Failed to fetch lecturers");
    }

    const lecturers = await response.json();

    lecturersTableBody.innerHTML = "";

    if (lecturers.length === 0) {

      lecturersTableBody.innerHTML = `
        <tr>
          <td colspan="7" class="loading">
            No lecturers found.
          </td>
        </tr>
      `;
      return;
    }

    lecturers.forEach(lecturer => {

      let modulesHtml = "";

      if (lecturer.modules && lecturer.modules.length > 0) {

        lecturer.modules.forEach(module => {

          modulesHtml += `
            <div class="module-badge">
              ${module.code} - ${module.name}
            </div>
          `;
        });

      } else {

        modulesHtml = `
          <span class="empty-modules">
            No modules registered
          </span>
        `;
      }

      const row = document.createElement("tr");

      row.innerHTML = `
        <td>${lecturer.id}</td>
        <td>${lecturer.firstName}</td>
        <td>${lecturer.lastName}</td>
        <td>${lecturer.username}</td>
        <td>${lecturer.email}</td>

        <td>
          <div class="modules-cell">
            ${modulesHtml}
          </div>
        </td>

        <td>
          <button class="register-btn"
                  onclick="openModuleModal(${lecturer.id})">
            Register For Module
          </button>
        </td>
      `;

      lecturersTableBody.appendChild(row);
    });

  } catch (error) {

    console.error(error);

    lecturersTableBody.innerHTML = `
      <tr>
        <td colspan="7" class="error-message">
          Error loading lecturers.
        </td>
      </tr>
    `;
  }
}


// ======================================
// INIT
// ======================================

document.addEventListener("DOMContentLoaded", loadLecturers);

// Navigation

function openAddStudent() {
  window.location.href = "addStudent.html";
}


function openViewAllStudents() {
  window.location.href = "viewStudents.html";
}

function logout() {
  localStorage.clear();
  window.location.href = "/index.html";
}

function openAddNewLecturer(){
window.location.href = "addLecturer.html";
}

function openViewLecturers(){
  window.location.href = "viewLecturers.html"
}


// ======================================
// MODULE REGISTRATION
// ======================================

let selectedLecturerId = null;

async function openModuleModal(lecturerId) {

  selectedLecturerId = lecturerId;

  const modal = document.getElementById("moduleModal");
  const modulesList = document.getElementById("modulesList");

  modal.style.display = "block";

  try {

    const response = await fetch(
      `https://automative-graiding-system.onrender.com/api/module/getAvailableModulesLecturer/${lecturerId}`
    );

    if (!response.ok) {
      throw new Error("Failed to load modules");
    }

    const modules = await response.json();

    modulesList.innerHTML = "";

    modules.forEach(module => {

      modulesList.innerHTML += `
        <div class="module-option">
          <label>
            <input type="checkbox" value="${module.code}">
            <strong>${module.code}</strong> - ${module.name}
          </label>
        </div>
      `;
    });

  } catch (error) {

    console.error(error);

    modulesList.innerHTML = "<p>Error loading modules.</p>";
  }
}

function closeModal() {
  document.getElementById("moduleModal").style.display = "none";
}


// ======================================
// SAVE REGISTRATION
// ======================================

async function submitModuleRegistration() {

  const selectedModules = [];

  document.querySelectorAll(
    '#modulesList input[type="checkbox"]:checked'
  ).forEach(cb => {
    selectedModules.push(cb.value);
  });

  if (selectedModules.length === 0) {
    alert("Select at least one module.");
    return;
  }

  try {

    const response = await fetch(
      "https://automative-graiding-system.onrender.com/api/registrations/syncLecturer",
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          lecturerId: selectedLecturerId,
          modules: selectedModules
        })
      }
    );

    if (!response.ok) {
      throw new Error("Registration failed");
    }

    alert("Lecturer registered successfully!");

    closeModal();
    loadLecturers();

  } catch (error) {

    console.error(error);
    alert("Error registering modules.");
  }
}


// ======================================
// CREATE MODULE
// ======================================

function openCreateModuleModal() {
  document.getElementById("createModuleModal").style.display = "block";
}

function closeCreateModuleModal() {
  document.getElementById("createModuleModal").style.display = "none";
}

async function createModule() {

  const code = document.getElementById("moduleCode").value.trim();
  const name = document.getElementById("moduleName").value.trim();
  const credits = parseInt(document.getElementById("moduleCredits").value);

  if (!code || !name || !credits) {
    alert("Please fill all fields.");
    return;
  }

  try {

    const response = await fetch(
      "https://automative-graiding-system.onrender.com/api/module/createModule",
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          code,
          name,
          credits
        })
      }
    );

    if (!response.ok) {
      throw new Error("Failed to create module");
    }

    alert("Module created successfully!");

    closeCreateModuleModal();

    document.getElementById("moduleCode").value = "";
    document.getElementById("moduleName").value = "";
    document.getElementById("moduleCredits").value = "";

  } catch (error) {

    console.error(error);
    alert("Error creating module.");
  }
}


// ======================================
// MODAL CLOSE ON OUTSIDE CLICK (optional UX)
// ======================================

window.onclick = function (event) {

  const moduleModal = document.getElementById("moduleModal");
  const createModal = document.getElementById("createModuleModal");

  if (event.target === moduleModal) {
    moduleModal.style.display = "none";
  }

  if (event.target === createModal) {
    createModal.style.display = "none";
  }
};