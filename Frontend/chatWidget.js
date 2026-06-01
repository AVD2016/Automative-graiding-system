function toggleChat() {
  const widget = document.getElementById("chatWidget");
  widget.classList.toggle("hidden");
}

async function sendMessage() {

  const input = document.getElementById("chatInput");
  const messages = document.getElementById("chatMessages");

  const text = input.value.trim();
  if (!text) return;

  // show user message
  messages.innerHTML += `<div><b>You:</b> ${text}</div>`;
  input.value = "";

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

    //  response text
    const reply = data.reply || "No response";

    messages.innerHTML += `<div><b>Bot:</b> ${reply}</div>`;
    messages.scrollTop = messages.scrollHeight;

  } catch (error) {

    console.error(error);

    messages.innerHTML += `<div><b>Bot:</b> Error contacting server</div>`;
  }
}

// WAIT UNTIL PAGE LOADS
document.addEventListener("DOMContentLoaded", () => {

  const btn = document.getElementById("chatToggle");

  if (btn) {
    btn.addEventListener("click", toggleChat);
  }
});