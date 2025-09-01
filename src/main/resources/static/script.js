let token = "";

// UI helpers
function showLoggedInUI() {
  document.getElementById("registro").style.display = "none";
  document.getElementById("login").style.display = "none";
  document.getElementById("taskSection").style.display = "block";
  fetchTasks();
}

function showErrorIfNotLoggedIn(sectionId) {
  if (!token) {
    const el = document.getElementById(sectionId);
    el.innerHTML = `<p style="color:red;">❌ Debes iniciar sesión o registrarte primero.</p>`;
    return true;
  }
  return false;
}

// Registro
function register() {
  fetch("http://localhost:8080/auth/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      username: document.getElementById("regUsername").value,
      name: document.getElementById("regName").value,
      email: document.getElementById("regEmail").value,
      password: document.getElementById("regPassword").value
    })
  })
    .then(res => {
      if (!res.ok) throw new Error("Error en registro");
      return res.json();
    })
    .then(() => {
      document.getElementById("registroResult").textContent =
        "✅ Registro exitoso. Ahora inicia sesión.";
    })
    .catch(() => {
      document.getElementById("registroResult").textContent = "❌ Error en el registro.";
    });
}

// Login
function login() {
  const username = document.getElementById("loginUsername").value;
  const password = document.getElementById("loginPassword").value;

  fetch("http://localhost:8080/auth/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password })
  })
    .then(res => {
      if (!res.ok) throw new Error("Credenciales incorrectas");
      return res.json();
    })
    .then(data => {
      token = data.jwt; 
      document.getElementById("loginResult").textContent = "🎉 Login exitoso.";
      showLoggedInUI();
    })
    .catch(() => {
      document.getElementById("loginResult").textContent = "❌ Error en login.";
    });
}

// Obtener tareas
function fetchTasks() {
  if (!token) return;

  fetch("http://localhost:8080/tasks/me", {
    headers: { Authorization: `Bearer ${token}` }
  })
    .then(res => {
      if (!res.ok) throw new Error("Error al cargar tareas");
      return res.json();
    })
    .then(tasks => {
      const container = document.getElementById("tasksList");
      container.innerHTML = "";

      tasks.forEach(t => {
        const card = document.createElement("div");
        card.className = "task-item";
        card.innerHTML = `
          <h3>${t.title}</h3>
          <small>Estado: ${t.status} | Prioridad: ${t.priority} | Vence: ${t.dueDate}</small>
          <p>${t.description || ""}</p>
          <div class="task-actions">
            <button class="edit-btn">✏️ Editar</button>
            <button class="del-btn">🗑 Eliminar</button>
          </div>
        `;

        // Editar -> rellena el formulario con TODOS los campos
        card.querySelector(".edit-btn").addEventListener("click", () => startEditTask(t));

        // Eliminar
        card.querySelector(".del-btn").addEventListener("click", () => deleteTask(t.id));

        container.appendChild(card);
      });
    })
    .catch(() => {
      document.getElementById("tasksList").textContent =
        "❌ Error al cargar tareas, quizá tu token expiró.";
    });
}

// Crear tarea
function createTask() {
  if (!token) return alert("Debes loguearte primero.");

  const task = {
    title: document.getElementById("taskTitle").value,
    description: document.getElementById("taskDescription").value,
    dueDate: document.getElementById("taskDueDate").value,
    priority: document.getElementById("taskPriority").value,
    status: document.getElementById("taskStatus").value // ✅ ahora se lee del select
  };

  fetch("http://localhost:8080/tasks", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`
    },
    body: JSON.stringify(task)
  })
    .then(res => {
      if (!res.ok) throw new Error("Error al crear tarea");
      return res.json();
    })
    .then(() => {
      alert("✅ Tarea creada");
      fetchTasks();
      document.getElementById("taskForm").reset(); // limpia el form
    })
    .catch(() => alert("❌ Error creando tarea"));
}

// Eliminar tarea
function deleteTask(id) {
  fetch(`http://localhost:8080/tasks/${id}`, {
    method: "DELETE",
    headers: { Authorization: `Bearer ${token}` }
  })
    .then(res => {
      if (!res.ok) throw new Error("Error al eliminar tarea");
      alert("✅ Tarea eliminada");
      fetchTasks();
    })
    .catch(() => alert("❌ Error eliminando tarea"));
}

// Actualizar tarea
let editingId = null; // si es null -> creas; si tiene id -> actualizas

// Rellena el formulario con la tarea seleccionada para editar
function startEditTask(task) {
  editingId = task.id;
  document.getElementById("taskTitle").value = task.title || "";
  document.getElementById("taskDescription").value = task.description || "";
  document.getElementById("taskDueDate").value = task.dueDate || "";
  document.getElementById("taskPriority").value = task.priority || "MEDIUM";
  document.getElementById("taskStatus").value = task.status || "PENDING";

  // Cambia el texto del botón de guardar para que el usuario sepa que está en modo edición
  // Mostrar botón de actualizar y ocultar el de crear
  document.getElementById("saveTaskBtn").style.display = "none";
  document.getElementById("updateTaskBtn").style.display = "inline-block";
}

// Envía el PUT usando los valores del formulario (igual que crear)
function updateTask() {
  if (!editingId) {
    alert("Primero pulsa ✏️ Editar en una tarea.");
    return;
  }
  if (!token) return alert("Debes loguearte primero.");

  const payload = {
    title: document.getElementById("taskTitle").value,
    description: document.getElementById("taskDescription").value,
    dueDate: document.getElementById("taskDueDate").value,
    priority: document.getElementById("taskPriority").value,
    status: document.getElementById("taskStatus").value
  };

  fetch(`http://localhost:8080/tasks/${editingId}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`
    },
    body: JSON.stringify(payload)
  })
    .then(res => {
      if (!res.ok) return res.text().then(t => { throw new Error(t || "Error al actualizar"); });
      return res.json();
    })
    .then(() => {
      alert("✅ Tarea actualizada");
      // reset al modo creación
      editingId = null;
      // Mostrar botón de crear y ocultar el de actualizar
      document.getElementById("saveTaskBtn").style.display = "inline-block";
      document.getElementById("updateTaskBtn").style.display = "inline-block";
      document.getElementById("taskForm").reset();
      fetchTasks();
    })
    .catch(err => alert("❌ " + err.message));
}