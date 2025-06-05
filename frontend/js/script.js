document.addEventListener('DOMContentLoaded', () => {
    const docenteButton = document.getElementById('docente-button');
    const estudianteButton = document.getElementById('estudiante-button');

    docenteButton.addEventListener('click', () => {
        window.location.href = 'docente.html';
    });

    estudianteButton.addEventListener('click', () => {
        // Redirige a la página de login de estudiantes
        window.location.href = 'estudiante.html'; // <- CAMBIO AQUÍ
    });
});