
        // API Configuration
        const API_BASE_URL = 'http://localhost:8081/api'; // Ajusta seg�n tu configuraci�n
        let authToken = null;
        let currentMode = 'create'; // 'create' or 'edit'

        // Initialize application
        document.addEventListener('DOMContentLoaded', function() {
            // Check if user is already logged in
            const token = sessionStorage.getItem('authToken');
            if (token) {
                authToken = token;
                showDashboard();
                loadStudents();
            }

            // Setup form handlers
            document.getElementById('login-form').addEventListener('submit', handleLogin);
            document.getElementById('student-form').addEventListener('submit', function(e) {
                e.preventDefault();
                saveStudent();
            });
        });

        // Authentication Functions
        async function handleLogin(event) {
            event.preventDefault();
            
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            
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
                    // Asegura que el token se obtiene correctamente
                    authToken = data.token || data.accessToken || data.jwt || null;
                    console.log('Token JWT recibido y guardado:', authToken); // Debug
                    if (!authToken) {
                        showAlert('login-alert', 'No se recibió token JWT del backend', 'error');
                        return;
                    }
                    sessionStorage.setItem('authToken', authToken);
                    sessionStorage.setItem('username', username);
                    
                    showAlert('login-alert', 'Login exitoso!', 'success');
                    setTimeout(() => {
                        showDashboard();
                        loadStudents();
                    }, 1000);
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

        function logout() {
            authToken = null;
            sessionStorage.removeItem('authToken');
            sessionStorage.removeItem('username');
            
            document.getElementById('login-section').classList.remove('hidden');
            document.getElementById('dashboard-section').classList.add('hidden');
            
            // Clear form
            document.getElementById('login-form').reset();
            clearAlert('login-alert');
        }

        function showDashboard() {
            document.getElementById('login-section').classList.add('hidden');
            document.getElementById('dashboard-section').classList.remove('hidden');
            
            const username = sessionStorage.getItem('username') || 'Usuario';
            document.getElementById('current-user').textContent = `Usuario: ${username}`;
        }

        // Student CRUD Functions
        async function loadStudents() {
            try {
                if (!authToken) {
                    console.warn('No hay token JWT presente al intentar cargar estudiantes'); // Debug
                    logout();
                    return;
                }
                console.log('Enviando token JWT en loadStudents:', authToken); // Debug
                const response = await fetch(`${API_BASE_URL}/estudiantes`, {
                    headers: {
                        'Authorization': `Bearer ${authToken}`,
                        'Content-Type': 'application/json'
                    }
                });
                console.log('Load students status:', response.status); // Debug
                if (response.ok) {
                    const students = await response.json();
                    displayStudents(students);
                } else if (response.status === 401 || response.status === 403) {
                    showAlert('dashboard-alert', 'Sesión expirada o no autorizada. Por favor, inicia sesión de nuevo.', 'error');
                    logout();
                } else {
                    showAlert('dashboard-alert', 'Error al cargar estudiantes', 'error');
                }
            } catch (error) {
                console.error('Error loading students:', error);
                showAlert('dashboard-alert', 'Error de conexion 1', 'error');
            }
        }

        function displayStudents(students) {
            const tbody = document.getElementById('students-tbody');
            tbody.innerHTML = '';
            students.forEach(student => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${student.id}</td>
                    <td>${student.nombre}</td>
                    <td>${student.apellido}</td>
                    <td>${student.email}</td>
                    <td>${student.numeroInscripcion}</td>
                    <td>${student.estado}</td>
                    <td>${student.fechaNacimiento ? student.fechaNacimiento : ''}</td>
                    <td>
                        <div class="action-buttons">
                            <button class="btn btn-sm btn-secondary" onclick="editStudent(${student.id})">Editar</button>
                            <button class="btn btn-sm btn-danger" onclick="deleteStudent(${student.id})">Eliminar</button>
                        </div>
                    </td>
                `;
                tbody.appendChild(row);
            });
        }

        async function saveStudent() {
            const form = document.getElementById('student-form');
            const formData = new FormData(form);
            const fechaNacimiento = document.getElementById('student-fecha-nacimiento').value;
            console.log('Valor de fechaNacimiento leído del input:', fechaNacimiento); // Debug
            const username = sessionStorage.getItem('username') || '';
            // Formatear la fecha actual a dd/MM/YYYY
            function formatDateDDMMYYYY(date) {
                const d = new Date(date);
                const day = String(d.getDate()).padStart(2, '0');
                const month = String(d.getMonth() + 1).padStart(2, '0');
                const year = d.getFullYear();
                return `${year}-${month}-${day}`;
            }
            const now = new Date();
            const fechaAltaFormateada = formatDateDDMMYYYY(now);
            const fechaModificacionFormateada = formatDateDDMMYYYY(now);
            const studentData = {
                nombre: formData.get('nombre'),
                apellido: formData.get('apellido'),
                email: formData.get('email'),
                numeroInscripcion: formData.get('numeroInscripcion'),
                estado: formData.get('estado'),
                fechaNacimiento: fechaNacimiento,
                usuarioAlta: currentMode === 'create' ? username : undefined,
                usuarioModificacion: currentMode === 'edit' ? username : undefined,
                fechaAlta: currentMode === 'create' ? fechaAltaFormateada : undefined,
                fechaModificacion: currentMode === 'edit' ? fechaModificacionFormateada : undefined
            };
            setSaveLoading(true);
            clearAlert('modal-alert');
            try {
                if (!authToken) {
                    console.warn('No hay token JWT presente al intentar guardar estudiante'); // Debug
                    logout();
                    return;
                }
                console.log('Enviando token JWT en saveStudent:', authToken); // Debug
                console.log('JSON enviado en saveStudent:', JSON.stringify(studentData)); // Debug
                let response;
                if (currentMode === 'create') {
                    // Elimina campos undefined para evitar enviarlos en el JSON
                    const cleanData = Object.fromEntries(Object.entries(studentData).filter(([_, v]) => v !== undefined));
                    response = await fetch(`${API_BASE_URL}/estudiantes`, {
                        method: 'POST',
                        headers: {
                            'Authorization': `Bearer ${authToken}`,
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(cleanData)
                    });
                } else {
                    const studentId = document.getElementById('student-id').value;
                    const editData = {...studentData, id: parseInt(studentId)};
                    const cleanEditData = Object.fromEntries(Object.entries(editData).filter(([_, v]) => v !== undefined));
                    console.log('JSON enviado en editStudent:', JSON.stringify(cleanEditData)); // Debug
                    response = await fetch(`${API_BASE_URL}/estudiantes/${studentId}`, {
                        method: 'PUT',
                        headers: {
                            'Authorization': `Bearer ${authToken}`,
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(cleanEditData)
                    });
                }
                console.log('Save student status:', response.status); // Debug
                if (response.ok) {
                    showAlert('dashboard-alert', 
                        currentMode === 'create' ? 'Estudiante creado exitosamente' : 'Estudiante actualizado exitosamente', 
                        'success');
                    closeModal();
                    loadStudents();
                } else if (response.status === 401 || response.status === 403) {
                    showAlert('modal-alert', 'Sesión expirada o no autorizada. Por favor, inicia sesión de nuevo.', 'error');
                    logout();
                } else {
                    const errorData = await response.json();
                    showAlert('modal-alert', errorData.message || 'Error al guardar estudiante', 'error');
                }
            } catch (error) {
                console.error('Error saving student:', error);
                showAlert('modal-alert', 'Error de conexión', 'error');
            } finally {
                setSaveLoading(false);
            }
        }

        async function editStudent(id) {
            try {
                if (!authToken) {
                    console.warn('No hay token JWT presente al intentar editar estudiante'); // Debug
                    logout();
                    return;
                }
                console.log('Enviando token JWT en editStudent:', authToken); // Debug
                const response = await fetch(`${API_BASE_URL}/estudiantes/${id}/lock`, {
                    headers: {
                        'Authorization': `Bearer ${authToken}`,
                        'Content-Type': 'application/json'
                    }
                });
                console.log('Edit student status:', response.status); // Debug
                if (response.ok) {
                    const student = await response.json();
                    document.getElementById('student-id').value = student.id;
                    document.getElementById('student-name').value = student.nombre;
                    document.getElementById('student-lname').value = student.apellido;
                    document.getElementById('student-email').value = student.email;
                    document.getElementById('student-age').value = student.numeroInscripcion;
                    document.getElementById('student-career').value = student.estado;
                    document.getElementById('student-fecha-nacimiento').value = student.fechaNacimiento ? student.fechaNacimiento : '';
                    openModal('edit');
                } else if (response.status === 401 || response.status === 403) {
                    showAlert('dashboard-alert', 'Sesión expirada o no autorizada. Por favor, inicia sesión de nuevo.', 'error');
                    logout();
                } else {
                    showAlert('dashboard-alert', 'Error al cargar datos del estudiante', 'error');
                }
            } catch (error) {
                console.error('Error loading student:', error);
                showAlert('dashboard-alert', 'Error de conexión', 'error');
            }
        }

        async function deleteStudent(id) {
            if (!confirm('¿Estás seguro de que deseas eliminar este estudiante?')) {
                return;
            }
            try {
                if (!authToken) {
                    console.warn('No hay token JWT presente al intentar eliminar estudiante'); // Debug
                    logout();
                    return;
                }
                console.log('Enviando token JWT en deleteStudent:', authToken); // Debug
                const response = await fetch(`${API_BASE_URL}/estudiantes/${id}`, {
                    method: 'DELETE',
                    headers: {
                        'Authorization': `Bearer ${authToken}`,
                        'Content-Type': 'application/json'
                    }
                });
                console.log('Delete student status:', response.status); // Debug
                if (response.ok) {
                    showAlert('dashboard-alert', 'Estudiante eliminado exitosamente', 'success');
                    loadStudents();
                } else if (response.status === 401 || response.status === 403) {
                    showAlert('dashboard-alert', 'Sesión expirada o no autorizada. Por favor, inicia sesión de nuevo.', 'error');
                    logout();
                } else {
                    showAlert('dashboard-alert', 'Error al eliminar estudiante', 'error');
                }
            } catch (error) {
                console.error('Error deleting student:', error);
                showAlert('dashboard-alert', 'Error de conexión', 'error');
            }
        }

        // Modal Functions
        function openModal(mode) {
            currentMode = mode;
            const modal = document.getElementById('student-modal');
            const title = document.getElementById('modal-title');
            const form = document.getElementById('student-form');
            
            if (mode === 'create') {
                title.textContent = 'Agregar Estudiante';
                form.reset();
                document.getElementById('student-id').value = '';
            } else {
                title.textContent = 'Editar Estudiante';
            }
            
            clearAlert('modal-alert');
            modal.style.display = 'block';
        }

        function closeModal() {
            document.getElementById('student-modal').style.display = 'none';
            document.getElementById('student-form').reset();
            clearAlert('modal-alert');
        }

        // Utility Functions
        function showAlert(containerId, message, type) {
            const container = document.getElementById(containerId);
            container.innerHTML = `<div class="alert alert-${type}">${message}</div>`;
            
            // Auto-hide success alerts
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
            const btn = document.querySelector('#login-form .btn');
            
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
            const modal = document.getElementById('student-modal');
            if (event.target === modal) {
                closeModal();
            }
        }
    