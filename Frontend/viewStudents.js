const container = document.getElementById("studentsContainer");

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

    // CLEAR CONTAINER
    container.innerHTML = "";

    // NO STUDENTS
    if (students.length === 0) {
      container.innerHTML = "<p>No students found.</p>";
      return;
    }

    // DISPLAY EACH STUDENT
    students.forEach(student => {

      const card = document.createElement("div");
      card.className = "student-card";

      // MODULE LIST
      let modulesHtml = "";

      if (student.registered && student.registered.length > 0) {

        student.registered.forEach(registration => {

          if (registration.module) {

            modulesHtml += `
              <div class="module-item">
                <strong>${registration.module.code}</strong>
                - ${registration.module.name}
              </div>
            `;
          }
        });

      } else {

        modulesHtml = "<p>No registered modules.</p>";
      }

      // CARD HTML
      card.innerHTML = `
        <h2>${student.firstName} ${student.lastName}</h2>

        <div class="info">
          <strong>ID:</strong> ${student.id}
        </div>

        <div class="info">
          <strong>Username:</strong> ${student.username}
        </div>

        <div class="info">
          <strong>Email:</strong> ${student.email}
        </div>

        <div class="modules">
          <strong>Registered Modules:</strong>
          ${modulesHtml}
        </div>
      `;

      container.appendChild(card);
    });

  } catch (error) {

    console.error(error);

    container.innerHTML =
      "<p>Error loading students.</p>";
  }
}

// BACK BUTTON
function goBack() {
  window.location.href = "admin-dashboard.html";
}

// LOAD ON PAGE OPEN
loadStudents();