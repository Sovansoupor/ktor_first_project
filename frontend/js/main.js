const taskList = document.getElementById("taskList");
const taskModal = document.getElementById("taskModal");
const openModalBtn = document.getElementById("openModalBtn");
const closeModalBtn = document.getElementById("closeModalBtn");
const taskForm = document.getElementById("taskForm");

// Inputs from the Modal
const nameInput = document.getElementById("taskName");
const descInput = document.getElementById("taskDesc");
const priorityInput = document.getElementById("taskPriority");
const filterPriority = document.getElementById("filterPriority");
let editingTaskName = null;

// Modal Controls
openModalBtn.onclick = () => {
    editingTaskName = null;
    taskForm.reset();
    document.querySelector("#taskModal h1").textContent = "New Task";
    taskModal.showModal();
};

closeModalBtn.onclick = () => taskModal.close();

async function loadTasks(priority = "All") {
    try {
        let tasks;
        if (priority === "All") {
            tasks = await fetchTasks();
        } else {
            tasks = await fetchTasksByPriority(priority);
        }
        renderTaskList(tasks);
    } catch (error) {
        console.error("Load failed:", error);
    }

    function renderTaskList(tasks) {
        taskList.innerHTML = "";
        if (tasks.length === 0) {
            taskList.innerHTML = `<li style="justify-content: center; color: #999;">No tasks found</li>`;
            return;
        }
        tasks.forEach(task => {
            const li = document.createElement("li");

            // Left Side: Text container
            const infoDiv = document.createElement("div");
            infoDiv.className = "task-info";

            const nameSpan = document.createElement("span");
            nameSpan.className = "taskTag";
            nameSpan.textContent = task.name;

            const descSpan = document.createElement("span");
            descSpan.className = "task-description";
            descSpan.textContent = task.description;

            infoDiv.appendChild(nameSpan);
            infoDiv.appendChild(descSpan);

            // Badge + Actions
            const rightSide = document.createElement("div");
            rightSide.className = "task-right-side";

            const badge = document.createElement("span");
            badge.className = `badge priority-${task.priority.toLowerCase()}`;
            badge.textContent = task.priority;

            const actions = document.createElement("div");
            actions.className = "actions";

            // Edit Button
            const editBtn = document.createElement("button");
            editBtn.className = "btn-icon btn-edit";
            editBtn.innerHTML = `<i data-lucide="pencil"></i>`;
            editBtn.onclick = () => {
                console.log(`Editing task: ${task.name}`);
                editingTaskName = task.name; // Store the name for the API call later

                // Pre-fill the form with existing data
                nameInput.value = task.name;
                descInput.value = task.description;
                priorityInput.value = task.priority;

                document.querySelector("#taskModal h1").textContent = "Edit Task";
                taskModal.showModal();
            };

            // Delete Button
            const deleteBtn = document.createElement("button");
            deleteBtn.className = "btn-icon btn-danger";
            deleteBtn.innerHTML = `<i data-lucide="trash-2"></i>`;
            deleteBtn.onclick = async () => {
                if (confirm(`Delete "${task.name}"?`)) {
                    await fetch(`${API_BASE_URL}/tasks/${task.name}`, {method: 'DELETE'});
                    await loadTasks();
                }
            };

            actions.appendChild(editBtn);
            actions.appendChild(deleteBtn);

            rightSide.appendChild(badge);
            rightSide.appendChild(actions);

            li.appendChild(infoDiv);
            li.appendChild(rightSide);
            taskList.appendChild(li);
        });

        if (window.lucide) lucide.createIcons();
    }

    taskForm.onsubmit = async (e) => {
        e.preventDefault();

        const taskData = {
            name: nameInput.value,
            description: descInput.value,
            priority: priorityInput.value
        };

        try {
            if (editingTaskName) {
                await updateTask(editingTaskName, taskData);
                editingTaskName = null;
            } else {
                await createTask(taskData);
            }
            // Clean up UI
            taskForm.reset();
            document.querySelector("#taskModal h1").textContent = "New Task";
            taskModal.close();
            loadTasks();//refresh
        } catch (error) {
            console.error("UI ERROR:", error);
        }
    };
}
filterPriority.onchange = (e) => {
    loadTasks(e.target.value);
};

// Initial Load
loadTasks();