// API Configuration
const API_BASE_URL = 'http://localhost:8081/api';
let authToken = null;

// Initialize application
document.addEventListener('DOMContentLoaded', function() {
    const token = sessionStorage.getItem('authToken');
    if (token) {
        authToken = token;
        console.log("User already logged in with a token. Consider redirecting or pre-filling data.");
        // In a real scenario, you might want to validate the token with the backend here
        // and potentially redirect to the admin dashboard if the role is correct.
    }
    document.getElementById('adminLoginForm').addEventListener('submit', handleLogin);
});

// Authentication Functions
async function handleLogin(event) {
    event.preventDefault();
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
        console.log('Login response:', data); // Debug

        if (response.ok) {
            authToken = data.token || data.accessToken || data.jwt || null;
            console.log('Token JWT recibido y guardado:', authToken); // Debug

            if (!authToken) {
                showAlert('login-alert', 'No se recibió token JWT del backend', 'error');
                return;
            }

            // Decode JWT to check roles (client-side check for redirection)
            // In a real application, role validation should ALWAYS be done on the backend.
            const payload = JSON.parse(atob(authToken.split('.')[1]));
            console.log('JWT Payload:', payload); // Debug JWT payload

            const userRoles = payload.roles || []; // Assuming roles are in a 'roles' array in the JWT payload
            const requiredRole = "ROLE_ADMIN";

            if (userRoles.includes(requiredRole)) {
                sessionStorage.setItem('authToken', authToken);
                sessionStorage.setItem('username', username); // Store username for display or further use
                showAlert('login-alert', '¡Inicio de sesión exitoso! Redireccionando...', 'success');
                window.location.href = 'dashboard_admin.html'; // Redirect to admin dashboard
            } else {
                // If user is not an Admin, clear token and show error
                sessionStorage.removeItem('authToken');
                sessionStorage.removeItem('username');
                showAlert('login-alert', 'Acceso denegado. Este login es solo para Administradores.', 'warning');
            }
        } else {
            showAlert('login-alert', data.message || 'Error en el login', 'error');
        }
    } catch (error) {
        console.error('Login error:', error);
        showAlert('login-alert', 'Error de conexión con el servidor', 'error');
    } finally {
        setLoginLoading(false);
    }
}

// Utility Functions
function showAlert(containerId, message, type) {
    const container = document.getElementById(containerId);
    container.innerHTML = `<div class="alert alert-${type}">${message}</div>`;

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
    const btn = document.querySelector('#adminLoginForm .login-button');

    if (loading) {
        btnText.classList.add('hidden');
        loadingSpinner.classList.remove('hidden');
        btn.disabled = true;
    } else {
        btnText.classList.remove('hidden');
        loadingSpinner.classList.add('hidden');
        btn.disabled = false;
    }
}