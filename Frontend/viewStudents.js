const tableBody = document.getElementById("studentsTableBody");

// LOAD STUDENTS
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

    // NO STUDENTS
    if (students.length === 0) {

      tableBody.innerHTML = `
        <tr>
          <td colspan="6" class="loading">
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
      `;

      tableBody.appendChild(row);
    });

  } catch (error) {

    console.error(error);

    tableBody.innerHTML = `
      <tr>
        <td colspan="6" class="error-message">
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

// LOAD DATA
loadStudents();