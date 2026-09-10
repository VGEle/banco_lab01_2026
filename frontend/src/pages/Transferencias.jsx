import { useState, useEffect } from 'react';
import { listarClientes, transferir } from '../api/banco';

export default function Transferencias() {
    const [clientes, setClientes] = useState([]);
    const [cuentaOrigen, setCuentaOrigen] = useState('');
    const [cuentaDestino, setCuentaDestino] = useState('');
    const [monto, setMonto] = useState('');

    const [enviando, setEnviando] = useState(false);
    const [mensaje, setMensaje] = useState('');
    const [error, setError] = useState('');

    // Cargar clientes al abrir la vista para los selectores
    useEffect(() => {
        listarClientes()
            .then(data => setClientes(data))
            .catch(() => setError('No fue posible cargar los clientes para las cuentas.'));
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMensaje('');
        setError('');

        // Comprobaciones iniciales
        if (!cuentaOrigen || !cuentaDestino) {
            setError('Debes seleccionar las dos cuentas.');
            return;
        }
        if (cuentaOrigen === cuentaDestino) {
            setError('La cuenta de origen y la de destino deben ser diferentes.');
            return;
        }
        const parsedMonto = Number(monto);
        if (isNaN(parsedMonto) || parsedMonto <= 0) {
            setError('El monto debe ser un número válido y positivo.');
            return;
        }

        setEnviando(true);
        try {
            const datos = {
                senderAccountNumber: cuentaOrigen,
                receiverAccountNumber: cuentaDestino,
                amount: parsedMonto,
            };

            await transferir(datos);

            setMensaje('¡Transferencia realizada con éxito!');
            setCuentaOrigen('');
            setCuentaDestino('');
            setMonto('');
        } catch (err) {
            const datosError = err.response?.data;
            const msg = typeof datosError === 'string'
                ? datosError
                : datosError?.message || 'No se pudo realizar la transferencia.';
            setError(msg);
        } finally {
            setEnviando(false);
        }
    };

    return (
        <div style={{ padding: '20px', fontFamily: 'Arial' }}>
            <h2>Realizar Transferencia</h2>

            <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', maxWidth: '400px', gap: '12px' }}>

                {/* Selector de cuenta de origen */}
                <label>
                    Cuenta de origen:
                    <select
                        value={cuentaOrigen}
                        onChange={(e) => setCuentaOrigen(e.target.value)}
                        style={{ width: '100%', padding: '6px', marginTop: '4px' }}
                    >
                        <option value="">Seleccione cuenta de origen</option>
                        {clientes.map(c => (
                            <option key={c.id} value={c.accountNumber}>
                                {c.firstName} {c.lastName} — Cuenta {c.accountNumber}
                            </option>
                        ))}
                    </select>
                </label>

                {/* Selector de cuenta de destino */}
                <label>
                    Cuenta de destino:
                    <select
                        value={cuentaDestino}
                        onChange={(e) => setCuentaDestino(e.target.value)}
                        style={{ width: '100%', padding: '6px', marginTop: '4px' }}
                    >
                        <option value="">Seleccione cuenta de destino</option>
                        {clientes.map(c => (
                            <option key={c.id} value={c.accountNumber}>
                                {c.firstName} {c.lastName} — Cuenta {c.accountNumber}
                            </option>
                        ))}
                    </select>
                </label>

                {/* Campo de monto */}
                <label>
                    Monto:
                    <input
                        type="number"
                        step="any"
                        placeholder="Ej. 150"
                        value={monto}
                        onChange={(e) => setMonto(e.target.value)}
                        style={{ width: '100%', padding: '6px', marginTop: '4px' }}
                    />
                </label>

                {/* Botón de envío deshabilitado mientras se procesa */}
                <button type="submit" disabled={enviando} style={{ padding: '10px', cursor: enviando ? 'not-allowed' : 'pointer' }}>
                    {enviando ? 'Procesando...' : 'Transferir'}
                </button>
            </form>

            {/* Mensajes de éxito y error */}
            {mensaje && <p style={{ color: 'green', marginTop: '10px' }}>{mensaje}</p>}
            {error && <p style={{ color: 'red', marginTop: '10px' }}>{error}</p>}
        </div>
    );
}