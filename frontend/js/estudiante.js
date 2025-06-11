// API Configuration
const API_BASE_URL = 'http://localhost:8081/api'; // Adjust according to your backend configuration
let authToken = null;

// Initialize application
document.addEventListener('DOMContentLoaded', function() {
    // Check if user is already logged in (optional, but good practice for later dashboard integration)
    const token = sessionStorage.getItem('authToken');
    if (token) {
        authToken = token;
        // In a real scenario, you might want to validate the token with the backend here
        // For this specific request, we only focus on the login form
        console.log("User already logged in with a token. Consider redirecting or pre-filling data.");
        // Optional: If you want to auto-redirect if already logged in:
        // window.location.href = 'dashboard_estudiante.html';
    }

    // Setup form handlers
    document.getElementById('estudianteLoginForm').addEventListener('submit', handleLogin);
});

// Authentication Functions
async function handleLogin(event) {
    event.preventDefault(); // Prevent default form submission

    const username = document.getElementById('username').value;
    const password = document.getElementById('contrasena').value;

    setLoginLoading(true);
    clearAlert('login-alert');

    try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username, password })
        });

        const data = await response.json();
        console.log('Login response:', data); // Debug the full response

        if (response.ok) {
            // Ensure the token is correctly obtained from the response data
            authToken = data.token || data.accessToken || data.jwt || null;
            console.log('Token JWT recibido y guardado:', authToken); // Debug

            if (!authToken) {
                showAlert('login-alert', 'No se recibió token JWT del backend', 'error');
                return;
            }

            // Check for ROL_ESTUDIANTE
            // Assuming 'roles' is an array of role names (e.g., ['ROL_ESTUDIANTE', 'ROL_ADMIN']) in the response
            const userRoles = data.roles;
            if (userRoles && userRoles.includes('ROLE_ESTUDIANTE')) {
                sessionStorage.setItem('authToken', authToken);
                // IMPORTANT CHANGE: Store the 'email' specifically if it's a distinct attribute
                // Assuming your backend sends an 'email' field in the successful login response
                if (data.email) {
                    sessionStorage.setItem('studentEmail', data.email); // Store the student's email
                } else {
                    console.warn("Email attribute not found in login response. Dashboard features might be limited.");
                    // Fallback to username if email is not provided, or handle as an error
                    sessionStorage.setItem('studentEmail', username);
                }

                showAlert('login-alert', 'Login exitoso! Bienvenido, Estudiante.', 'success');
                // Redirect to the student dashboard after a short delay
                setTimeout(() => {
                    window.location.href = 'dashboard_estudiante.html'; // Redirect to the student dashboard
                }, 1000);
            } else {
                // If the user does not have ROL_ESTUDIANTE, clear any potential token and show an error
                authToken = null; // Clear token if wrong role
                sessionStorage.removeItem('authToken'); // Ensure no invalid token is stored
                sessionStorage.removeItem('studentEmail'); // Clear student email as well
                showAlert('login-alert', 'Acceso denegado. Este login es solo para Estudiantes.', 'warning');
            }
        } else {
            // Handle non-OK responses (e.g., 401 Unauthorized, 403 Forbidden, 400 Bad Request)
            showAlert('login-alert', data.message || 'Error en el login. Credenciales inválidas o error del servidor.', 'error');
        }
    } catch (error) {
        console.error('Login error:', error);
        showAlert('login-alert', 'Error de conexión con el servidor. Inténtalo más tarde.', 'error');
    } finally {
        setLoginLoading(false); // Always hide loading spinner and re-enable button
    }
}

// Utility Functions (adapted from previous implementations)
function showAlert(containerId, message, type) {
    const container = document.getElementById(containerId);
    container.innerHTML = `<div class="alert alert-${type}">${message}</div>`;

    // Auto-hide success alerts after a few seconds
    if (type === 'success') {
        setTimeout(() => clearAlert(containerId), 3000);
    }
}

function clearAlert(containerId) {
    document.getElementById(containerId).innerHTML = '';
}

function setLoginLoading(loading) {
    const btnText = document.getElementById('login-btn-text');
    const loadingSpinner = document.getElementById('login-loading');
    const btn = document.querySelector('#estudianteLoginForm .login-button'); // Select the button within the form

    if (loading) {
        btnText.classList.add('hidden');
        loadingSpinner.classList.remove('hidden');
        btn.disabled = true; // Disable button to prevent multiple submissions
    } else {
        btnText.classList.remove('hidden');
        loadingSpinner.classList.add('hidden');
        btn.disabled = false; // Re-enable button
    }
}