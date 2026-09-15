import { useState, useEffect } from 'react';
import { listarClientes, crearCliente } from '../api/banco';

export default function Clientes() {
    const [clientes, setClientes] = useState([]);
    const [cargando, setCargando] = useState(true);
    const [error, setError] = useState('');

    // Estados para el formulario de creación de clientes
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [accountNumber, setAccountNumber] = useState('');
    const [balance, setBalance] = useState('');

    // Función para cargar los clientes desde el backend
    const cargarClientes = async () => {
        setCargando(true);
        setError('');
        try {
            const data = await listarClientes();
            setClientes(data);
        } catch {
            setError('No fue posible consultar los clientes.');
        } finally {
            setCargando(false);
        }
    };

    // Llamar a la función al abrir la vista
    useEffect(() => {
        listarClientes()
            .then(data => setClientes(data))
            .catch(() => setError('No fue posible consultar los clientes.'))
            .finally(() => setCargando(false));
    }, []);

    // Manejar la creación del cliente
    const handleSubmit = async (e) => {
        e.preventDefault();

        // Validar campos obligatorios
        if (!firstName.trim() || !lastName.trim() || !accountNumber.trim() || balance === '') {
            alert('Todos los campos son obligatorios.');
            return;
        }

        const parsedBalance = parseFloat(balance);
        // Validar que el saldo no sea negativo
        if (isNaN(parsedBalance) || parsedBalance < 0) {
            alert('El saldo debe ser un número positivo o cero.');
            return;
        }

        try {
            await crearCliente({
                firstName,
                lastName,
                accountNumber,
                balance: parsedBalance
            });

            // Limpiar formulario y volver a consultar la lista
            setFirstName('');
            setLastName('');
            setAccountNumber('');
            setBalance('');
            await cargarClientes();
        } catch {
            alert('Error al crear el cliente en el servidor.');
        }
    };

    // Manejo de estados de carga y error
    if (cargando) return <p>Cargando clientes…</p>;
    if (error) return <p style={{ color: 'red' }}>{error}</p>;

    return (
        <div style={{ padding: '20px', fontFamily: 'Arial' }}>
            <h2>Gestión de Clientes</h2>

            {/* Formulario de creación opcional */}
            <form onSubmit={handleSubmit} style={{ marginBottom: '20px', display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
                <input
                    type="text"
                    placeholder="Nombre"
                    value={firstName}
                    onChange={(e) => setFirstName(e.target.value)}
                />
                <input
                    type="text"
                    placeholder="Apellido"
                    value={lastName}
                    onChange={(e) => setLastName(e.target.value)}
                />
                <input
                    type="text"
                    placeholder="Número de Cuenta"
                    value={accountNumber}
                    onChange={(e) => setAccountNumber(e.target.value)}
                />
                <input
                    type="number"
                    placeholder="Saldo Inicial"
                    value={balance}
                    onChange={(e) => setBalance(e.target.value)}
                />
                <button type="submit">Crear Cliente</button>
            </form>

            {/* Tabla de clientes o mensaje si está vacía */}
            {clientes.length === 0 ? (
                <p>No hay clientes registrados.</p>
            ) : (
                <table border="1" cellPadding="8" style={{ width: '100%', borderCollapse: 'collapse' }}>
                    <thead>
                    <tr style={{ backgroundColor: '#f2f2f2' }}>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Apellido</th>
                        <th>Cuenta</th>
                        <th>Saldo</th>
                    </tr>
                    </thead>
                    <tbody>
                    {clientes.map((cliente) => (
                        <tr key={cliente.id}>
                            <td>{cliente.id}</td>
                            <td>{cliente.firstName}</td>
                            <td>{cliente.lastName}</td>
                            <td>{cliente.accountNumber}</td>
                            <td>{cliente.balance}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}
