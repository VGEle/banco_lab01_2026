import axios from 'axios';

const api = axios.create({
    baseURL: '/api',
});

export async function listarClientes() {
    const respuesta = await api.get('/customers');
    return respuesta.data;
}

export async function crearCliente(datos) {
    const respuesta = await api.post('/customers', datos);
    return respuesta.data;
}

export async function actualizarCliente(id, datos) {
    const respuesta = await api.put(`/customers/${id}`, datos);
    return respuesta.data;
}

export async function eliminarCliente(id) {
    await api.delete(`/customers/${id}`);
}

export async function transferir(datos) {
    const respuesta = await api.post('/transactions', datos);
    return respuesta.data;
}

export async function consultarHistorial(cuenta) {
    const respuesta = await api.get(
        `/transactions/account/${encodeURIComponent(cuenta)}`
    );
    return respuesta.data;
}
