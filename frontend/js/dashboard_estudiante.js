// API Configuration
const API_BASE_URL = 'http://localhost:8081/api'; // Adjust according to your backend configuration
let authToken = null;
let studentEmail = null;
let studentCi = null; // We'll need to fetch the CI of the logged-in student

// DOMContentLoaded: Ensures the DOM is fully loaded before executing scripts
document.addEventListener('DOMContentLoaded', async function() {
    // Check for authentication token and student email in session storage
    authToken = sessionStorage.getItem('authToken');
    studentEmail = sessionStorage.getItem('studentEmail');

    if (!authToken || !studentEmail) {
        // If no token or email, redirect to login page
        window.location.href = 'index.html'; // Assuming index.html is your login page
        return;
    }

    // Display student email on the dashboard
    document.getElementById('student-email-display').textContent = `Email: ${studentEmail}`;

    // Fetch the student's CI using their email
    await fetchStudentCi(studentEmail);

    // Load available subjects and enrolled subjects
    if (studentCi) {
        await loadAvailableSubjects();
        await loadMyEnrollments();
    } else {
        showAlert('dashboard-alert', 'No se pudo obtener la información del estudiante. Por favor, intente iniciar sesión nuevamente.', 'error');
    }

    // Add event listener for logout button
    document.getElementById('logout-btn').addEventListener('click', handleLogout);

    // Add event listener for enrollment form submission
    document.getElementById('enrollment-form').addEventListener('submit', handleEnrollmentSubmission);
});

// --- Authentication and User Info Functions ---

function handleLogout() {
    sessionStorage.removeItem('authToken');
    sessionStorage.removeItem('studentEmail');
    window.location.href = 'index.html'; // Redirect to login page
}

async function fetchStudentCi(email) {
    try {
        const response = await fetch(`${API_BASE_URL}/estudiantes/by-email/${encodeURIComponent(email)}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const data = await response.json();
            // Assuming EstudianteDTO contains 'ci' field
            studentCi = data.ci;
            console.log("Student CI fetched:", studentCi);
        } else {
            const errorData = await response.json();
            console.error('Error fetching student CI:', errorData.message);
            showAlert('dashboard-alert', `Error al obtener la cédula del estudiante: ${errorData.message}`, 'error');
            studentCi = null;
        }
    } catch (error) {
        console.error('Network error fetching student CI:', error);
        showAlert('dashboard-alert', 'Error de conexión al obtener la cédula del estudiante.', 'error');
        studentCi = null;
    }
}

// --- Subject Management Functions ---

async function loadAvailableSubjects() {
    const tbody = document.getElementById('available-subjects-tbody');
    tbody.innerHTML = '<tr><td colspan="4">Cargando materias disponibles...</td></tr>';
    clearAlert('dashboard-alert');

    try {
        const response = await fetch(`${API_BASE_URL}/materias`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const subjects = await response.json();
            tbody.innerHTML = ''; // Clear loading message

            if (subjects.length === 0) {
                tbody.innerHTML = '<tr><td colspan="4">No hay materias disponibles en este momento.</td></tr>';
                return;
            }

            subjects.forEach(subject => {
                const row = tbody.insertRow();
                row.insertCell().textContent = subject.codigoUnico;
                row.insertCell().textContent = subject.nombreMateria; // This is directly available in MateriaDTO
                row.insertCell().textContent = subject.descripcion;

                const actionCell = row.insertCell();
                const enrollButton = document.createElement('button');
                enrollButton.textContent = 'Inscribirme';
                enrollButton.classList.add('btn', 'btn-primary');
                enrollButton.onclick = () => openEnrollmentModal(subject.codigoUnico, subject.nombreMateria);
                actionCell.appendChild(enrollButton);
            });
        } else {
            const errorData = await response.json();
            showAlert('dashboard-alert', `Error al cargar materias disponibles: ${errorData.message}`, 'error');
            tbody.innerHTML = '<tr><td colspan="4">Error al cargar materias disponibles.</td></tr>';
        }
    } catch (error) {
        console.error('Error loading available subjects:', error);
        showAlert('dashboard-alert', 'Error de conexión al cargar materias disponibles.', 'error');
        tbody.innerHTML = '<tr><td colspan="4">Error de conexión.</td></tr>';
    }
}

async function loadMyEnrollments() {
    const tbody = document.getElementById('my-enrollments-tbody');
    tbody.innerHTML = '<tr><td colspan="4">Cargando tus inscripciones...</td></tr>';
    clearAlert('dashboard-alert');

    if (!studentCi) {
        tbody.innerHTML = '<tr><td colspan="4">No se pudo cargar las inscripciones sin la cédula del estudiante.</td></tr>';
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/inscripciones/estudiante/${studentCi}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const enrollments = await response.json();
            tbody.innerHTML = ''; // Clear loading message

            if (enrollments.length === 0) {
                tbody.innerHTML = '<tr><td colspan="4">No estás inscrito en ninguna materia todavía.</td></tr>';
                return;
            }

            // --- START OF REQUIRED CHANGES FOR BACKEND DATA STRUCTURE ---
            // We need to fetch materia names because InscritoDTO does not include them directly.
            // Create an array of promises to fetch materia details for each enrollment.
            const enrollmentsWithMateriaDetailsPromises = enrollments.map(async enrollment => {
                let materiaNombre = 'Cargando nombre...'; // Default until fetched

                if (enrollment.materiaCodigoUnico) {
                    try {
                        // Assuming an endpoint like /api/materias/{codigoUnico} exists
                            const materiaResponse = await fetch(`<span class="math-inline">\{API\_BASE\_URL\}/materias/by\-codigo/</span>{enrollment.materiaCodigoUnico}`, {                            method: 'GET',
                            headers: {
                                'Authorization': `Bearer ${authToken}`,
                                'Content-Type': 'application/json'
                            }
                        });

                        if (materiaResponse.ok) {
                            const materiaData = await materiaResponse.json();
                            materiaNombre = materiaData.nombreMateria; // MateriaDTO has nombreMateria
                        } else {
                            console.warn(`Could not fetch details for materia ${enrollment.materiaCodigoUnico}. Status: ${materiaResponse.status}`);
                            materiaNombre = `Error: ${enrollment.materiaCodigoUnico}`;
                        }
                    } catch (materiaError) {
                        console.error(`Network error fetching materia ${enrollment.materiaCodigoUnico}:`, materiaError);
                        materiaNombre = `Error: ${enrollment.materiaCodigoUnico}`;
                    }
                } else {
                    materiaNombre = 'N/A (Código no disponible)';
                }

                // Return the enrollment with the fetched materia name added as a new property
                return {
                    ...enrollment,
                    materiaNombre: materiaNombre,
                };
            });

            // Wait for all materia details to be fetched before rendering the table
            const enrollmentsWithDetails = await Promise.all(enrollmentsWithMateriaDetailsPromises);
            // --- END OF REQUIRED CHANGES ---

            enrollmentsWithDetails.forEach(enrollment => { // Iterate through the enriched enrollments
                const row = tbody.insertRow();
                row.insertCell().textContent = enrollment.materiaCodigoUnico; // Use materiaCodigoUnico directly
                row.insertCell().textContent = enrollment.materiaNombre;    // Use the fetched materiaNombre
                row.insertCell().textContent = enrollment.fechaInscripcion; // Display the date as returned

                const actionCell = row.insertCell();
                const unenrollButton = document.createElement('button');
                unenrollButton.textContent = 'Dar de baja';
                unenrollButton.classList.add('btn', 'btn-danger');
                // Pass materiaNombre to confirmUnenroll for a better user confirmation message
                unenrollButton.onclick = () => confirmUnenroll(studentCi, enrollment.materiaCodigoUnico, enrollment.materiaNombre);
                actionCell.appendChild(unenrollButton);
            });
        } else {
            const errorData = await response.json();
            showAlert('dashboard-alert', `Error al cargar tus inscripciones: ${errorData.message}`, 'error');
            tbody.innerHTML = '<tr><td colspan="4">Error al cargar tus inscripciones.</td></tr>';
        }
    } catch (error) {
        console.error('Error loading enrollments:', error);
        showAlert('dashboard-alert', 'Error de conexión al cargar tus inscripciones.', 'error');
        tbody.innerHTML = '<tr><td colspan="4">Error de conexión.</td></tr>';
    }
}

// --- Enrollment Modal Functions ---

function openEnrollmentModal(codigoUnico, nombreMateria) {
    document.getElementById('modal-title').textContent = 'Inscribir Materia';
    document.getElementById('enrollment-materia-codigo').value = codigoUnico;
    document.getElementById('enrollment-materia-nombre').value = nombreMateria;
    document.getElementById('enrollment-id').value = ''; // Clear any existing enrollment ID if re-using modal for edit
    document.getElementById('save-btn-text').textContent = 'Guardar Inscripción';
    clearAlert('modal-alert');
    document.getElementById('enrollment-modal').style.display = 'block';
}

function closeModal() {
    document.getElementById('enrollment-modal').style.display = 'none';
    clearAlert('modal-alert');
}

async function handleEnrollmentSubmission(event) {
    event.preventDefault();

    const materiaCodigo = document.getElementById('enrollment-materia-codigo').value;

    setModalLoading(true);
    clearAlert('modal-alert');

    if (!studentCi) {
        showAlert('modal-alert', 'No se pudo inscribir. Información del estudiante no disponible.', 'error');
        setModalLoading(false);
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/inscripciones`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                estudianteCi: studentCi,
                materiaCodigoUnico: materiaCodigo,
                fechaInscripcion: new Date().toISOString().slice(0, 10) // Automatically set current date for new enrollment
            })
        });

        const data = await response.json();

        if (response.ok) {
            showAlert('modal-alert', 'Inscripción realizada con éxito!', 'success');
            await loadAvailableSubjects(); // Refresh available subjects
            await loadMyEnrollments(); // Refresh enrolled subjects
            setTimeout(() => closeModal(), 1500); // Close modal after success
        } else {
            showAlert('modal-alert', data.message || 'Error al inscribir la materia.', 'error');
        }
    } catch (error) {
        console.error('Enrollment error:', error);
        showAlert('modal-alert', 'Error de conexión al inscribir la materia.', 'error');
    } finally {
        setModalLoading(false);
    }
}

function confirmUnenroll(estudianteCi, materiaCodigoUnico, materiaNombre) {
    if (confirm(`¿Estás seguro de que quieres dar de baja la materia: ${materiaNombre} (${materiaCodigoUnico})?`)) {
        unenrollSubject(estudianteCi, materiaCodigoUnico);
    }
}

async function unenrollSubject(estudianteCi, materiaCodigoUnico) {
    clearAlert('dashboard-alert');
    try {
        // This assumes your DELETE endpoint can uniquely identify the enrollment
        // based on estudianteCi and materiaCodigoUnico (e.g., if only one enrollment per student per subject is allowed).
        // If a student can be enrolled in the same subject multiple times on different dates,
        // you might need to send the 'idInscrito' or 'fechaInscripcion' for precise deletion.
        const response = await fetch(`${API_BASE_URL}/inscripciones/estudiante/${estudianteCi}/materia/${materiaCodigoUnico}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${authToken}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            showAlert('dashboard-alert', 'Materia dada de baja exitosamente.', 'success');
            await loadMyEnrollments(); // Refresh enrolled subjects
            await loadAvailableSubjects(); // Refresh available subjects (in case it affects what's "available" like preventing re-enrollment)
        } else {
            const errorData = await response.json();
            showAlert('dashboard-alert', errorData.message || 'Error al dar de baja la materia.', 'error');
        }
    } catch (error) {
        console.error('Unenrollment error:', error);
        showAlert('dashboard-alert', 'Error de conexión al dar de baja la materia.', 'error');
    }
}

// --- Utility Functions ---

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

function setModalLoading(loading) {
    const btnText = document.getElementById('save-btn-text');
    const loadingSpinner = document.getElementById('save-loading');
    const btn = document.querySelector('#enrollment-form button[type="submit"]');

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