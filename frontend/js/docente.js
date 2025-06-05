const API_BASE_URL = 'http://localhost:8081/api'; // Ajusta según tu configuración
let authToken = null;
let currentMode = 'create';

document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('docenteLoginForm').addEventListener('submit', handleDocenteLogin);
});

async function handleDocenteLogin(event) {
    event.preventDefault();

    const correo = document.getElementById('correo').value;
    const contrasena = document.getElementById('contrasena').value;

    // Puedes agregar una función para mostrar/ocultar un spinner o mensaje de carga
    // setLoginLoading(true);
    // clearAlert('login-alert');

    try {
        // CAMBIO CLAVE: El endpoint es /auth/login para ambos roles
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            // CAMBIO CLAVE: El backend espera 'username' y 'password'
            body: JSON.stringify({ username: correo, password: contrasena })
        });

        const data = await response.json();
        console.log('Docente Login response:', data); // Debug

        if (response.ok) {
            authToken = data.token || data.accessToken || data.jwt || null;
            console.log('Docente Token JWT recibido y guardado:', authToken); // Debug

            if (!authToken) {
                alert('No se recibió token JWT del backend. Por favor, intente de nuevo.');
                return;
            }

            // Validar que el usuario logueado tenga el rol de docente
            // Asumiendo que `data.roles` es una lista de strings como ["ROLE_DOCENTE"]
            const userRoles = new Set(data.roles); // Convertir a Set para búsqueda eficiente
            if (!userRoles.has("ROLE_DOCENTE") && !userRoles.has("docente")) { // Ajusta el nombre del rol si es diferente (ej. "DOCENTE")
                alert("Acceso denegado: Este login es solo para DOCENTES.");
                // Opcional: Limpiar el token si se guardó por error o redirigir al login principal
                sessionStorage.removeItem('authToken');
                sessionStorage.removeItem('userEmail');
                sessionStorage.removeItem('userRole');
                return;
            }


            sessionStorage.setItem('authToken', authToken);
            sessionStorage.setItem('userEmail', correo);
            sessionStorage.setItem('userRole', 'docente'); // Guardamos el rol para futuras verificaciones

            alert('Docente Login exitoso!');
            setTimeout(() => {
                window.location.href = 'docente_dashboard.html';
            }, 1000);

        } else {
            alert(data.message || 'Error en el login. Credenciales inválidas o no autorizado.');
        }
    } catch (error) {
        console.error('Docente Login error:', error);
        alert('Error de conexión con el servidor. Por favor, intente de nuevo.');
    } finally {
        // setLoginLoading(false); // Descomentar si implementas estas funciones
    }
}