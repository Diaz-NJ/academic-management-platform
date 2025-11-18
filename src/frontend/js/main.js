// Global State
let currentUser = null;
let tasks = [];
let events = [];
let notifications = [];

// Initialize App
document.addEventListener('DOMContentLoaded', function() {
    initializeApp();
    setupEventListeners();
    loadMockData(); // For testing without backend
});

// Initialize Application
function initializeApp() {
    // Check if user is logged in
    currentUser = {
        id: 1,
        name: "Neil John Diaz",
        studentId: "2021-00123",
        section: "BSIT-3I"
    };
    
    document.getElementById('userName').textContent = currentUser.name;
    
    // Load initial data
    loadDashboard();
}

// Setup Event Listeners
function setupEventListeners() {
    // Navigation
    document.querySelectorAll('.nav-menu a').forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const targetId = this.getAttribute('href').substring(1);
            showSection(targetId);
            
            // Update active state
            document.querySelectorAll('.nav-menu a').forEach(l => l.classList.remove('active'));
            this.classList.add('active');
        });
    });
    
    // Task Form Submission
    document.getElementById('taskForm').addEventListener('submit', handleTaskSubmit);
    
    // Event Form Submission
    document.getElementById('eventForm').addEventListener('submit', handleEventSubmit);
    
    // Filter changes
    document.getElementById('filterSubject')?.addEventListener('change', filterTasks);
    document.getElementById('filterPriority')?.addEventListener('change', filterTasks);
}

// Show Section
function showSection(sectionId) {
    document.querySelectorAll('.content-section').forEach(section => {
        section.classList.remove('active');
    });
    document.getElementById(sectionId)?.classList.add('active');
    
    // Load section-specific data
    switch(sectionId) {
        case 'dashboard':
            loadDashboard();
            break;
        case 'tasks':
            loadTasks();
            break;
        case 'calendar':
            loadCalendar();
            break;
        case 'collaboration':
            loadGroups();
            break;
        case 'analytics':
            loadAnalytics();
            break;
    }
}

// Modal Functions
function openModal(modalId) {
    document.getElementById(modalId).classList.add('active');
}

function closeModal(modalId) {
    document.getElementById(modalId).classList.remove('active');
}

// Handle Task Submission
async function handleTaskSubmit(e) {
    e.preventDefault();
    
    const taskData = {
        userId: currentUser.id,
        title: document.getElementById('taskTitle').value,
        description: document.getElementById('taskDescription').value,
        subject: document.getElementById('taskSubject').value,
        dueDate: document.getElementById('taskDueDate').value,
        priority: document.getElementById('taskPriority').value,
        status: 'Pending'
    };
    
    try {
        const response = await fetch('/api/tasks', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(taskData)
        });
        
        if (response.ok) {
            // Reset form and close modal
            document.getElementById('taskForm').reset();
            closeModal('taskModal');
            
            // Reload tasks
            await loadTasksFromAPI();
            loadDashboard();
            
            showNotification('Task created successfully!', 'success');
        } else {
            throw new Error('Failed to create task');
        }
        
    } catch (error) {
        console.error('Error creating task:', error);
        showNotification('Error creating task', 'error');
    }
}

// Load tasks from API
async function loadTasksFromAPI() {
    try {
        const response = await fetch(`/api/tasks?userId=${currentUser.id}`);
        
        if (response.ok) {
            tasks = await response.json();
        } else {
            console.error('Failed to load tasks');
        }
    } catch (error) {
        console.error('Error loading tasks:', error);
    }
}

// Update task status via API
async function updateTaskStatus(taskId, newStatus) {
    try {
        const task = tasks.find(t => t.id === taskId);
        if (!task) return;
        
        task.status = newStatus;
        
        const response = await fetch(`/api/tasks/${taskId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(task)
        });
        
        if (response.ok) {
            await loadTasksFromAPI();
            renderKanbanBoard();
            updateStats();
            showNotification('Task status updated!', 'success');
        } else {
            throw new Error('Failed to update task');
        }
    } catch (error) {
        console.error('Error updating task:', error);
        showNotification('Error updating task', 'error');
    }
}

// Handle Event Submission
async function handleEventSubmit(e) {
    e.preventDefault();
    
    const eventData = {
        title: document.getElementById('eventTitle').value,
        description: document.getElementById('eventDescription').value,
        eventType: document.getElementById('eventType').value,
        startDate: document.getElementById('eventStartDate').value,
        endDate: document.getElementById('eventEndDate').value || null,
        location: document.getElementById('eventLocation').value,
        color: document.getElementById('eventColor').value,
        userId: currentUser.id
    };
    
    try {
        const response = await fetch('/api/tasks', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(taskData)
        });
        
        if (response.ok) {
            // Reset form and close modal
            document.getElementById('taskForm').reset();
            closeModal('taskModal');
            
            // Reload tasks
            await loadTasksFromAPI();
            loadDashboard();
            
            showNotification('Task created successfully!', 'success');
        } else {
            throw new Error('Failed to create task');
        }
        
    } catch (error) {
        console.error('Error creating task:', error);
        showNotification('Error creating task', 'error');
    }
}

// Load Dashboard
function loadDashboard() {
    updateStats();
    renderKanbanBoard();
    loadUpcoming();
}

// Update Stats
function updateStats() {
    const now = new Date();
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    const weekFromNow = new Date(today.getTime() + 7 * 24 * 60 * 60 * 1000);
    
    // Pending tasks
    const pendingTasks = tasks.filter(t => t.status === 'Pending').length;
    document.getElementById('pendingTasks').textContent = pendingTasks;
    
    // Today's events
    const todayEvents = events.filter(e => {
        const eventDate = new Date(e.startDate);
        return eventDate.toDateString() === today.toDateString();
    }).length;
    document.getElementById('todayClasses').textContent = todayEvents;
    
    // This week's tasks
    const weekTasks = tasks.filter(t => {
        const dueDate = new Date(t.dueDate);
        return dueDate >= today && dueDate <= weekFromNow;
    }).length;
    document.getElementById('weekTasks').textContent = weekTasks;
    
    // Completion rate
    const completedTasks = tasks.filter(t => t.status === 'Completed').length;
    const totalTasks = tasks.length || 1;
    const completionRate = Math.round((completedTasks / totalTasks) * 100);
    document.getElementById('completionRate').textContent = completionRate + '%';
}

// Render Kanban Board
function renderKanbanBoard() {
    const statuses = ['Pending', 'In Progress', 'Completed'];
    
    statuses.forEach(status => {
        const column = document.querySelector(`.task-list[data-status="${status}"]`);
        if (!column) return;
        
        column.innerHTML = '';
        
        const statusTasks = tasks.filter(t => t.status === status);
        
        statusTasks.forEach(task => {
            const taskCard = createTaskCard(task);
            column.appendChild(taskCard);
        });
        
        // Add drag and drop functionality
        setupDragAndDrop(column);
    });
}

// Create Task Card
function createTaskCard(task) {
    const card = document.createElement('div');
    card.className = 'task-card';
    card.draggable = true;
    card.dataset.taskId = task.id;
    
    const dueDate = new Date(task.dueDate);
    const formattedDate = dueDate.toLocaleDateString('en-US', { 
        month: 'short', 
        day: 'numeric' 
    });
    
    card.innerHTML = `
        <h4>${task.title}</h4>
        <p style="font-size: 0.85rem; color: #6c757d; margin-bottom: 0.5rem;">
            ${task.subject || 'General'}
        </p>
        <div class="task-meta">
            <span>${formattedDate}</span>
            <span class="priority-badge priority-${task.priority}">${task.priority}</span>
        </div>
    `;
    
    // Add click to view details
    card.addEventListener('click', () => showTaskDetails(task));
    
    return card;
}

// Setup Drag and Drop
function setupDragAndDrop(column) {
    const cards = column.querySelectorAll('.task-card');
    
    cards.forEach(card => {
        card.addEventListener('dragstart', function(e) {
            e.dataTransfer.effectAllowed = 'move';
            e.dataTransfer.setData('text/html', this.innerHTML);
            e.dataTransfer.setData('taskId', this.dataset.taskId);
            this.style.opacity = '0.5';
        });
        
        card.addEventListener('dragend', function() {
            this.style.opacity = '1';
        });
    });
    
    column.addEventListener('dragover', function(e) {
        e.preventDefault();
        e.dataTransfer.dropEffect = 'move';
        this.style.backgroundColor = '#e9ecef';
    });
    
    column.addEventListener('dragleave', function() {
        this.style.backgroundColor = '';
    });
    
    column.addEventListener('drop', function(e) {
        e.preventDefault();
        this.style.backgroundColor = '';
        
        const taskId = parseInt(e.dataTransfer.getData('taskId'));
        const newStatus = this.dataset.status;
        
        updateTaskStatus(taskId, newStatus);
    });
}

// Update Task Status
async function updateTaskStatus(taskId, newStatus) {
    try {
        // TODO: Replace with actual API call
        const taskIndex = tasks.findIndex(t => t.id === taskId);
        if (taskIndex !== -1) {
            tasks[taskIndex].status = newStatus;
            renderKanbanBoard();
            updateStats();
            showNotification('Task status updated!', 'success');
        }
    } catch (error) {
        console.error('Error updating task:', error);
        showNotification('Error updating task', 'error');
    }
}

// Show Task Details
function showTaskDetails(task) {
    // Create a custom modal or expand the card
    alert(`Task: ${task.title}\n\nDescription: ${task.description}\nSubject: ${task.subject}\nDue: ${task.dueDate}\nPriority: ${task.priority}\nStatus: ${task.status}`);
}

// Load Upcoming
function loadUpcoming() {
    const upcomingList = document.getElementById('upcomingList');
    upcomingList.innerHTML = '';
    
    const now = new Date();
    const upcoming = [...tasks, ...events]
        .filter(item => {
            const date = new Date(item.dueDate || item.startDate);
            return date > now;
        })
        .sort((a, b) => {
            const dateA = new Date(a.dueDate || a.startDate);
            const dateB = new Date(b.dueDate || b.startDate);
            return dateA - dateB;
        })
        .slice(0, 5);
    
    if (upcoming.length === 0) {
        upcomingList.innerHTML = '<p style="color: #6c757d; font-size: 0.9rem;">No upcoming items</p>';
        return;
    }
    
    upcoming.forEach(item => {
        const div = document.createElement('div');
        div.className = 'upcoming-item';
        
        const date = new Date(item.dueDate || item.startDate);
        const formattedDate = date.toLocaleDateString('en-US', { 
            month: 'short', 
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
        
        div.innerHTML = `
            <strong>${item.title}</strong><br>
            <small style="color: #6c757d;">${formattedDate}</small>
        `;
        
        upcomingList.appendChild(div);
    });
}

// Load Tasks
function loadTasks() {
    const tasksList = document.getElementById('tasksList');
    tasksList.innerHTML = '';
    
    if (tasks.length === 0) {
        tasksList.innerHTML = '<p>No tasks yet. Create your first task!</p>';
        return;
    }
    
    tasks.forEach(task => {
        const taskItem = document.createElement('div');
        taskItem.className = 'task-item';
        
        const dueDate = new Date(task.dueDate);
        const formattedDate = dueDate.toLocaleString('en-US', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
        
        taskItem.innerHTML = `
            <div class="task-info">
                <h3>${task.title}</h3>
                <p>${task.description || 'No description'}</p>
                <div class="task-meta">
                    <span>📚 ${task.subject || 'General'}</span>
                    <span>📅 ${formattedDate}</span>
                    <span class="priority-badge priority-${task.priority}">${task.priority}</span>
                    <span style="font-weight: 600; color: ${getStatusColor(task.status)}">${task.status}</span>
                </div>
            </div>
            <div class="task-actions">
                <button class="btn btn-sm btn-primary" onclick="editTask(${task.id})">Edit</button>
                <button class="btn btn-sm btn-secondary" onclick="deleteTask(${task.id})">Delete</button>
                ${task.status !== 'Completed' ? 
                    `<button class="btn btn-sm" style="background-color: var(--success-color); color: white;" onclick="completeTask(${task.id})">Complete</button>` 
                    : ''}
            </div>
        `;
        
        tasksList.appendChild(taskItem);
    });
}

// Get Status Color
function getStatusColor(status) {
    const colors = {
        'Pending': '#ffc107',
        'In Progress': '#17a2b8',
        'Completed': '#28a745',
        'Overdue': '#dc3545'
    };
    return colors[status] || '#6c757d';
}

// Filter Tasks
function filterTasks() {
    const subject = document.getElementById('filterSubject').value;
    const priority = document.getElementById('filterPriority').value;
    
    const filteredTasks = tasks.filter(task => {
        const subjectMatch = subject === 'all' || task.subject === subject;
        const priorityMatch = priority === 'all' || task.priority === priority;
        return subjectMatch && priorityMatch;
    });
    
    // Temporarily replace tasks array for rendering
    const originalTasks = [...tasks];
    tasks = filteredTasks;
    loadTasks();
    tasks = originalTasks;
}

// Complete Task
function completeTask(taskId) {
    const taskIndex = tasks.findIndex(t => t.id === taskId);
    if (taskIndex !== -1) {
        tasks[taskIndex].status = 'Completed';
        loadTasks();
        loadDashboard();
        showNotification('Task completed! 🎉', 'success');
    }
}

// Edit Task
function editTask(taskId) {
    const task = tasks.find(t => t.id === taskId);
    if (!task) return;
    
    // Populate form with task data
    document.getElementById('taskTitle').value = task.title;
    document.getElementById('taskDescription').value = task.description;
    document.getElementById('taskSubject').value = task.subject;
    document.getElementById('taskDueDate').value = task.dueDate;
    document.getElementById('taskPriority').value = task.priority;
    
    // Remove old task
    deleteTask(taskId, false);
    
    // Open modal
    openModal('taskModal');
}

// Delete Task
function deleteTask(taskId, showConfirm = true) {
    if (showConfirm && !confirm('Are you sure you want to delete this task?')) {
        return;
    }
    
    const taskIndex = tasks.findIndex(t => t.id === taskId);
    if (taskIndex !== -1) {
        tasks.splice(taskIndex, 1);
        loadTasks();
        loadDashboard();
        if (showConfirm) {
            showNotification('Task deleted', 'info');
        }
    }
}

// Load Calendar
function loadCalendar() {
    const calendarView = document.getElementById('calendarView');
    
    // Simple calendar implementation
    calendarView.innerHTML = `
        <div style="text-align: center; padding: 2rem;">
            <h3>Calendar View</h3>
            <p style="color: #6c757d; margin-top: 1rem;">
                Calendar implementation coming soon. 
                <br>Consider integrating FullCalendar.js or similar library.
            </p>
            <div style="margin-top: 2rem;">
                <h4>Upcoming Events:</h4>
                <div id="eventsList"></div>
            </div>
        </div>
    `;
    
    const eventsList = document.getElementById('eventsList');
    
    if (events.length === 0) {
        eventsList.innerHTML = '<p>No events scheduled</p>';
        return;
    }
    
    events.forEach(event => {
        const eventItem = document.createElement('div');
        eventItem.style.cssText = 'background: white; border-left: 4px solid ' + event.color + '; padding: 1rem; margin: 1rem 0; border-radius: 4px; text-align: left;';
        
        const startDate = new Date(event.startDate);
        const formattedDate = startDate.toLocaleString();
        
        eventItem.innerHTML = `
            <strong>${event.title}</strong> - ${event.eventType}<br>
            <small>${formattedDate}</small><br>
            ${event.location ? `<small>📍 ${event.location}</small>` : ''}
        `;
        
        eventsList.appendChild(eventItem);
    });
}

// Load Groups
function loadGroups() {
    const groupsList = document.getElementById('groupsList');
    
    // Mock groups data
    const groups = [
        { id: 1, name: 'System Analysis Project', members: 5, description: 'Main capstone project group' },
        { id: 2, name: 'Database Study Group', members: 8, description: 'Weekly study sessions for DBMS' },
        { id: 3, name: 'Programming Practice', members: 12, description: 'Collaborative coding exercises' }
    ];
    
    groupsList.innerHTML = '';
    
    groups.forEach(group => {
        const groupCard = document.createElement('div');
        groupCard.className = 'group-card';
        
        groupCard.innerHTML = `
            <h3>${group.name}</h3>
            <p style="color: #6c757d; margin: 1rem 0;">${group.description}</p>
            <div style="display: flex; justify-content: space-between; align-items: center;">
                <span>👥 ${group.members} members</span>
                <button class="btn btn-sm btn-primary" onclick="viewGroup(${group.id})">View</button>
            </div>
        `;
        
        groupsList.appendChild(groupCard);
    });
}

// View Group
function viewGroup(groupId) {
    alert(`Viewing group ${groupId}. Group details and collaboration features coming soon!`);
}

// Load Analytics
function loadAnalytics() {
    // Mock analytics data
    const analyticsContainer = document.querySelector('.analytics-charts');
    
    analyticsContainer.innerHTML = `
        <div style="background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
            <h3>Task Completion Over Time</h3>
            <canvas id="completionChart" width="400" height="200"></canvas>
        </div>
        <div style="background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
            <h3>Tasks by Subject</h3>
            <canvas id="subjectChart" width="400" height="200"></canvas>
        </div>
        <div style="background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); grid-column: 1 / -1;">
            <h3>Performance Summary</h3>
            <p>Total Tasks: ${tasks.length}</p>
            <p>Completed: ${tasks.filter(t => t.status === 'Completed').length}</p>
            <p>In Progress: ${tasks.filter(t => t.status === 'In Progress').length}</p>
            <p>Pending: ${tasks.filter(t => t.status === 'Pending').length}</p>
        </div>
    `;
    
    // Note: For actual charts, integrate Chart.js library
    // Add this to your HTML: <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
}

// Show Notification
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 1rem 2rem;
        background-color: ${type === 'success' ? '#28a745' : type === 'error' ? '#dc3545' : '#17a2b8'};
        color: white;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.2);
        z-index: 3000;
        animation: slideIn 0.3s ease-out;
    `;
    notification.textContent = message;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease-out';
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

// Add CSS animations
const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from { transform: translateX(400px); opacity: 0; }
        to { transform: translateX(0); opacity: 1; }
    }
    @keyframes slideOut {
        from { transform: translateX(0); opacity: 1; }
        to { transform: translateX(400px); opacity: 0; }
    }
`;
document.head.appendChild(style);

// Load Mock Data for Testing
function loadMockData() {
    tasks = [
        {
            id: 1,
            title: 'Complete Chapter 1 Documentation',
            description: 'Write the problem and background section',
            subject: 'System Analysis',
            dueDate: '2025-11-20T23:59',
            priority: 'High',
            status: 'In Progress',
            userId: 1
        },
        {
            id: 2,
            title: 'Database Design Assignment',
            description: 'Create ER diagram for the project',
            subject: 'Database',
            dueDate: '2025-11-18T17:00',
            priority: 'Urgent',
            status: 'Pending',
            userId: 1
        },
        {
            id: 3,
            title: 'Review Java Concepts',
            description: 'Study inheritance and polymorphism',
            subject: 'Programming',
            dueDate: '2025-11-19T14:00',
            priority: 'Medium',
            status: 'Pending',
            userId: 1
        },
        {
            id: 4,
            title: 'Prepare Presentation Slides',
            description: 'Create slides for midterm defense',
            subject: 'System Analysis',
            dueDate: '2025-11-25T10:00',
            priority: 'High',
            status: 'Pending',
            userId: 1
        },
        {
            id: 5,
            title: 'Submit Requirements Document',
            description: 'Finalize and submit to professor',
            subject: 'System Analysis',
            dueDate: '2025-11-15T23:59',
            priority: 'Medium',
            status: 'Completed',
            userId: 1
        }
    ];
    
    events = [
        {
            id: 1,
            title: 'System Analysis Class',
            description: 'Regular class meeting',
            eventType: 'Class',
            startDate: '2025-11-18T09:00',
            endDate: '2025-11-18T12:00',
            location: 'Room 301',
            color: '#3788d8',
            userId: 1
        },
        {
            id: 2,
            title: 'Database Midterm Exam',
            description: 'Comprehensive exam covering chapters 1-5',
            eventType: 'Exam',
            startDate: '2025-11-22T13:00',
            endDate: '2025-11-22T15:00',
            location: 'Computer Lab',
            color: '#dc3545',
            userId: 1
        },
        {
            id: 3,
            title: 'Group Meeting',
            description: 'Discuss project progress',
            eventType: 'Meeting',
            startDate: '2025-11-19T16:00',
            endDate: '2025-11-19T18:00',
            location: 'Library Study Room',
            color: '#28a745',
            userId: 1
        }
    ];
}

// Export functions for global access
window.openModal = openModal;
window.closeModal = closeModal;
window.completeTask = completeTask;
window.editTask = editTask;
window.deleteTask = deleteTask;
window.viewGroup = viewGroup;