import { useState } from 'react';
import Clientes from './pages/Clientes';
import Transferencias from './pages/Transferencias';
import Historial from './pages/Historial';

export default function App() {
  const [vista, setVista] = useState('clientes');

  return (
      <div style={{ padding: '20px', fontFamily: 'Arial', maxWidth: '800px', margin: '0 auto' }}>
        <h1>Banco - Laboratorio de Arquitectura</h1>

        {/* Botones de navegación para cambiar de vista */}
        <nav style={{ marginBottom: '20px', display: 'flex', gap: '10px' }}>
          <button
              onClick={() => setVista('clientes')}
              style={{ padding: '8px 16px', fontWeight: vista === 'clientes' ? 'bold' : 'normal' }}
          >
            Clientes
          </button>
          <button
              onClick={() => setVista('transferencias')}
              style={{ padding: '8px 16px', fontWeight: vista === 'transferencias' ? 'bold' : 'normal' }}
          >
            Transferencias
          </button>
          <button
              onClick={() => setVista('historial')}
              style={{ padding: '8px 16px', fontWeight: vista === 'historial' ? 'bold' : 'normal' }}
          >
            Historial
          </button>
        </nav>

        <hr style={{ marginBottom: '20px' }} />

        {/* Renderizado condicional según la vista activa */}
        {vista === 'clientes' && <Clientes />}
        {vista === 'transferencias' && <Transferencias />}
        {vista === 'historial' && <Historial />}
      </div>
  );
}
