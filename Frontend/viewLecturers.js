const lecturersTableBody = document.getElementById("lecturersTableBody");

// =========================
// LOAD LECTURERS
// =========================
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
      lecturersTableBody.innerHTML =
        `<tr><td colspan="6">No lecturers found</td></tr>`;
      return;
    }

    lecturers.forEach(lecturer => {

      const modulesHTML = lecturer.modules && lecturer.modules.length > 0
        ? lecturer.modules.map(m =>
            `<span class="module-badge">${m.code} - ${m.name}</span>`
          ).join("")
        : `<span class="empty">No modules</span>`;

      const row = `
        <tr>
          <td>${lecturer.id}</td>
          <td>${lecturer.firstName}</td>
          <td>${lecturer.lastName}</td>
          <td>${lecturer.username}</td>
          <td>${lecturer.email}</td>
          <td class="modules-cell">${modulesHTML}</td>
        </tr>
      `;

      lecturersTableBody.innerHTML += row;
    });

  } catch (error) {
    console.error(error);
    lecturersTableBody.innerHTML =
      `<tr><td colspan="6" class="error-message">Failed to load lecturers</td></tr>`;
  }
}

// =========================
// CREATE MODULE
// =========================
async function createModule() {

  const module = {
    code: document.getElementById("moduleCode").value,
    name: document.getElementById("moduleName").value,
    credits: parseInt(document.getElementById("moduleCredits").value)
  };

  try {

    const response = await fetch(
      "https://automative-graiding-system.onrender.com/api/module/addModule",
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(module)
      }
    );

    if (!response.ok) {
      const error = await response.text();
      throw new Error(error);
    }

    alert("Module created successfully!");

    closeCreateModuleModal();

    // optionally refresh if needed
    // loadLecturers();

  } catch (error) {
    console.error(error);
    alert("Error: " + error.message);
  }
}

// =========================
// MODAL CONTROL
// =========================
function openCreateModuleModal() {
  document.getElementById("createModuleModal").style.display = "block";
}

function closeCreateModuleModal() {
  document.getElementById("createModuleModal").style.display = "none";
}

// =========================
// NAVIGATION
// =========================
function goBack() {
  window.location.href = "adminDashboard.html";
}

// =========================
// INIT
// =========================
loadLecturers();