// API Configuration
const API_BASE_URL = 'http://localhost:8081/api';
let authToken = null;
let currentStudentCi = null; // To store the CI of the logged-in student (primary key of ESTUDIANTE)
let currentMode = 'create'; // 'create' or 'edit' for enrollments

// Initialize application
document.addEventListener('DOMContentLoaded', function() {
    // Check if user is logged in
    const token = sessionStorage.getItem('authToken');
    // Getting the student's email directly from sessionStorage, as saved in estudiante.js
    const studentEmailFromSession = sessionStorage.getItem('studentEmail');

    if (token && studentEmailFromSession) {
        authToken = token;
        // Fetch student's CI using the stored email
        getStudentCiAndLoadDashboard(studentEmailFromSession);
    } else {
        // If not logged in, redirect to login page
        alert('Sesión no iniciada. Por favor, inicie sesión.');
        window.location.href = 'estudiante.html';
    }

    // Set up logout button
    document.getElementById('logout-btn').addEventListener('click', logout);

    // Set up enrollment form handler
    document.getElementById('enrollment-form').addEventListener('submit', function(e) {
        e.preventDefault();
        saveEnrollment();
    });
});

// --- Authentication & Session Management ---
function logout() {
    authToken = null;
    sessionStorage.removeItem('authToken');
    sessionStorage.removeItem('studentEmail'); // Clear the stored email
    alert('Has cerrado sesión.');
    window.location.href = 'estudiante.html';
}

async function checkAuthAndExecute() {
    if (!authToken) {
        console.warn('No hay token JWT presente. Redirigiendo a login.');
        showAlert('dashboard-alert', 'Sesión expirada o no autorizada. Por favor, inicia sesión de nuevo.', 'error');
        logout();
        return false;
    }
    return true;
}

// --- Student CI Retrieval and Data Loading ---
async function getStudentCiAndLoadDashboard(studentEmail) { // Parameter is now explicitly 'studentEmail'
    if (!(await checkAuthAndExecute())) return;

    try {
        // Calling the Backend Endpoint: /api/estudiantes/by-email/{email}
        const studentResponse = await fetch(`${API_BASE_URL}/estudiantes/by-email/${studentEmail}`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        if (studentResponse.ok) {
            const studentData = await studentResponse.json();
            currentStudentCi = studentData.ci; // Get 'ci' from the response (primary key)
            const studentActualEmail = studentData.email; // Get 'email' from the response

            if (currentStudentCi) {
                // Display the actual email from the student data
                document.getElementById('student-email-display').textContent = `Email: ${studentActualEmail || studentEmail}`;
                loadDashboardData(); // Load all dashboard data now that we have CI
            } else {
                showAlert('dashboard-alert', 'No se pudo obtener el CI del estudiante. Acceso denegado.', 'error');
                logout();
            }
        } else if (studentResponse.status === 401 || studentResponse.status === 403) {
            showAlert('dashboard-alert', 'Sesión expirada o no autorizada. Por favor, inicia sesión de nuevo.', 'error');
            logout();
        } else {
            const errorData = await studentResponse.json();
            showAlert('dashboard-alert', errorData.message || 'Error al obtener datos del estudiante.', 'error');
        }
    } catch (error) {
        console.error('Error fetching student CI:', error);
        showAlert('dashboard-alert', 'Error de conexión al obtener datos del estudiante.', 'error');
    }
}

async function loadDashboardData() {
    if (currentStudentCi) {
        loadAvailableSubjects();
        loadMyEnrollments();
    } else {
        console.error('No student CI available to load dashboard data.');
        showAlert('dashboard-alert', 'No se pudo cargar el dashboard sin el ID de estudiante.', 'error');
        // No logout here, as getStudentCiAndLoadDashboard would have already handled it if CI was not found.
    }
}

// --- Data Loading Functions ---
async function loadAvailableSubjects() {
    try {
        if (!(await checkAuthAndExecute())) return;

        const response = await fetch(`${API_BASE_URL}/materias`, { // Endpoint for all subjects
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const subjects = await response.json();
            displayAvailableSubjects(subjects);
        } else if (response.status === 401 || response.status === 403) {
            showAlert('dashboard-alert', 'Sesión expirada o no autorizada. Por favor, inicia sesión de nuevo.', 'error');
            logout();
        } else {
            showAlert('dashboard-alert', 'Error al cargar materias disponibles.', 'error');
        }
    } catch (error) {
        console.error('Error loading available subjects:', error);
        showAlert('dashboard-alert', 'Error de conexión al cargar materias.', 'error');
    }
}

function displayAvailableSubjects(subjects) {
    const tbody = document.getElementById('available-subjects-tbody');
    tbody.innerHTML = '';
    if (subjects.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4">No hay materias disponibles en este momento.</td></tr>';
        return;
    }
    subjects.forEach(subject => {
        const row = document.createElement('tr');
        // Using attribute names from your MATERIA table: codigoUnico, nombreMateria, descripcion
        row.innerHTML = `
            <td>${subject.codigoUnico}</td>
            <td>${subject.nombreMateria}</td>
            <td>${subject.descripcion}</td>
            <td>
                <button class="btn btn-primary" onclick="openEnrollmentModal('${subject.codigoUnico}', '${subject.nombreMateria}', 'create')">Inscribir</button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

async function loadMyEnrollments() {
    if (!(await checkAuthAndExecute())) return;

    if (!currentStudentCi) {
        showAlert('dashboard-alert', 'No se pudo identificar al estudiante para cargar inscripciones.', 'error');
        return;
    }

    try {
        // Use currentStudentCi to fetch enrollments specific to this student
        // Assuming your backend has: GET /api/inscripciones/estudiante/{ci}
        const response = await fetch(`${API_BASE_URL}/inscripciones/estudiante/${currentStudentCi}`, {
            method: 'GET', // Explicitly use GET method
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const enrollments = await response.json();
            displayMyEnrollments(enrollments);
        } else if (response.status === 401 || response.status === 403) {
            showAlert('dashboard-alert', 'Sesión expirada o no autorizada. Por favor, inicia sesión de nuevo.', 'error');
            logout();
        } else {
            const errorData = await response.json();
            showAlert('dashboard-alert', errorData.message || 'Error al cargar mis inscripciones.', 'error');
        }
    } catch (error) {
        console.error('Error loading my enrollments:', error);
        showAlert('dashboard-alert', 'Error de conexión al cargar inscripciones.', 'error');
    }
}

function displayMyEnrollments(enrollments) {
    const tbody = document.getElementById('my-enrollments-tbody');
    tbody.innerHTML = '';
    if (enrollments.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4">No tienes inscripciones activas.</td></tr>';
        return;
    }
    enrollments.forEach(enrollment => {
        const row = document.createElement('tr');
        // Using attribute names from your `inscrito` table: ESTUDIANTE_ci, MATERIA_codigoUnico, fechaInscripcion
        // `nombreMateria` would come from a JOIN in the backend's DTO for `inscrito`
        row.innerHTML = `
            <td>${enrollment.MATERIA_codigoUnico}</td>
            <td>${enrollment.nombreMateria || 'N/A'}</td> <td>${enrollment.fechaInscripcion ? new Date(enrollment.fechaInscripcion).toLocaleDateString() : 'N/A'}</td>
            <td>
                <button class="btn btn-secondary" onclick="editEnrollment('${enrollment.id}', '${enrollment.MATERIA_codigoUnico}', '${enrollment.nombreMateria}')">Editar</button>
                <button class="btn btn-danger" onclick="deleteEnrollment('${enrollment.id}')">Eliminar</button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

// --- Enrollment CRUD Functions ---
async function saveEnrollment() {
    const materiaCodigo = document.getElementById('enrollment-materia-codigo').value;
    const enrollmentId = document.getElementById('enrollment-id').value; // Only for edit

    if (!currentStudentCi) {
        showAlert('modal-alert', 'No se pudo identificar al estudiante para guardar la inscripción.', 'error');
        return;
    }

    const enrollmentData = {
        ESTUDIANTE_ci: currentStudentCi, // Use the fetched student CI (primary key of ESTUDIANTE)
        MATERIA_codigoUnico: materiaCodigo, // Foreign key to MATERIA
        fechaInscripcion: new Date().toISOString().split('T')[0] // Format: YYYY-MM-DD
    };

    setSaveLoading(true);
    clearAlert('modal-alert');

    try {
        if (!(await checkAuthAndExecute())) return;

        let response;
        if (currentMode === 'create') {
            const cleanData = Object.fromEntries(Object.entries(enrollmentData).filter(([_, v]) => v !== undefined));
            response = await fetch(`${API_BASE_URL}/inscripciones`, { // Assuming POST for new enrollment
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${authToken}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(cleanData)
            });
        } else { // 'edit' mode, typically PUT to an ID
            // For `inscrito`, updating might mean re-sending if it's a simple record.
            // If your `inscrito` entity has its own primary key `id` (not just composite FKs), use that for PUT.
            // Assuming PUT /api/inscripciones/{id} to update an enrollment record.
            const editEnrollmentData = { ...enrollmentData, id: parseInt(enrollmentId) }; // Include ID for PUT
            const cleanEditData = Object.fromEntries(Object.entries(editEnrollmentData).filter(([_, v]) => v !== undefined));
            response = await fetch(`${API_BASE_URL}/inscripciones/${enrollmentId}`, { // Assuming PUT to update an enrollment by its ID
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${authToken}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(cleanEditData)
            });
        }

        if (response.ok) {
            showAlert('dashboard-alert',
                currentMode === 'create' ? 'Inscripción creada exitosamente.' : 'Inscripción actualizada exitosamente.',
                'success');
            closeModal();
            loadMyEnrollments(); // Reload student's enrollments
            loadAvailableSubjects(); // Refresh available subjects as well (optional, if enrollment affects availability)
        } else if (response.status === 401 || response.status === 403) {
            showAlert('modal-alert', 'Sesión expirada o no autorizada. Por favor, inicia sesión de nuevo.', 'error');
            logout();
        } else {
            const errorData = await response.json();
            showAlert('modal-alert', errorData.message || 'Error al guardar la inscripción.', 'error');
        }
    } catch (error) {
        console.error('Error saving enrollment:', error);
        showAlert('modal-alert', 'Error de conexión al guardar la inscripción.', 'error');
    } finally {
        setSaveLoading(false);
    }
}


async function deleteEnrollment(id) {
    if (!confirm('¿Estás seguro de que deseas eliminar esta inscripción?')) {
        return;
    }

    if (!(await checkAuthAndExecute())) return;

    try {
        const response = await fetch(`${API_BASE_URL}/inscripciones/${id}`, { // Assuming DELETE endpoint by enrollment ID
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            showAlert('dashboard-alert', 'Inscripción eliminada exitosamente.', 'success');
            loadMyEnrollments(); // Reload student's enrollments
            loadAvailableSubjects(); // Refresh available subjects
        } else if (response.status === 401 || response.status === 403) {
            showAlert('dashboard-alert', 'Sesión expirada o no autorizada. Por favor, inicia sesión de nuevo.', 'error');
            logout();
        } else {
            const errorData = await response.json();
            showAlert('dashboard-alert', errorData.message || 'Error al eliminar la inscripción.', 'error');
        }
    } catch (error) {
        console.error('Error deleting enrollment:', error);
        showAlert('dashboard-alert', 'Error de conexión al eliminar inscripción.', 'error');
    }
}

// Function to open the enrollment modal
function openEnrollmentModal(materiaCodigo, materiaNombre, mode, enrollmentId = '') {
    currentMode = mode;
    const modal = document.getElementById('enrollment-modal');
    const title = document.getElementById('modal-title');

    document.getElementById('enrollment-materia-codigo').value = materiaCodigo;
    document.getElementById('enrollment-materia-nombre').value = materiaNombre;
    document.getElementById('enrollment-id').value = enrollmentId; // Set ID for edit

    if (mode === 'create') {
        title.textContent = 'Inscribir Materia';
    } else { // edit mode
        title.textContent = 'Editar Inscripción';
    }

    clearAlert('modal-alert');
    modal.style.display = 'block';
}

// Function to simulate 'editing' an enrollment (opens modal with pre-filled data)
async function editEnrollment(enrollmentId, materiaCodigo, materiaNombre) {
    // For now, it will just pre-fill the modal as if for re-saving/confirmation.
    // If your backend supports updating fields within an 'inscrito' record (e.g., fechaInscripcion),
    // you'd fetch that enrollment by ID here and populate all modal fields.
    openEnrollmentModal(materiaCodigo, materiaNombre, 'edit', enrollmentId);
}


// --- Modal & Utility Functions ---
function closeModal() {
    document.getElementById('enrollment-modal').style.display = 'none';
    document.getElementById('enrollment-form').reset();
    clearAlert('modal-alert');
}

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

function setSaveLoading(loading) {
    const btnText = document.getElementById('save-btn-text');
    const loadingSpinner = document.getElementById('save-loading');
    const btn = document.querySelector('.modal-footer .btn-success');

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

// Close modal when clicking outside
window.onclick = function(event) {
    const modal = document.getElementById('enrollment-modal');
    if (event.target === modal) {
        closeModal();
    }
}