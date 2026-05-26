const loginForm = document.getElementById("loginForm");
const message = document.getElementById("message");
const loading = document.getElementById("loading");

loginForm.addEventListener("submit", async (e) => {
  e.preventDefault();

  message.textContent = "";
  loading.style.display = "block";

  const role = document.getElementById("role").value;
  const username = document.getElementById("email").value;
  const password = document.getElementById("password").value;

  try {
    const res = await fetch("https://automative-graiding-system.onrender.com/api/auth/login", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        userType: role,
        username: username,
        password: password
      })
    });

    const data = await res.json();

    loading.style.display = "none";

    if (res.ok) {
      // NOTE: your backend currently returns full entity, not token
      localStorage.setItem("role", role);
      localStorage.setItem("user", JSON.stringify(data));

      if (role === "student") {
        window.location.href = "/student-dashboard.html";
      } else if (role === "lecturer") {
        window.location.href = "/lecturer-dashboard.html";
      } else if (role === "administrator") {
        window.location.href = "/admin-dashboard.html";
      }

    } else {
      message.textContent = data.message || "Login failed";
    }

  } catch (err) {
    loading.style.display = "none";
    message.textContent = "Server not reachable";
    console.error(err);
  }
});