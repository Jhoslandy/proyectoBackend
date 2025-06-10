// estudiante.js

const API_BASE_URL = 'http://localhost:8081/api'; // Asegúrate que esta URL sea correcta
let authToken = null;
let userUsername = null; 

document.addEventListener('DOMContentLoaded', function() {
    const token = sessionStorage.getItem('authToken');
    const storedUsername = sessionStorage.getItem('userUsername');
    if (token && storedUsername) {
        authToken = token;
        userUsername = storedUsername;
        showDashboard();
    } else {
        showLoginSection();
    }

    const loginForm = document.getElementById('login-form');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }

    const logoutBtn = document.getElementById('logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', handleLogout);
    }
});

async function handleLogin(event) {
    event.preventDefault();

    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');

    if (!usernameInput || !passwordInput) {
        showAlert('login-alert', 'Error: No se encontraron los campos de usuario o contraseña.', 'error');
        return;
    }

    const username = usernameInput.value;
    const password = passwordInput.value;

    setLoginLoading(true);
    clearAlert('login-alert');

    try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });

        const data = await response.json();

        if (response.ok) {
            authToken = data.token;
            userUsername = data.username;
            sessionStorage.setItem('authToken', authToken);
            sessionStorage.setItem('userUsername', userUsername);

            const userRoles = new Set(data.roles);
            console.log("Roles del usuario recibidos:", userRoles); // DEPURACIÓN: Ver roles

            if (!userRoles.has("ROLE_ESTUDIANTE")) {
                showAlert('login-alert', "Acceso denegado: Este login es solo para ESTUDIANTES.", 'error');
                sessionStorage.clear();
                authToken = null;
                userUsername = null;
                setLoginLoading(false);
                return;
            }

            showAlert('login-alert', 'Inicio de sesión exitoso. Redirigiendo...', 'success');
            setTimeout(() => {
                showDashboard();
                // Aquí podrías cargar datos específicos para el estudiante (ej. loadEstudianteData(userUsername);)
            }, 1000);
        } else {
            let errorMessage = 'Ocurrió un error inesperado al iniciar sesión.';
            if (data && data.message) {
                errorMessage = data.message;
            } else if (response.status === 401) {
                errorMessage = 'Credenciales incorrectas. Verifica tu usuario y contraseña.';
            } else if (response.status === 403) {
                errorMessage = 'Acceso denegado. No tienes permiso para acceder.';
            }
            showAlert('login-alert', errorMessage, 'error');
        }
    } catch (error) {
        console.error('Error de conexión:', error);
        showAlert('login-alert', 'Error de conexión con el servidor. Intenta de nuevo más tarde.', 'error');
    } finally {
        setLoginLoading(false);
    }
}

function handleLogout() {
    sessionStorage.clear();
    authToken = null;
    userUsername = null;
    showAlert('login-alert', 'Sesión cerrada exitosamente.', 'info');
    showLoginSection();
    clearDashboard();
}

function showLoginSection() {
    document.getElementById('login-section').classList.remove('hidden');
    document.getElementById('student-dashboard').classList.add('hidden');
    const docenteDashboard = document.getElementById('docente-dashboard');
    if (docenteDashboard) docenteDashboard.classList.add('hidden');
}

function showDashboard() {
    document.getElementById('login-section').classList.add('hidden');
    document.getElementById('student-dashboard').classList.remove('hidden');
    const docenteDashboard = document.getElementById('docente-dashboard');
    if (docenteDashboard) docenteDashboard.classList.add('hidden');

    const welcomeMessage = document.getElementById('student-welcome-message');
    if (welcomeMessage && userUsername) {
        welcomeMessage.textContent = `Bienvenido, ${userUsername} (Estudiante)`;
    }
}

function clearDashboard() {
    const welcomeMessage = document.getElementById('student-welcome-message');
    if (welcomeMessage) {
        welcomeMessage.textContent = '';
    }
    clearAlert('student-alert');
}

function showAlert(elementId, message, type) {
    const alertElement = document.getElementById(elementId);
    if (alertElement) {
        alertElement.textContent = message;
        alertElement.className = `alert alert-${type}`;
        alertElement.classList.remove('hidden');
    }
}

function clearAlert(elementId) {
    const alertElement = document.getElementById(elementId);
    if (alertElement) {
        alertElement.textContent = '';
        alertElement.className = '';
        alertElement.classList.add('hidden');
    }
}

function setLoginLoading(loading) {
    const btnText = document.getElementById('login-btn-text');
    const loadingSpinner = document.getElementById('login-loading');
    const btn = document.querySelector('#login-form .btn');

    if (btnText && loadingSpinner && btn) {
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
}

// Ejemplo de carga de datos para estudiante (DESCOMENTAR SI TIENES UN ENDPOINT ASOCIADO)
/*
async function loadEstudianteData(username) {
    try {
        const response = await fetch(`${API_BASE_URL}/estudiantes/${username}/perfil`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${authToken}`, // ¡CRÍTICO para peticiones protegidas!
                'Content-Type': 'application/json'
            }
        });
        if (response.ok) {
            const data = await response.json();
            console.log('Datos del estudiante cargados:', data);
            // Actualiza tu HTML con los datos
        } else if (response.status === 401) {
            showAlert('student-alert', 'Sesión expirada o no autorizada. Por favor, inicia sesión de nuevo.', 'error');
            handleLogout();
        } else if (response.status === 403) {
            showAlert('student-alert', 'No tienes permiso para ver esta información.', 'error');
        } else {
            showAlert('student-alert', 'No se pudieron cargar los datos del estudiante.', 'error');
            console.error('Error al cargar datos del estudiante:', response.statusText);
        }
    } catch (error) {
        console.error('Error de conexión al cargar datos del estudiante:', error);
        showAlert('student-alert', 'Error de conexión al cargar datos del estudiante.', 'error');
    }
}
*/