let currentAssignmentId = null;

  function openModal(assignment) {
    currentAssignmentId = assignment.id;

    document.getElementById("modalTitle").innerText = assignment.title;
    document.getElementById("modalDescription").innerText = assignment.description;
    document.getElementById("modalCriteria").innerText = assignment.markingCriteria;
    document.getElementById("modalCredits").innerText = assignment.credits;
    document.getElementById("modalDeadline").innerText = assignment.deadline;

    document.getElementById("assignmentModal").style.display = "flex";
  }

  function closeModal() {
    document.getElementById("assignmentModal").style.display = "none";
  }

  function submitAssignment() {
    const file = document.getElementById("submissionFile").files[0];
    if (!file) return alert("Select a file first");

    console.log("Submitting:", file, currentAssignmentId);
    alert("Submitted!");
  }

  function deleteSubmission() {
    if (!confirm("Delete submission?")) return;
    console.log("Deleted submission for", currentAssignmentId);
    alert("Deleted");
  }

  function formatDeadline(dateStr) {
    const now = new Date();
    const date = new Date(dateStr);
    const diff = (date - now) / (1000 * 60 * 60 * 24);

    if (diff < 3) return "deadline-red";
    if (diff < 7) return "deadline-orange";
    return "";
  }