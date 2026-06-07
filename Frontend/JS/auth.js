const loginForm = document.getElementById("loginForm");
const message = document.getElementById("message");
const loading = document.getElementById("loading");

/* =========================
   AUTO LOGIN CHECK (NEW)
========================= */
document.addEventListener("DOMContentLoaded", () => {

  const existingUser = JSON.parse(localStorage.getItem("user"));
  const role = localStorage.getItem("role");

  if (existingUser && role) {

    if (role === "student") {
      window.location.href = "student-dashboard.html";
    } 
    else if (role === "lecturer") {
      window.location.href = "lecturer-dashboard.html";
    } 
    else if (role === "administrator") {
      window.location.href = "admin-dashboard.html";
    }
  }
});

/* =========================
   LOGIN HANDLER
========================= */
loginForm.addEventListener("submit", async (e) => {
  e.preventDefault();

  message.textContent = "";
  loading.style.display = "block";

  const role = document.getElementById("role").value;
  const username = document.getElementById("username").value;
  const password = document.getElementById("password").value;

  try {

    const res = await fetch(
      "https://automative-graiding-system.onrender.com/api/auth/login",
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          userType: role,
          username: username,
          password: password
        })
      }
    );

    loading.style.display = "none";

    const text = await res.text();

    if (!res.ok) {
      message.textContent = text;
      return;
    }

    const data = JSON.parse(text);

    localStorage.setItem("role", role);
    localStorage.setItem("user", JSON.stringify(data));

    if (role === "student") {
      window.location.href = "student-dashboard.html";
    } 
    else if (role === "lecturer") {
      window.location.href = "lecturer-dashboard.html";
    } 
    else if (role === "administrator") {
      window.location.href = "admin-dashboard.html";
    }

  } catch (err) {
    loading.style.display = "none";
    message.textContent = "Server not reachable";
    console.error(err);
  }
});