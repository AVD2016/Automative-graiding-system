// CHAT TOGGLE
function toggleChat() {
  const widget = document.getElementById("chatWidget");
  if (!widget) return;

  widget.classList.toggle("hidden");
}


// SAVE MESSAGE TO STORAGE

function saveChatMessage(html) {
  const history = JSON.parse(localStorage.getItem("chatHistory")) || [];
  history.push(html);
  localStorage.setItem("chatHistory", JSON.stringify(history));
}


// LOAD CHAT HISTORY

function loadChatHistory() {
  const messages = document.getElementById("chatMessages");
  if (!messages) return;

  const history = JSON.parse(localStorage.getItem("chatHistory")) || [];

  messages.innerHTML = "";

  history.forEach(msg => {
    messages.innerHTML += msg;
  });

  messages.scrollTop = messages.scrollHeight;
}

// SEND MESSAGE

async function sendMessage() {

  const input = document.getElementById("chatInput");
  const messages = document.getElementById("chatMessages");

  if (!input || !messages) return;

  const text = input.value.trim();
  if (!text) return;

  // USER MESSAGE
  const userMsg = `<div><b>You:</b> ${text}</div>`;
  messages.innerHTML += userMsg;
  saveChatMessage(userMsg);

  input.value = "";

  messages.scrollTop = messages.scrollHeight;

  try {

    const response = await fetch("https://automative-graiding-system.onrender.com/api/chat/send", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        message: text
      })
    });

    const data = await response.json();

    const reply = data.reply || "No response";

    // BOT MESSAGE
    const botMsg = `<div><b>Bot:</b> ${reply}</div>`;
    messages.innerHTML += botMsg;
    saveChatMessage(botMsg);

    messages.scrollTop = messages.scrollHeight;

  } catch (error) {

    console.error(error);

    const errorMsg = `<div><b>Bot:</b> Error contacting server</div>`;
    messages.innerHTML += errorMsg;
    saveChatMessage(errorMsg);
  }
}

function initChat() {

  const btn = document.getElementById("chatToggle");
  if (btn && !btn.dataset.bound) {
    btn.addEventListener("click", toggleChat);
    btn.dataset.bound = "true"; // prevents duplicate binding
  }

  loadChatHistory();
}

// Run immediately if DOM already loaded
if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", initChat);
} else {
  initChat(); // 🔥 IMPORTANT FIX
}