const form = document.getElementById("addLecturerForm");

form.addEventListener("submit", async (e) => {

  e.preventDefault();

  const lecturer = {
    id: parseInt(document.getElementById("id").value),
    firstName: document.getElementById("firstName").value,
    lastName: document.getElementById("lastName").value,
    username: document.getElementById("username").value,
    password: document.getElementById("password").value,
    email: document.getElementById("email").value
  };

  try {

    const response = await fetch(
      "https://automative-graiding-system.onrender.com/api/lecturer/addLecturer",
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(lecturer)
      }
    );

    if (!response.ok) {
      const error = await response.text();
      throw new Error(error);
    }

    alert("Lecturer added successfully!");

    form.reset();

  } catch (error) {

    console.error(error);

    alert("Error: " + error.message);
  }
});

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