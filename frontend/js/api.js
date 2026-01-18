const API_BASE_URL = "http://localhost:8080";

async function fetchTasks() {
    console.log("API: Fetching ALL tasks...");
    const response = await fetch(`${API_BASE_URL}/tasks`);
    return response.json();
}

async function fetchTasksByPriority(priority) {
    console.log(`API: Filtering tasks by priority [${priority}]...`);
    const response = await fetch(`${API_BASE_URL}/tasks/byPriority/${priority}`);
    if (!response.ok) {
        if (response.status === 404) return [];
        console.warn(`API: No tasks found for priority [${priority}]`);
        throw new Error("Failed to filter tasks");
    }
    return response.json();
}

async function createTask(task) {
    console.log("API: Sending POST to create task:", task);
    const response = await fetch(`${API_BASE_URL}/tasks`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(task)
    });
    if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Server error: ${errorText}`);
    }
    console.log(`API: Server responded with status: ${response.status} ${response.statusText}`);
    return response;
}

async function updateTask(originalName, task) {
    console.log(`API: Updating task [${originalName}]...`);
    const response = await fetch(`${API_BASE_URL}/tasks/${originalName}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(task)
    });

    if (!response.ok) {
        throw new Error("Failed to update task");
    }
    console.log(`API: Task [${originalName}] updated.`);
    return response;
}
