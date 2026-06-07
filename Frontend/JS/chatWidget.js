// ============================
// CHAT STATE
// ============================

function getUserId() {
  const user = JSON.parse(localStorage.getItem("user"));
  return user?.id || user?.userId || "guest";
}

// ============================
// TOGGLE CHAT
// ============================

function toggleChat() {
  const widget = document.getElementById("chatWidget");
  if (!widget) return;
  widget.classList.toggle("hidden");
}

// ============================
// SAVE HISTORY (LOCAL UI ONLY)
// ============================

function saveChatMessage(html) {
  const history = JSON.parse(localStorage.getItem("chatHistory")) || [];
  history.push(html);
  localStorage.setItem("chatHistory", JSON.stringify(history));
}

// ============================
// LOAD HISTORY
// ============================

function loadChatHistory() {
  const messages = document.getElementById("chatMessages");
  if (!messages) return;

  const history = JSON.parse(localStorage.getItem("chatHistory")) || [];
  messages.innerHTML = history.join("");
  messages.scrollTop = messages.scrollHeight;
}

// ============================
// SEND MESSAGE
// ============================

async function sendMessage() {

  const input =
    document.getElementById("chatInput");

  const messages =
    document.getElementById("chatMessages");

  if (!input || !messages) return;

  const text = input.value.trim();

  if (!text) return;

  const userId = getUserId();

  /* =========================
     USER MESSAGE
  ========================= */

const userMsg = `
  <div class="chat-message user">
    <div class="message-bubble">
      ${text}
    </div>
  </div>
`;

  messages.innerHTML += userMsg;

  saveChatMessage(userMsg);

  input.value = "";

  messages.scrollTop =
    messages.scrollHeight;

  try {

    const response = await fetch(
      "https://automative-graiding-system.onrender.com/api/chat/send",
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          message: text,
          userId: userId
        })
      }
    );

    const data = await response.json();

    const reply =
      data.reply || "No response";

    /* =========================
       BOT MESSAGE
    ========================= */

    const botMsg = `
  <div class="chat-message bot">
    <div class="message-bubble">
      ${reply}
    </div>
  </div>
`;

    messages.innerHTML += botMsg;

    saveChatMessage(botMsg);

    messages.scrollTop =
      messages.scrollHeight;

  } catch (error) {

    console.error(error);

    const errorMsg = `
  <div class="chat-message bot">
    <div class="message-bubble">
      Error contacting server
    </div>
  </div>
`;

    messages.innerHTML += errorMsg;

    saveChatMessage(errorMsg);
  }
}

// ============================
// INIT
// ============================

function initChat() {
  const btn = document.getElementById("chatToggle");

  if (btn && !btn.dataset.bound) {
    btn.addEventListener("click", toggleChat);
    btn.dataset.bound = "true";
  }

  loadChatHistory();
}

if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", initChat);
} else {
  initChat();
}