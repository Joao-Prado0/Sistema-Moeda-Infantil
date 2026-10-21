import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Header() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <header className="top-header">
      <Link to="/" className="brand">
        <div className="brand-icon"></div>
        <span>Moeda Estudantil</span>
      </Link>
      <div className="header-user">
        {user ? (
          <>
            <span>{user.perfil === 'ALUNO' ? 'Aluno' : 'Empresa'} - {user.nome || user.nomeFantasia}</span>
            <button type="button" onClick={handleLogout}>Sair</button>
          </>
        ) : (
          <>
            <span>Visitante</span>
            <Link to="/login">Entrar</Link>
          </>
        )}
      </div>
    </header>
  );
}

