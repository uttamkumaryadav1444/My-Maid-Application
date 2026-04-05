// Common utilities
const API_BASE = 'http://localhost:8080/api';

// Check authentication
function checkAuth() {
    const token = localStorage.getItem('token');
    const userType = localStorage.getItem('userType');

    if (!token || !userType) {
        // Redirect to login if not authenticated
        if (!window.location.href.includes('login.html') &&
            !window.location.href.includes('register.html') &&
            !window.location.href.includes('index.html')) {
            window.location.href = 'login.html';
        }
        return null;
    }

    return { token, userType };
}

// Get user data
function getUserData() {
    const userData = localStorage.getItem('userData');
    return userData ? JSON.parse(userData) : null;
}

// Logout
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('userType');
    localStorage.removeItem('userData');
    window.location.href = 'index.html';
}

// Format date
function formatDate(dateString) {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString('en-IN');
}

// Show notification
function showNotification(message, type = 'success') {
    // Create notification element
    const notification = document.createElement('div');
    notification.className = `fixed top-4 right-4 px-6 py-3 rounded-lg shadow-lg z-50 ${
        type === 'success' ? 'bg-green-500' :
        type === 'error' ? 'bg-red-500' : 'bg-blue-500'
    } text-white`;
    notification.textContent = message;

    document.body.appendChild(notification);

    // Remove after 3 seconds
    setTimeout(() => {
        notification.remove();
    }, 3000);
}

// API call wrapper
async function apiCall(endpoint, method = 'GET', data = null) {
    const token = localStorage.getItem('token');

    const headers = {
        'Content-Type': 'application/json'
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const config = {
        method,
        headers
    };

    if (data && (method === 'POST' || method === 'PUT')) {
        config.body = JSON.stringify(data);
    }

    try {
        const response = await fetch(`${API_BASE}${endpoint}`, config);

        if (response.status === 401) {
            // Unauthorized - redirect to login
            logout();
            return null;
        }

        const result = await response.json();
        return { success: response.ok, data: result };
    } catch (error) {
        console.error('API Error:', error);
        return { success: false, error: error.message };
    }
}

// Initialize common functionality
document.addEventListener('DOMContentLoaded', function() {
    // Add logout button to dashboard pages if user is logged in
    const userData = getUserData();

    if (userData) {
        // Update user name in dashboard if element exists
        const userNameElement = document.getElementById('userName');
        if (userNameElement) {
            userNameElement.textContent = userData.name || userData.fullName || 'User';
        }

        // Add logout button to nav if not already present
        if (!document.getElementById('logoutBtn')) {
            const nav = document.querySelector('nav .flex.items-center');
            if (nav) {
                const logoutBtn = document.createElement('button');
                logoutBtn.id = 'logoutBtn';
                logoutBtn.className = 'text-red-600 hover:text-red-800 ml-4';
                logoutBtn.innerHTML = '<i class="fas fa-sign-out-alt mr-2"></i>Logout';
                logoutBtn.onclick = logout;
                nav.appendChild(logoutBtn);
            }
        }
    }
});