const API_SESIONES = '/api/sesiones';
const API_MATERIAS = '/api/materias';

let sesionEditandoId = null;   // null = modo "registrar"; número = modo "editar"
let filtroPendientes = false;
let timerMensaje = null;

document.addEventListener('DOMContentLoaded', () => {
    cargarMaterias();
    cargarSesiones();

    document.getElementById('tutoriasForm').addEventListener('submit', guardarSesion);
    document.getElementById('cancelarEdicionBtn').addEventListener('click', salirModoEdicion);
    document.getElementById('materiaForm').addEventListener('submit', guardarMateria);
    document.getElementById('totalBtn').addEventListener('click', consultarTotal);

    document.getElementById('cargarBtn').addEventListener('click', () => {
        filtroPendientes = false;
        cargarSesiones();
    });
    document.getElementById('pendientesBtn').addEventListener('click', () => {
        filtroPendientes = true;
        cargarSesiones();
    });
});

/* ------------------------------------------------------------ Utilidades */

function mostrarMensaje(texto, tipo) {
    const caja = document.getElementById('mensaje');
    caja.textContent = texto;
    caja.className = 'mensaje ' + (tipo === 'error' ? 'mensaje-error' : 'mensaje-ok');
    caja.hidden = false;
    clearTimeout(timerMensaje);
    timerMensaje = setTimeout(() => { caja.hidden = true; }, 7000);
}

// Lee el formato de error del backend: { status, error, errores: { campo: detalle } }
async function leerError(response) {
    try {
        const data = await response.json();
        let texto = data.error || ('Error ' + response.status);
        if (data.errores) {
            texto += ' ' + Object.values(data.errores).join(' ');
        }
        return texto;
    } catch (e) {
        return 'Error ' + response.status;
    }
}

// El input datetime-local devuelve "2026-11-10T16:00"; el backend espera segundos
function aFechaIso(valor) {
    return valor.length === 16 ? valor + ':00' : valor;
}

function formatoQuetzales(valor) {
    return (valor === null || valor === undefined) ? '-' : 'Q' + Number(valor).toFixed(2);
}

function crearCelda(texto) {
    const td = document.createElement('td');
    td.textContent = texto;   // textContent evita inyección de HTML
    return td;
}

/* --------------------------------------------------------------- Materias */

async function cargarMaterias(seleccionarId) {
    const select = document.getElementById('materiaId');
    try {
        const response = await fetch(API_MATERIAS);
        if (!response.ok) throw new Error('HTTP ' + response.status);
        const materias = await response.json();

        const valorPrevio = seleccionarId !== undefined ? String(seleccionarId) : select.value;
        select.innerHTML = '';

        if (materias.length === 0) {
            select.appendChild(new Option('No hay materias: registre una primero', ''));
            return;
        }
        select.appendChild(new Option('Seleccione una materia', ''));
        materias.forEach(m => {
            select.appendChild(new Option(`${m.nombre} (${formatoQuetzales(m.tarifaHora)}/hora)`, m.id));
        });
        select.value = valorPrevio;
    } catch (error) {
        console.error('Error al cargar materias:', error);
        select.innerHTML = '';
        select.appendChild(new Option('No se pudieron cargar las materias', ''));
    }
}

async function guardarMateria(e) {
    e.preventDefault();
    const form = e.target;

    const materia = {
        nombre: document.getElementById('materiaNombre').value.trim(),
        descripcion: document.getElementById('materiaDescripcion').value.trim(),
        tarifaHora: parseFloat(document.getElementById('materiaTarifa').value)
    };

    try {
        const response = await fetch(API_MATERIAS, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(materia)
        });

        if (response.ok) {
            const nueva = await response.json();
            mostrarMensaje('Materia registrada correctamente.', 'ok');
            form.reset();
            await cargarMaterias(nueva.id);
        } else {
            mostrarMensaje(await leerError(response), 'error');
        }
    } catch (error) {
        console.error('Error de conexión:', error);
        mostrarMensaje('No se pudo conectar con el servidor.', 'error');
    }
}

/* --------------------------------------------------------------- Sesiones */

async function guardarSesion(e) {
    e.preventDefault();

    const materiaId = document.getElementById('materiaId').value;
    if (sesionEditandoId === null && !materiaId) {
        mostrarMensaje('Seleccione una materia.', 'error');
        return;
    }

    // El costo NO se envía: lo calcula el backend (duración x tarifa de la materia)
    const sesion = {
        fechaHora: aFechaIso(document.getElementById('fecha').value),
        duracion: parseFloat(document.getElementById('duracion').value),
        estudiante: document.getElementById('estudiante').value.trim(),
        estado: document.getElementById('estado').value
    };

    const editando = sesionEditandoId !== null;
    const url = editando ? `${API_SESIONES}/${sesionEditandoId}` : `${API_SESIONES}/materia/${materiaId}`;

    try {
        const response = await fetch(url, {
            method: editando ? 'PUT' : 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(sesion)
        });

        if (response.ok) {
            mostrarMensaje(editando ? 'Tutoría actualizada correctamente.' : 'Tutoría registrada correctamente.', 'ok');
            salirModoEdicion();
            cargarSesiones();
        } else {
            mostrarMensaje(await leerError(response), 'error');
        }
    } catch (error) {
        console.error('Error de conexión:', error);
        mostrarMensaje('No se pudo conectar con el servidor.', 'error');
    }
}

function entrarModoEdicion(s) {
    sesionEditandoId = s.id;

    const selectMateria = document.getElementById('materiaId');
    selectMateria.value = String(s.materiaId);
    selectMateria.disabled = true;   // la materia no se cambia al editar

    document.getElementById('estudiante').value = s.estudiante || '';
    document.getElementById('fecha').value = s.fechaHora ? s.fechaHora.substring(0, 16) : '';
    document.getElementById('duracion').value = s.duracion;
    document.getElementById('estado').value = s.estado;

    document.getElementById('formTitulo').textContent = '✏️ Editar Tutoría #' + s.id;
    document.getElementById('guardarBtn').textContent = 'Actualizar Tutoría';
    document.getElementById('cancelarEdicionBtn').hidden = false;
    document.getElementById('tutoriasForm').scrollIntoView({ behavior: 'smooth' });
}

function salirModoEdicion() {
    sesionEditandoId = null;
    document.getElementById('tutoriasForm').reset();
    document.getElementById('materiaId').disabled = false;
    document.getElementById('estado').value = 'PENDIENTE';
    document.getElementById('formTitulo').textContent = '📚 Registrar Nueva Tutoría';
    document.getElementById('guardarBtn').textContent = 'Guardar Tutoría';
    document.getElementById('cancelarEdicionBtn').hidden = true;
}

async function cargarSesiones() {
    const tbody = document.querySelector('#tablaTutorias tbody');
    document.getElementById('tituloLista').textContent =
        filtroPendientes ? '⏳ Sesiones Pendientes' : '📋 Listado de Sesiones Registradas';

    try {
        const url = filtroPendientes ? `${API_SESIONES}/pendientes` : API_SESIONES;
        const response = await fetch(url);
        if (!response.ok) throw new Error('HTTP ' + response.status);
        const sesiones = await response.json();

        tbody.innerHTML = '';

        if (sesiones.length === 0) {
            const tr = document.createElement('tr');
            const td = crearCelda('No hay sesiones para mostrar.');
            td.colSpan = 8;
            td.className = 'tabla-vacia';
            tr.appendChild(td);
            tbody.appendChild(tr);
            return;
        }

        sesiones.forEach(s => tbody.appendChild(crearFila(s)));
    } catch (error) {
        console.error('Error al cargar sesiones:', error);
        mostrarMensaje('No se pudieron cargar las sesiones.', 'error');
    }
}

function crearFila(s) {
    const tr = document.createElement('tr');

    tr.appendChild(crearCelda(s.id));
    tr.appendChild(crearCelda(s.fechaHora ? s.fechaHora.replace('T', ' ') : ''));
    tr.appendChild(crearCelda(s.materiaNombre || 'No asignada'));
    tr.appendChild(crearCelda(s.estudiante || 'Sin registrar'));
    tr.appendChild(crearCelda(s.duracion));

    const tdEstado = document.createElement('td');
    const badge = document.createElement('span');
    badge.className = 'estado estado-' + String(s.estado || '').toLowerCase();
    badge.textContent = s.estado;
    tdEstado.appendChild(badge);
    tr.appendChild(tdEstado);

    tr.appendChild(crearCelda(formatoQuetzales(s.costoTotal)));

    const tdAcciones = document.createElement('td');
    tdAcciones.className = 'acciones';

    const btnEditar = document.createElement('button');
    btnEditar.type = 'button';
    btnEditar.className = 'btn-edit';
    btnEditar.textContent = 'Editar';
    btnEditar.addEventListener('click', () => entrarModoEdicion(s));

    const btnEliminar = document.createElement('button');
    btnEliminar.type = 'button';
    btnEliminar.className = 'btn-delete';
    btnEliminar.textContent = 'Eliminar';
    btnEliminar.addEventListener('click', () => eliminarSesion(s.id));

    tdAcciones.appendChild(btnEditar);
    tdAcciones.appendChild(btnEliminar);
    tr.appendChild(tdAcciones);

    return tr;
}

async function eliminarSesion(id) {
    if (!confirm('¿Está segura de eliminar esta sesión de tutoría?')) return;

    try {
        const response = await fetch(`${API_SESIONES}/${id}`, { method: 'DELETE' });
        if (response.ok) {
            mostrarMensaje('Sesión eliminada correctamente.', 'ok');
            if (sesionEditandoId === id) salirModoEdicion();
            cargarSesiones();
        } else {
            mostrarMensaje(await leerError(response), 'error');
        }
    } catch (error) {
        console.error('Error de red:', error);
        mostrarMensaje('No se pudo conectar con el servidor.', 'error');
    }
}

/* ------------------------------------------------------------- Indicador */

async function consultarTotal() {
    const inicio = document.getElementById('totalInicio').value;
    const fin = document.getElementById('totalFin').value;
    const resultado = document.getElementById('totalResultado');

    if (!inicio || !fin) {
        mostrarMensaje('Indique la fecha de inicio y la de fin.', 'error');
        return;
    }

    try {
        const url = `${API_SESIONES}/total-dia?inicio=${encodeURIComponent(aFechaIso(inicio))}&fin=${encodeURIComponent(aFechaIso(fin))}`;
        const response = await fetch(url);
        if (response.ok) {
            resultado.textContent = formatoQuetzales(await response.json());
        } else {
            resultado.textContent = '—';
            mostrarMensaje(await leerError(response), 'error');
        }
    } catch (error) {
        console.error('Error de red:', error);
        mostrarMensaje('No se pudo conectar con el servidor.', 'error');
    }
}
