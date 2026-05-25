// AUTH MODULE (SHARED ACROSS PAGES)
//
// HTTP request is used for login:
// POST /api/login

const loginForm = document.getElementById("loginForm");
const message = document.getElementById("message");
const loading = document.getElementById("loading");

loginForm.addEventListener("submit", async (e) => {
  e.preventDefault();

  message.textContent = "";
  loading.style.display = "block";

  const role = document.getElementById("role").value;
  const email = document.getElementById("email").value;
  const password = document.getElementById("password").value;

  const payload = {
    role,
    email,
    password
  };

  try {
    const res = await fetch("http://localhost:3000/api/login", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(payload)
    });

    const data = await res.json();

    loading.style.display = "none";

    if (res.ok) {
      // Store auth data
      localStorage.setItem("token", data.token);
      localStorage.setItem("role", data.role);

      // Redirect based on role
      if (data.role === "student") {
        window.location.href = "/student-dashboard.html";
      } else if (data.role === "lecturer") {
        window.location.href = "/lecturer-dashboard.html";
      }

    } else {
      message.textContent = data.message || "Login failed";
    }

  } catch (err) {
    loading.style.display = "none";
    message.textContent = "Server not reachable";
    console.error(err);
  }
if (localStorage.getItem("token")) {
  window.location.href = "/student-dashboard.html";
}
});