import { useState, useEffect } from 'react';
import { listarClientes, consultarHistorial } from '../api/banco';

export default function Historial() {
    const [clientes, setClientes] = useState([]);
    const [cuentaSeleccionada, setCuentaSeleccionada] = useState('');
    const [movimientos, setMovimientos] = useState([]);
    const [cargando, setCargando] = useState(false);
    const [error, setError] = useState('');
    const [buscado, setBuscado] = useState(false);

    // Cargar clientes al abrir la vista para llenar el selector
    useEffect(() => {
        listarClientes()
            .then(data => setClientes(data))
            .catch(() => setError('No fue posible cargar los clientes.'));
    }, []);

    const handleConsultar = async (e) => {
        e.preventDefault();
        if (!cuentaSeleccionada) {
            setError('Debes seleccionar una cuenta.');
            return;
        }

        // Limpiar resultados anteriores al iniciar otra consulta
        setMovimientos([]);
        setError('');
        setCargando(true);
        setBuscado(true);

        try {
            const data = await consultarHistorial(cuentaSeleccionada);
            setMovimientos(data);
        } catch {
            setError('No fue posible consultar el historial.');
        } finally {
            setCargando(false);
        }
    };

    // Ordenar una copia de los movimientos por fecha descendente
    const ordenados = [...movimientos].sort(
        (a, b) => (b.timestamp ?? '').localeCompare(a.timestamp ?? '')
    );

    return (
        <div style={{ padding: '20px', fontFamily: 'Arial' }}>
            <h2>Historial de Movimientos por Cliente</h2>

            <form onSubmit={handleConsultar} style={{ marginBottom: '20px', display: 'flex', gap: '10px', alignItems: 'center' }}>
                <select
                    value={cuentaSeleccionada}
                    onChange={(e) => setCuentaSeleccionada(e.target.value)}
                    style={{ padding: '6px', minWidth: '250px' }}
                >
                    <option value="">Seleccione un cliente / cuenta</option>
                    {clientes.map((c) => (
                        <option key={c.id} value={c.accountNumber}>
                            {c.firstName} {c.lastName} — Cuenta {c.accountNumber}
                        </option>
                    ))}
                </select>
                <button type="submit" style={{ padding: '6px 12px' }}>Consultar</button>
            </form>

            {cargando && <p>Cargando historial...</p>}
            {error && <p style={{ color: 'red' }}>{error}</p>}

            {!cargando && buscado && ordenados.length === 0 && !error && (
                <p>No hay transacciones registradas para esta cuenta.</p>
            )}

            {ordenados.length > 0 && (
                <table border="1" cellPadding="8" style={{ width: '100%', borderCollapse: 'collapse' }}>
                    <thead>
                    <tr style={{ backgroundColor: '#f2f2f2' }}>
                        <th>ID</th>
                        <th>Fecha y hora</th>
                        <th>Cuenta de origen</th>
                        <th>Cuenta de destino</th>
                        <th>Tipo</th>
                        <th>Monto</th>
                    </tr>
                    </thead>
                    <tbody>
                    {ordenados.map((mov) => {
                        const tipo =
                            mov.senderAccountNumber === cuentaSeleccionada
                                ? 'Salida'
                                : 'Entrada';

                        return (
                            <tr key={mov.id}>
                                <td>{mov.id}</td>
                                <td>{mov.timestamp ? mov.timestamp : 'Sin fecha'}</td>
                                <td>{mov.senderAccountNumber}</td>
                                <td>{mov.receiverAccountNumber}</td>
                                <td style={{ color: tipo === 'Salida' ? 'red' : 'green', fontWeight: 'bold' }}>
                                    {tipo}
                                </td>
                                <td>{mov.amount}</td>
                            </tr>
                        );
                    })}
                    </tbody>
                </table>
            )}
        </div>
    );
}
