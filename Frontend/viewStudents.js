const tableBody = document.getElementById("studentsTableBody");

async function loadStudents() {

  try {

    const response = await fetch(
      "https://automative-graiding-system.onrender.com/api/student/getStudents"
    );

    if (!response.ok) {
      throw new Error("Failed to fetch students");
    }

    const students = await response.json();

    tableBody.innerHTML = "";

    if (students.length === 0) {

      tableBody.innerHTML = `
        <tr>
          <td colspan="7" class="loading">
            No students found.
          </td>
        </tr>
      `;

      return;
    }

    // DISPLAY STUDENTS
    students.forEach(student => {

      const row = document.createElement("tr");

      // MODULES HTML
      let modulesHtml = "";

      if (student.modules && student.modules.length > 0) {

        student.modules.forEach(module => {

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

      row.innerHTML = `
        <td>${student.id}</td>
        <td>${student.firstName}</td>
        <td>${student.lastName}</td>
        <td>${student.username}</td>
        <td>${student.email}</td>

        <td>
          <div class="modules-cell">
            ${modulesHtml}
          </div>
        </td>

        <td>
          <button class="register-btn"
                  onclick="openModuleModal(${student.id})">

            Register For Module

          </button>
        </td>
      `;

      tableBody.appendChild(row);
    });

  } catch (error) {

    console.error(error);

    tableBody.innerHTML = `
      <tr>
        <td colspan="7" class="error-message">
          Error loading students.
        </td>
      </tr>
    `;
  }
}


// BACK BUTTON

function goBack() {

  window.location.href = "admin-dashboard.html";
}


// LOAD STUDENTS

loadStudents();


// ======================================
// REGISTERING STUDENT ON MODULES
// ======================================

let selectedStudentId = null;


async function openModuleModal(studentId) {

  selectedStudentId = studentId;

  const modal = document.getElementById("moduleModal");

  const modulesList = document.getElementById("modulesList");

  modal.style.display = "block";

  try {

    const response = await fetch(
      `https://automative-graiding-system.onrender.com/api/module/getAvailableModules/${studentId}`
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

            <input type="checkbox"
                   value="${module.code}">

            <strong>${module.code}</strong>
            - ${module.name}

          </label>

        </div>
      `;
    });

  } catch (error) {

    console.error(error);

    modulesList.innerHTML =
      "<p>Error loading modules.</p>";
  }
}


function closeModal() {

  document.getElementById("moduleModal").style.display = "none";
}


async function submitModuleRegistration() {

  const selectedModules = [];

  document.querySelectorAll(
    '#modulesList input[type="checkbox"]:checked'
  ).forEach(checkbox => {

    selectedModules.push(checkbox.value);
  });

  if (selectedModules.length === 0) {

    alert("Select at least one module.");

    return;
  }

  try {

    const response = await fetch(
      "https://automative-graiding-system.onrender.com/api/registrations/sync",
      {
        method: "POST",

        headers: {
          "Content-Type": "application/json"
        },

        body: JSON.stringify({
          studentId: selectedStudentId,
          modules: selectedModules
        })
      }
    );

    if (!response.ok) {

      throw new Error("Registration failed");
    }

    alert("Student registered successfully!");

    closeModal();

    loadStudents();

  } catch (error) {

    console.error(error);

    alert("Error registering modules.");
  }
}


// ======================================
// CREATE MODULE MODAL
// ======================================

function openCreateModuleModal() {

  document.getElementById(
    "createModuleModal"
  ).style.display = "block";
}


function closeCreateModuleModal() {

  document.getElementById(
    "createModuleModal"
  ).style.display = "none";
}


// ======================================
// CREATE MODULE
// ======================================

async function createModule() {

  const code = document.getElementById("moduleCode")
    .value
    .trim();

  const name = document.getElementById("moduleName")
    .value
    .trim();

  const credits = parseInt(
    document.getElementById("moduleCredits").value
  );

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
          code: code,
          name: name,
          credits: credits
        })
      }
    );

    if (!response.ok) {

      throw new Error("Failed to create module");
    }

    alert("Module created successfully!");

    closeCreateModuleModal();

    // CLEAR FORM

    document.getElementById("moduleCode").value = "";

    document.getElementById("moduleName").value = "";

    document.getElementById("moduleCredits").value = "";

  } catch (error) {

    console.error(error);

    alert("Error creating module.");
  }
}