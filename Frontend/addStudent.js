document.getElementById("addStudentForm").addEventListener("submit", async function (e) {
  e.preventDefault();

  const student = {
    id: document.getElementById("id").value,
    firstName: document.getElementById("firstName").value,
    lastName: document.getElementById("lastName").value,
    username: document.getElementById("username").value,
    email: document.getElementById("email").value
  };

  try {
    const response = await fetch("https://automative-graiding-system.onrender.com/api/student/addStudent", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(student)
    });

    if (response.ok) {
      alert("Student added successfully!");
      document.getElementById("addStudentForm").reset();
    } else {
      alert("Failed to add student.");
    }

  } catch (error) {
    console.error(error);
    alert("Error connecting to server.");
  }
});

// BACK NAVIGATION
function goBack() {
  window.location.href = "admin-dashboard.html";
}