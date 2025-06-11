const API_BASE_URL = 'http://localhost:8081/api';
let authToken = null;
let currentAdminEmail = null; // To display the logged-in admin's email

document.addEventListener('DOMContentLoaded', async function() {
    // 1. Check for authentication token and user role
    authToken = sessionStorage.getItem('authToken');
    const username = sessionStorage.getItem('username'); // Get username from session

    if (!authToken) {
        alert('No autorizado. Por favor, inicie sesión como administrador.');
        window.location.href = 'admin.html'; // Redirect to admin login
        return;
    }

    try {
        // Decode JWT to check roles (client-side check for initial redirection)
        const payload = JSON.parse(atob(authToken.split('.')[1]));
        const userRoles = payload.roles || []; // Assuming roles are in a 'roles' array

        if (!userRoles.includes('ROLE_ADMIN')) {
            alert('Acceso denegado. No tienes los permisos de administrador.');
            sessionStorage.clear(); // Clear invalid session
            window.location.href = 'admin.html';
            return;
        }

        // Display admin email if available (or username)
        currentAdminEmail = username || payload.sub; // Assuming 'sub' in JWT is username/email
        document.getElementById('admin-email-display').textContent = `Conectado como: ${currentAdminEmail}`;

        // 2. Setup event listeners
        document.getElementById('logout-btn').addEventListener('click', handleLogout);

        // **NUEVA LÓGICA DE NAVEGACIÓN Y CARGA DE DATOS**
        setupNavigation();

        // Cargar y mostrar la sección de Materias por defecto al iniciar
        showSection('materias');
        loadMaterias();

    } catch (error) {
        console.error('Error durante la inicialización:', error);
        alert('Ocurrió un error al cargar el dashboard. Por favor, intente de nuevo.');
        sessionStorage.clear();
        window.location.href = 'admin.html';
    }
});

function setupNavigation() {
    const navLinks = document.querySelectorAll('.nav-link');
    const sections = document.querySelectorAll('.dashboard-section');

    navLinks.forEach(link => {
        link.addEventListener('click', (event) => {
            event.preventDefault();

            // Remover la clase 'active' de todos los enlaces
            navLinks.forEach(l => l.classList.remove('active'));
            // Ocultar todas las secciones
            sections.forEach(s => s.style.display = 'none');

            // Añadir la clase 'active' al enlace clickeado
            event.target.classList.add('active');

            // Obtener el ID de la sección a mostrar y la función de carga
            const sectionId = event.target.dataset.section;
            const loadFunction = event.target.dataset.loadFunction;

            // Mostrar la sección correspondiente
            showSection(sectionId);

            // Llamar a la función de carga de datos correspondiente
            if (typeof window[loadFunction] === 'function') {
                window[loadFunction]();
            }
        });
    });
}

function showSection(sectionName) {
    const sectionElement = document.getElementById(`${sectionName}-section`);
    if (sectionElement) {
        sectionElement.style.display = 'block';
    }
}

// Global utility functions (showAlert, clearAlert, setButtonLoading)
function showAlert(containerId, message, type) {
    const container = document.getElementById(containerId);
    if (container) {
        container.innerHTML = `<div class="alert alert-${type}">${message}</div>`;
        if (type === 'success') {
            setTimeout(() => clearAlert(containerId), 3000);
        }
    }
}

function clearAlert(containerId) {
    const container = document.getElementById(containerId);
    if (container) {
        container.innerHTML = '';
    }
}

function setButtonLoading(selector, originalText, loading) {
    const button = document.querySelector(selector);
    const buttonTextSpan = button ? button.querySelector('span:not(.spinner)') : null;
    const spinnerSpan = button ? button.querySelector('.spinner') : null;

    if (!button || !buttonTextSpan || !spinnerSpan) {
        console.warn('Button or spinner elements not found for selector:', selector);
        return;
    }

    if (loading) {
        buttonTextSpan.classList.add('hidden');
        spinnerSpan.classList.remove('hidden');
        button.disabled = true;
    } else {
        buttonTextSpan.classList.remove('hidden');
        spinnerSpan.classList.add('hidden');
        button.disabled = false;
        buttonTextSpan.textContent = originalText; // Restore original text
    }
}

// --- Authentication & Authorization ---
async function handleLogout() {
    // In a real app, you might want to call a backend logout endpoint here
    sessionStorage.clear(); // Clear all session data
    alert('Has cerrado sesión exitosamente.');
    window.location.href = 'admin.html'; // Redirect to login page
}

// --- MATERIAS SECTION FUNCTIONS ---
let currentMateriaId = null; // To store the ID of the materia being edited
let isMateriaEditing = false; // Flag to check if we are in edit mode

async function loadMaterias() {
    const tbody = document.getElementById('materias-tbody');
    tbody.innerHTML = '<tr><td colspan="4">Cargando materias...</td></tr>';
    showAlert('dashboard-alert', 'Cargando materias...', 'info');

    try {
        const response = await fetch(`${API_BASE_URL}/materias`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Error al obtener las materias.');
        }

        const materias = await response.json();
        tbody.innerHTML = ''; // Clear loading message

        if (materias.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4">No hay materias registradas.</td></tr>';
            showAlert('dashboard-alert', 'No hay materias registradas.', 'warning');
            return;
        }

        materias.forEach(materia => {
            const row = tbody.insertRow();
            row.insertCell(0).textContent = materia.codigoUnico;
            row.insertCell(1).textContent = materia.nombreMateria;
            row.insertCell(2).textContent = materia.descripcion;

            const actionsCell = row.insertCell(3);
            const editBtn = document.createElement('button');
            editBtn.textContent = 'Editar';
            editBtn.classList.add('btn', 'btn-edit');
            editBtn.onclick = () => openMateriaModal(true, materia);
            actionsCell.appendChild(editBtn);

            const deleteBtn = document.createElement('button');
            deleteBtn.textContent = 'Eliminar';
            deleteBtn.classList.add('btn', 'btn-danger');
            deleteBtn.onclick = () => deleteMateria(materia.codigoUnico);
            actionsCell.appendChild(deleteBtn);
        });
        showAlert('dashboard-alert', 'Materias cargadas exitosamente.', 'success');
    } catch (error) {
        console.error('Error al cargar materias:', error);
        tbody.innerHTML = '<tr><td colspan="4">Error al cargar materias.</td></tr>';
        showAlert('dashboard-alert', `Error: ${error.message}`, 'error');
    }
}

function openMateriaModal(editing = false, materia = {}) {
    const modal = document.getElementById('materia-modal');
    const title = document.getElementById('materia-modal-title');
    const saveBtnText = document.getElementById('materia-save-btn-text');
    const form = document.getElementById('materia-form');

    isMateriaEditing = editing;
    clearAlert('materia-modal-alert');
    form.reset(); // Clear previous form data

    if (editing) {
        title.textContent = 'Editar Materia';
        saveBtnText.textContent = 'Actualizar Materia';
        currentMateriaId = materia.codigoUnico; // Store for update
        document.getElementById('materia-nombre').value = materia.nombreMateria;
        document.getElementById('materia-codigo-unico').value = materia.codigoUnico;
        document.getElementById('materia-codigo-unico').disabled = true; // CI/Código Único usually not editable
        document.getElementById('materia-descripcion').value = materia.descripcion;
    } else {
        title.textContent = 'Agregar Nueva Materia';
        saveBtnText.textContent = 'Guardar Materia';
        currentMateriaId = null;
        document.getElementById('materia-codigo-unico').disabled = false;
    }

    modal.style.display = 'flex'; // Show modal
}

function closeMateriaModal() {
    document.getElementById('materia-modal').style.display = 'none';
    clearAlert('materia-modal-alert');
}

document.getElementById('materia-form').addEventListener('submit', async function(event) {
    event.preventDefault();
    setButtonLoading('materia-form .btn-success', isMateriaEditing ? 'Actualizar Materia' : 'Guardar Materia', true);
    clearAlert('materia-modal-alert');

    const nombreMateria = document.getElementById('materia-nombre').value;
    const codigoUnico = document.getElementById('materia-codigo-unico').value;
    const descripcion = document.getElementById('materia-descripcion').value;

    const materiaData = {
        nombreMateria,
        codigoUnico,
        descripcion
    };

    try {
        let response;
        if (isMateriaEditing) {
            response = await fetch(`${API_BASE_URL}/materias/${currentMateriaId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${authToken}`
                },
                body: JSON.stringify(materiaData)
            });
        } else {
            response = await fetch(`${API_BASE_URL}/materias`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${authToken}`
                },
                body: JSON.stringify(materiaData)
            });
        }

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Error al guardar la materia.');
        }

        showAlert('materia-modal-alert', `Materia ${isMateriaEditing ? 'actualizada' : 'agregada'} exitosamente.`, 'success');
        closeMateriaModal();
        loadMaterias();
    } catch (error) {
        console.error('Error saving materia:', error);
        showAlert('materia-modal-alert', `Error: ${error.message}`, 'error');
    } finally {
        setButtonLoading('materia-form .btn-success', isMateriaEditing ? 'Actualizar Materia' : 'Guardar Materia', false);
    }
});

async function deleteMateria(codigoUnico) {
    if (!confirm(`¿Estás seguro de que quieres eliminar la materia con código: ${codigoUnico}?`)) {
        return;
    }
    showAlert('dashboard-alert', 'Eliminando materia...', 'info');

    try {
        const response = await fetch(`${API_BASE_URL}/materias/${codigoUnico}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Error al eliminar la materia.');
        }

        showAlert('dashboard-alert', 'Materia eliminada exitosamente.', 'success');
        loadMaterias();
    } catch (error) {
        console.error('Error deleting materia:', error);
        showAlert('dashboard-alert', `Error: ${error.message}`, 'error');
    }
}

// --- ESTUDIANTES SECTION FUNCTIONS ---
let currentEstudianteCi = null;
let isEstudianteEditing = false;

async function loadEstudiantes() {
    const tbody = document.getElementById('estudiantes-tbody');
    tbody.innerHTML = '<tr><td colspan="6">Cargando estudiantes...</td></tr>';
    showAlert('dashboard-alert', 'Cargando estudiantes...', 'info');

    try {
        const response = await fetch(`${API_BASE_URL}/estudiantes`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Error al obtener los estudiantes.');
        }

        const estudiantes = await response.json();
        tbody.innerHTML = '';

        if (estudiantes.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6">No hay estudiantes registrados.</td></tr>';
            showAlert('dashboard-alert', 'No hay estudiantes registrados.', 'warning');
            return;
        }

        estudiantes.forEach(estudiante => {
            const row = tbody.insertRow();
            row.insertCell(0).textContent = estudiante.ci;
            row.insertCell(1).textContent = estudiante.nombre;
            row.insertCell(2).textContent = estudiante.apellido;
            row.insertCell(3).textContent = estudiante.email;
            row.insertCell(4).textContent = estudiante.fechaNac; // Assuming format is 'YYYY-MM-DD'

            const actionsCell = row.insertCell(5);
            const editBtn = document.createElement('button');
            editBtn.textContent = 'Editar';
            editBtn.classList.add('btn', 'btn-edit');
            editBtn.onclick = () => openEstudianteModal(true, estudiante);
            actionsCell.appendChild(editBtn);

            const deleteBtn = document.createElement('button');
            deleteBtn.textContent = 'Eliminar';
            deleteBtn.classList.add('btn', 'btn-danger');
            deleteBtn.onclick = () => deleteEstudiante(estudiante.ci);
            actionsCell.appendChild(deleteBtn);
        });
        showAlert('dashboard-alert', 'Estudiantes cargados exitosamente.', 'success');
    } catch (error) {
        console.error('Error al cargar estudiantes:', error);
        tbody.innerHTML = '<tr><td colspan="6">Error al cargar estudiantes.</td></tr>';
        showAlert('dashboard-alert', `Error: ${error.message}`, 'error');
    }
}

function openEstudianteModal(editing = false, estudiante = {}) {
    const modal = document.getElementById('estudiante-modal');
    const title = document.getElementById('estudiante-modal-title');
    const saveBtnText = document.getElementById('estudiante-save-btn-text');
    const form = document.getElementById('estudiante-form');

    isEstudianteEditing = editing;
    clearAlert('estudiante-modal-alert');
    form.reset();

    if (editing) {
        title.textContent = 'Editar Estudiante';
        saveBtnText.textContent = 'Actualizar Estudiante';
        currentEstudianteCi = estudiante.ci;
        document.getElementById('estudiante-ci').value = estudiante.ci;
        document.getElementById('estudiante-ci').disabled = true;
        document.getElementById('estudiante-nombre').value = estudiante.nombre;
        document.getElementById('estudiante-apellido').value = estudiante.apellido;
        document.getElementById('estudiante-email').value = estudiante.email;
        document.getElementById('estudiante-fechaNac').value = estudiante.fechaNac;
    } else {
        title.textContent = 'Agregar Nuevo Estudiante';
        saveBtnText.textContent = 'Guardar Estudiante';
        currentEstudianteCi = null;
        document.getElementById('estudiante-ci').disabled = false;
    }

    modal.style.display = 'flex';
}

function closeEstudianteModal() {
    document.getElementById('estudiante-modal').style.display = 'none';
    clearAlert('estudiante-modal-alert');
}

document.getElementById('estudiante-form').addEventListener('submit', async function(event) {
    event.preventDefault();
    setButtonLoading('estudiante-form .btn-success', isEstudianteEditing ? 'Actualizar Estudiante' : 'Guardar Estudiante', true);
    clearAlert('estudiante-modal-alert');

    const ci = document.getElementById('estudiante-ci').value;
    const nombre = document.getElementById('estudiante-nombre').value;
    const apellido = document.getElementById('estudiante-apellido').value;
    const email = document.getElementById('estudiante-email').value;
    const fechaNac = document.getElementById('estudiante-fechaNac').value;

    const estudianteData = {
        ci,
        nombre,
        apellido,
        email,
        fechaNac: fechaNac // Asegúrate de que el backend acepta este formato de fecha (YYYY-MM-DD)
    };

    try {
        let response;
        if (isEstudianteEditing) {
            response = await fetch(`${API_BASE_URL}/estudiantes/${currentEstudianteCi}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${authToken}`
                },
                body: JSON.stringify(estudianteData)
            });
        } else {
            response = await fetch(`${API_BASE_URL}/estudiantes`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${authToken}`
                },
                body: JSON.stringify(estudianteData)
            });
        }

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Error al guardar el estudiante.');
        }

        showAlert('estudiante-modal-alert', `Estudiante ${isEstudianteEditing ? 'actualizado' : 'agregado'} exitosamente.`, 'success');
        closeEstudianteModal();
        loadEstudiantes();
    } catch (error) {
        console.error('Error saving estudiante:', error);
        showAlert('estudiante-modal-alert', `Error: ${error.message}`, 'error');
    } finally {
        setButtonLoading('estudiante-form .btn-success', isEstudianteEditing ? 'Actualizar Estudiante' : 'Guardar Estudiante', false);
    }
});

async function deleteEstudiante(ci) {
    if (!confirm(`¿Estás seguro de que quieres eliminar al estudiante con C.I.: ${ci}?`)) {
        return;
    }
    showAlert('dashboard-alert', 'Eliminando estudiante...', 'info');

    try {
        const response = await fetch(`${API_BASE_URL}/estudiantes/${ci}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Error al eliminar el estudiante.');
        }

        showAlert('dashboard-alert', 'Estudiante eliminado exitosamente.', 'success');
        loadEstudiantes();
    } catch (error) {
        console.error('Error deleting estudiante:', error);
        showAlert('dashboard-alert', `Error: ${error.message}`, 'error');
    }
}

// --- DOCENTES SECTION FUNCTIONS ---
let currentDocenteCi = null;
let isDocenteEditing = false;

async function loadDocentes() {
    const tbody = document.getElementById('docentes-tbody');
    tbody.innerHTML = '<tr><td colspan="8">Cargando docentes...</td></tr>';
    showAlert('dashboard-alert', 'Cargando docentes...', 'info');

    try {
        const response = await fetch(`${API_BASE_URL}/docentes`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Error al obtener los docentes.');
        }

        const docentes = await response.json();
        tbody.innerHTML = '';

        if (docentes.length === 0) {
            tbody.innerHTML = '<tr><td colspan="8">No hay docentes registrados.</td></tr>';
            showAlert('dashboard-alert', 'No hay docentes registrados.', 'warning');
            return;
        }

        docentes.forEach(docente => {
            const row = tbody.insertRow();
            row.insertCell(0).textContent = docente.ci;
            row.insertCell(1).textContent = docente.nombre;
            row.insertCell(2).textContent = docente.apellido;
            row.insertCell(3).textContent = docente.email;
            row.insertCell(4).textContent = docente.fechaNac; // Assuming format is 'YYYY-MM-DD'
            row.insertCell(5).textContent = docente.departamento || '';
            row.insertCell(6).textContent = docente.noEmpleado || '';

            const actionsCell = row.insertCell(7);
            const editBtn = document.createElement('button');
            editBtn.textContent = 'Editar';
            editBtn.classList.add('btn', 'btn-edit');
            editBtn.onclick = () => openDocenteModal(true, docente);
            actionsCell.appendChild(editBtn);

            const deleteBtn = document.createElement('button');
            deleteBtn.textContent = 'Eliminar';
            deleteBtn.classList.add('btn', 'btn-danger');
            deleteBtn.onclick = () => deleteDocente(docente.ci);
            actionsCell.appendChild(deleteBtn);
        });
        showAlert('dashboard-alert', 'Docentes cargados exitosamente.', 'success');
    } catch (error) {
        console.error('Error al cargar docentes:', error);
        tbody.innerHTML = '<tr><td colspan="8">Error al cargar docentes.</td></tr>';
        showAlert('dashboard-alert', `Error: ${error.message}`, 'error');
    }
}

function openDocenteModal(editing = false, docente = {}) {
    const modal = document.getElementById('docente-modal');
    const title = document.getElementById('docente-modal-title');
    const saveBtnText = document.getElementById('docente-save-btn-text');
    const form = document.getElementById('docente-form');

    isDocenteEditing = editing;
    clearAlert('docente-modal-alert');
    form.reset();

    if (editing) {
        title.textContent = 'Editar Docente';
        saveBtnText.textContent = 'Actualizar Docente';
        currentDocenteCi = docente.ci;
        document.getElementById('docente-ci').value = docente.ci;
        document.getElementById('docente-ci').disabled = true;
        document.getElementById('docente-nombre').value = docente.nombre;
        document.getElementById('docente-apellido').value = docente.apellido;
        document.getElementById('docente-email').value = docente.email;
        document.getElementById('docente-fechaNac').value = docente.fechaNac;
        document.getElementById('docente-departamento').value = docente.departamento || '';
        document.getElementById('docente-noEmpleado').value = docente.noEmpleado || '';
    } else {
        title.textContent = 'Agregar Nuevo Docente';
        saveBtnText.textContent = 'Guardar Docente';
        currentDocenteCi = null;
        document.getElementById('docente-ci').disabled = false;
    }

    modal.style.display = 'flex';
}

function closeDocenteModal() {
    document.getElementById('docente-modal').style.display = 'none';
    clearAlert('docente-modal-alert');
}

document.getElementById('docente-form').addEventListener('submit', async function(event) {
    event.preventDefault();
    setButtonLoading('docente-form .btn-success', isDocenteEditing ? 'Actualizar Docente' : 'Guardar Docente', true);
    clearAlert('docente-modal-alert');

    const ci = document.getElementById('docente-ci').value;
    const nombre = document.getElementById('docente-nombre').value;
    const apellido = document.getElementById('docente-apellido').value;
    const email = document.getElementById('docente-email').value;
    const fechaNac = document.getElementById('docente-fechaNac').value;
    const departamento = document.getElementById('docente-departamento').value;
    const noEmpleado = document.getElementById('docente-noEmpleado').value;

    const docenteData = {
        ci,
        nombre,
        apellido,
        email,
        fechaNac,
        departamento,
        noEmpleado
    };

    try {
        let response;
        if (isDocenteEditing) {
            response = await fetch(`${API_BASE_URL}/docentes/${currentDocenteCi}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${authToken}`
                },
                body: JSON.stringify(docenteData)
            });
        } else {
            response = await fetch(`${API_BASE_URL}/docentes`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${authToken}`
                },
                body: JSON.stringify(docenteData)
            });
        }

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Error al guardar el docente.');
        }

        showAlert('docente-modal-alert', `Docente ${isDocenteEditing ? 'actualizado' : 'agregado'} exitosamente.`, 'success');
        closeDocenteModal();
        loadDocentes();
    } catch (error) {
        console.error('Error saving docente:', error);
        showAlert('docente-modal-alert', `Error: ${error.message}`, 'error');
    } finally {
        setButtonLoading('docente-form .btn-success', isDocenteEditing ? 'Actualizar Docente' : 'Guardar Docente', false);
    }
});

async function deleteDocente(ci) {
    if (!confirm(`¿Estás seguro de que quieres eliminar al docente con C.I.: ${ci}?`)) {
        return;
    }
    showAlert('dashboard-alert', 'Eliminando docente...', 'info');

    try {
        const response = await fetch(`${API_BASE_URL}/docentes/${ci}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Error al eliminar el docente.');
        }

        showAlert('dashboard-alert', 'Docente eliminado exitosamente.', 'success');
        loadDocentes();
    } catch (error) {
        console.error('Error deleting docente:', error);
        showAlert('dashboard-alert', `Error: ${error.message}`, 'error');
    }
}

// --- INSCRIPCIONES SECTION FUNCTIONS ---
let currentInscripcionId = null;
let isInscripcionEditing = false;

async function loadInscripciones() {
    const tbody = document.getElementById('inscripciones-tbody');
    tbody.innerHTML = '<tr><td colspan="4">Cargando inscripciones...</td></tr>';
    showAlert('dashboard-alert', 'Cargando inscripciones...', 'info');

    try {
        const response = await fetch(`${API_BASE_URL}/inscripciones`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Error al obtener las inscripciones.');
        }

        const inscripciones = await response.json();
        tbody.innerHTML = '';

        if (inscripciones.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4">No hay inscripciones registradas.</td></tr>';
            showAlert('dashboard-alert', 'No hay inscripciones registradas.', 'warning');
            return;
        }

        inscripciones.forEach(inscripcion => {
            const row = tbody.insertRow();
            row.insertCell(0).textContent = inscripcion.idInscrito;
            row.insertCell(1).textContent = inscripcion.estudianteCi;
            row.insertCell(2).textContent = inscripcion.materiaCodigoUnico;
            row.insertCell(3).textContent = inscripcion.fechaInscripcion;

            const actionsCell = row.insertCell(4);
            const editBtn = document.createElement('button');
            editBtn.textContent = 'Editar';
            editBtn.classList.add('btn', 'btn-edit');
            editBtn.onclick = () => openInscripcionModal(true, inscripcion);
            actionsCell.appendChild(editBtn);

            const deleteBtn = document.createElement('button');
            deleteBtn.textContent = 'Eliminar';
            deleteBtn.classList.add('btn', 'btn-danger');
            deleteBtn.onclick = () => deleteInscripcion(inscripcion.idInscrito);
            actionsCell.appendChild(deleteBtn);
        });
        showAlert('dashboard-alert', 'Inscripciones cargadas exitosamente.', 'success');
    } catch (error) {
        console.error('Error al cargar inscripciones:', error);
        tbody.innerHTML = '<tr><td colspan="4">Error al cargar inscripciones.</td></tr>';
        showAlert('dashboard-alert', `Error: ${error.message}`, 'error');
    }
}

function openInscripcionModal(editing = false, inscripcion = {}) {
    const modal = document.getElementById('enrollment-modal');
    const title = document.getElementById('modal-title'); // Uses existing 'modal-title'
    const saveBtnText = document.getElementById('save-btn-text'); // Uses existing 'save-btn-text'
    const form = document.getElementById('enrollment-form');

    isInscripcionEditing = editing;
    clearAlert('enrollment-modal-alert');
    form.reset();

    if (editing) {
        title.textContent = 'Editar Inscripción';
        saveBtnText.textContent = 'Actualizar Inscripción';
        currentInscripcionId = inscripcion.idInscrito;
        document.getElementById('enrollment-id').value = inscripcion.idInscrito; // Hidden input
        document.getElementById('enrollment-estudiante-ci').value = inscripcion.estudianteCi;
        document.getElementById('enrollment-materia-codigo').value = inscripcion.materiaCodigoUnico;
        document.getElementById('enrollment-fecha-inscripcion').value = inscripcion.fechaInscripcion;
        // Optionally disable CI and Materia Code if they are part of a composite key and should not be edited
        // document.getElementById('enrollment-estudiante-ci').disabled = true;
        // document.getElementById('enrollment-materia-codigo').disabled = true;
    } else {
        title.textContent = 'Registrar Nueva Inscripción';
        saveBtnText.textContent = 'Guardar Inscripción';
        currentInscripcionId = null;
        document.getElementById('enrollment-id').value = '';
        // Enable CI and Materia Code for new entries
        // document.getElementById('enrollment-estudiante-ci').disabled = false;
        // document.getElementById('enrollment-materia-codigo').disabled = false;
    }

    modal.style.display = 'flex';
}

function closeInscripcionModal() {
    document.getElementById('enrollment-modal').style.display = 'none';
    clearAlert('enrollment-modal-alert');
}

document.getElementById('enrollment-form').addEventListener('submit', async function(event) {
    event.preventDefault();
    setButtonLoading('enrollment-form .btn-success', isInscripcionEditing ? 'Actualizar Inscripción' : 'Guardar Inscripción', true);
    clearAlert('enrollment-modal-alert');

    const estudianteCi = document.getElementById('enrollment-estudiante-ci').value;
    const materiaCodigoUnico = document.getElementById('enrollment-materia-codigo').value;
    const fechaInscripcion = document.getElementById('enrollment-fecha-inscripcion').value;

    const inscripcionData = {
        estudianteCi,
        materiaCodigoUnico,
        fechaInscripcion
    };

    try {
        let response;
        if (isInscripcionEditing) {
            response = await fetch(`${API_BASE_URL}/inscripciones/${currentInscripcionId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${authToken}`
                },
                body: JSON.stringify(inscripcionData)
            });
        } else {
            response = await fetch(`${API_BASE_URL}/inscripciones`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${authToken}`
                },
                body: JSON.stringify(inscripcionData)
            });
        }

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Error al guardar la inscripción.');
        }

        showAlert('enrollment-modal-alert', `Inscripción ${isInscripcionEditing ? 'actualizada' : 'agregada'} exitosamente.`, 'success');
        closeInscripcionModal();
        loadInscripciones();
    } catch (error) {
        console.error('Error saving enrollment:', error);
        showAlert('enrollment-modal-alert', `Error: ${error.message}`, 'error');
    } finally {
        setButtonLoading('enrollment-form .btn-success', isInscripcionEditing ? 'Actualizar Inscripción' : 'Guardar Inscripción', false);
    }
});

async function deleteInscripcion(idInscrito) {
    if (!confirm(`¿Estás seguro de que quieres eliminar la inscripción con ID: ${idInscrito}?`)) {
        return;
    }
    showAlert('dashboard-alert', 'Eliminando inscripción...', 'info');

    try {
        const response = await fetch(`${API_BASE_URL}/inscripciones/${idInscrito}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Error al eliminar la inscripción.');
        }

        showAlert('dashboard-alert', 'Inscripción eliminada exitosamente.', 'success');
        loadInscripciones();
    } catch (error) {
        console.error('Error deleting enrollment:', error);
        showAlert('dashboard-alert', `Error: ${error.message}`, 'error');
    }
}