// Add this to your existing script.js
document.addEventListener('DOMContentLoaded', function() {
    const docenteButton = document.getElementById('docente-button');
    const estudianteButton = document.getElementById('estudiante-button');
    const adminButton = document.getElementById('admin-button'); // Get the new admin button

    if (docenteButton) {
        docenteButton.addEventListener('click', function() {
            window.location.href = 'docente.html'; // Adjust if your docente login page has a different name
        });
    }

    if (estudianteButton) {
        estudianteButton.addEventListener('click', function() {
            window.location.href = 'estudiante.html'; // Adjust if your estudiante login page has a different name
        });
    }

    if (adminButton) {
        adminButton.addEventListener('click', function() {
            window.location.href = 'admin.html'; // This will be your admin login page
        });
    }
});