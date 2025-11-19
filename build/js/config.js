// API Configuration
const API_CONFIG = {
    BASE_URL: '/academic-management-platform',
    ENDPOINTS: {
        // Auth endpoints
        LOGIN: '/api/auth/login',
        REGISTER: '/api/auth/register',
        LOGOUT: '/api/auth/logout',
        SESSION: '/api/auth/session',
        
        // Task endpoints
        TASKS: '/api/tasks',
        TASK_BY_ID: (id) => `/api/tasks/${id}`,
        
        // Event endpoints
        EVENTS: '/api/events',
        EVENT_BY_ID: (id) => `/api/events/${id}`,
        
        // Other endpoints can be added here
    }
};

// Helper function to get full API URL
function getApiUrl(endpoint) {
    return API_CONFIG.BASE_URL + endpoint;
}

// Helper function for API calls
async function apiCall(endpoint, options = {}) {
    const url = getApiUrl(endpoint);
    
    const defaultOptions = {
        headers: {
            'Content-Type': 'application/json',
        },
        ...options
    };
    
    try {
        const response = await fetch(url, defaultOptions);
        const data = await response.json();
        
        return {
            ok: response.ok,
            status: response.status,
            data: data
        };
    } catch (error) {
        console.error('API call error:', error);
        return {
            ok: false,
            status: 500,
            data: { error: error.message }
        };
    }
}

// Export for use in other files
window.API_CONFIG = API_CONFIG;
window.getApiUrl = getApiUrl;
window.apiCall = apiCall;