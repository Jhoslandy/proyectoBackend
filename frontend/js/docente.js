// API Configuration
const API_BASE_URL = 'http://localhost:8081/api';
let authToken = null;

// Initialize application
document.addEventListener('DOMContentLoaded', function() {
    const token = sessionStorage.getItem('authToken');
    if (token) {
        authToken = token;
        console.log("User already logged in with a token. Consider redirecting or pre-filling data.");
    }
    document.getElementById('docenteLoginForm').addEventListener('submit', handleLogin);
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

            // **NEW LOGIC: Check for ROL_DOCENTE**
            const userRoles = data.roles; // Assuming 'roles' is an array of role names in the response 
            if (userRoles && userRoles.includes('ROLE_DOCENTE')) { // 
                sessionStorage.setItem('authToken', authToken);
                sessionStorage.setItem('username', username);

                showAlert('login-alert', 'Login exitoso! Bienvenido, Docente.', 'success');
                setTimeout(() => {
                    // Redirect to a docente-specific dashboard or page
                    // window.location.href = 'docente-dashboard.html';
                    console.log("Login successful for ROL_DOCENTE! Ready to proceed to dashboard.");
                }, 1000);
            } else {
                // If the user does not have ROL_DOCENTE, clear the token and show an error
                authToken = null;
                sessionStorage.removeItem('authToken');
                sessionStorage.removeItem('username');
                showAlert('login-alert', 'Acceso denegado. Este login es solo para Docentes.', 'warning');
            }
        } else {
            showAlert('login-alert', data.message || 'Error en el login', 'error');
        }
    } catch (error) {
        console.error('Login error:', error);
        showAlert('login-alert', 'Error de conexion con el servidor', 'error');
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
    const btn = document.querySelector('#docenteLoginForm .login-button');

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