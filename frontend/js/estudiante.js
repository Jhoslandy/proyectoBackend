const API_BASE_URL = 'http://localhost:8081/api'; // Ajusta según tu configuración
let authToken = null; // Token JWT para la autenticación
let currentMode = 'create'; // 'create' or 'edit' - Esta variable puede no ser necesaria en un login simple.

// Initialize application
document.addEventListener('DOMContentLoaded', function() {
    // Similar al docente.js, esta parte es más para un dashboard después del login.
    // const token = sessionStorage.getItem('authToken');
    // if (token) {
    //     authToken = token;
    //     // Si hay un token, podrías redirigir al dashboard de estudiantes
    //     // window.location.href = 'estudiante_dashboard.html';
    // }

    // Setup form handlers
    document.getElementById('estudianteLoginForm').addEventListener('submit', handleEstudianteLogin); // <-- Usa el ID de tu formulario de estudiante
});

// Authentication Function for Estudiantes
async function handleEstudianteLogin(event) {
    event.preventDefault();

    const correo = document.getElementById('correo').value; // <-- Asegúrate que tu HTML tiene un input con ID 'correo'
    const contrasena = document.getElementById('contrasena').value; // <-- Asegúrate que tu HTML tiene un input con ID 'contrasena'

    // Puedes agregar una función para mostrar/ocultar un spinner o mensaje de carga
    // setLoginLoading(true);
    // clearAlert('login-alert');

    try {
        // Adaptar la URL al endpoint de login de estudiantes
        const response = await fetch(`${API_BASE_URL}/auth/login/estudiante`, { // <-- CAMBIO CLAVE: Endpoint específico para estudiantes
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            // El body debe coincidir con lo que tu backend espera para el login de estudiantes
            body: JSON.stringify({ correo: correo, contrasena: contrasena })
        });

        const data = await response.json();
        console.log('Estudiante Login response:', data); // Debug

        if (response.ok) {
            authToken = data.token || data.accessToken || data.jwt || null;
            console.log('Estudiante Token JWT recibido y guardado:', authToken); // Debug
            if (!authToken) {
                alert('No se recibió token JWT del backend. Por favor, intente de nuevo.');
                return;
            }
            sessionStorage.setItem('authToken', authToken);
            sessionStorage.setItem('userEmail', correo); // O sessionStorage.setItem('username', correo);
            sessionStorage.setItem('userRole', 'estudiante'); // <-- Guarda el rol del usuario

            alert('Estudiante Login exitoso!');
            setTimeout(() => {
                // Redirigir al dashboard de estudiantes después de un login exitoso
                window.location.href = 'estudiante_dashboard.html'; // <-- Crea esta página para el dashboard
            }, 1000);
        } else {
            alert(data.message || 'Error en el login de estudiante. Credenciales inválidas.');
        }
    } catch (error) {
        console.error('Estudiante Login error:', error);
        alert('Error de conexión con el servidor. Por favor, intente de nuevo.');
    } finally {
        // setLoginLoading(false);
    }
}

// Las funciones `logout`, `showAlert`, `clearAlert`, `setLoginLoading`, `showDashboard`, `loadStudents`
// no son directamente relevantes para la página de login en sí, sino para un dashboard posterior.
// Si las vas a usar, deberías implementarlas en el script del dashboard.
// Para el login, solo necesitamos `handleEstudianteLogin`.