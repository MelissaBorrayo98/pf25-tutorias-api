const API_URL = '/api/sesiones';

document.addEventListener('DOMContentLoaded', () => {
    cargarSesiones();

    const form = document.getElementById('tutoriasForm');
    if (form) {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();

            const materiaId = document.getElementById('materiaId').value;
            const estudiante = document.getElementById('estudiante').value;
            const fechaInput = document.getElementById('fecha').value;
            const duracion = document.getElementById('duracion').value;
            const estado = document.getElementById('estado').value;
            const costo = document.getElementById('costo').value;
            const detalles = document.getElementById('detalles').value;

            const fechaFormateada = fechaInput.includes('T') ? fechaInput : `${fechaInput}T00:00:00`;
            const duracionVal = parseFloat(duracion);
            const costoVal = parseFloat(costo);

            const nuevaSesion = {
                fecha: fechaFormateada,
                fechaHora: fechaFormateada,
                estudiante: estudiante,
                nombreEstudiante: estudiante,
                estado: estado,
                costoTotal: costoVal,
                costo_total: costoVal,
                costo: costoVal,
                costoPorHora: costoVal,
                costo_por_hora: costoVal,
                duracion: duracionVal,
                duracionHoras: duracionVal,
                duracion_horas: duracionVal,
                detalles: detalles,
                detalle: detalles
            };

            try {
                const response = await fetch(`${API_URL}/materia/${materiaId}`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(nuevaSesion)
                });

                if (response.ok) {
                    alert('¡Tutoría registrada con éxito!');
                    form.reset();
                    document.getElementById('estado').value = 'PENDIENTE';
                    cargarSesiones();
                } else {
                    const errorMsg = await response.text();
                    alert('Error al registrar: ' + errorMsg);
                }
            } catch (error) {
                console.error('Error de conexión:', error);
                alert('No se pudo conectar con el servidor.');
            }
        });
    }

    const cargarBtn = document.getElementById('cargarBtn');
    if (cargarBtn) {
        cargarBtn.addEventListener('click', cargarSesiones);
    }
});

async function cargarSesiones() {
    try {
        const response = await fetch(API_URL);
        if (!response.ok) throw new Error('Error al obtener datos');

        const sesiones = await response.json();
        const tbody = document.querySelector('#tablaTutorias tbody');
        tbody.innerHTML = '';

        if (sesiones.length === 0) {
            tbody.innerHTML = `<tr><td colspan="7" style="text-align: center; color: #64748b;">No hay sesiones registradas todavía.</td></tr>`;
            return;
        }

        sesiones.forEach(s => {
            // Imprime el objeto completo en la consola del navegador (F12) para inspección
            console.log("Objeto JSON recibido:", s);

            const idSesion = s.id !== undefined ? s.id : (s.sesionId !== undefined ? s.sesionId : '');

            const fechaRaw = s.fechaHora || s.fecha || s.fechaSesion || s.fecha_hora || '';
            const fechaStr = fechaRaw ? fechaRaw.replace('T', ' ') : '';

            const estudiante = s.estudiante || s.nombreEstudiante || 'Sin registrar';
            const estado = s.estado || 'PENDIENTE';

            // Captura exhaustiva del costo en todas sus posibles variantes
            let costoVal = s.costoTotal !== undefined ? s.costoTotal :
                (s.costo_total !== undefined ? s.costo_total :
                    (s.costo !== undefined ? s.costo :
                        (s.costoPorHora !== undefined ? s.costoPorHora :
                            (s.costo_por_hora !== undefined ? s.costo_por_hora : '-'))));

            // Captura exhaustiva de la materia (revisando materia_nombre, objetos, etc.)
            let materia = 'No asignada';
            if (s.materia_nombre) {
                materia = s.materia_nombre;
            } else if (s.materiaNombre) {
                materia = s.materiaNombre;
            } else if (s.materia) {
                if (typeof s.materia === 'string') {
                    materia = s.materia;
                } else if (typeof s.materia === 'object' && s.materia !== null) {
                    materia = s.materia.nombre || s.materia.descripcion || s.materia.nombreMateria || 'Asignada';
                }
            } else if (s.detalles) {
                materia = s.detalles;
            }

            const costoStr = (costoVal !== '-' && costoVal !== null && costoVal !== undefined) ? `Q${costoVal}` : '-';

            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${idSesion}</td>
                <td>${fechaStr}</td>
                <td>${materia}</td>
                <td>${estudiante}</td>
                <td><span style="background: #e0f2fe; color: #0369a1; padding: 4px 8px; border-radius: 6px; font-weight: 600; font-size: 0.85rem;">${estado}</span></td>
                <td>${costoStr}</td>
                <td><button onclick="eliminarSesion(${idSesion})" class="btn-delete">Eliminar</button></td>
            `;
            tbody.appendChild(tr);
        });
    } catch (error) {
        console.error('Error al cargar:', error);
    }
}

async function eliminarSesion(id) {
    if (!confirm('¿Estás segura de eliminar esta sesión de tutoría?')) return;

    try {
        const response = await fetch(`${API_URL}/${id}`, { method: 'DELETE' });
        if (response.ok || response.status === 204) {
            alert('Registro eliminado correctamente');
            cargarSesiones();
        } else {
            alert('No se pudo eliminar el registro.');
        }
    } catch (error) {
        console.error('Error de red:', error);
    }
}