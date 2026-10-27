import { Link, useLocation, useNavigate } from 'react-router-dom';
import { LogIn, LogOut } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import LogoMark from './LogoMark';
import { iniciais } from '../utils/formatters';

export default function Header() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const { pathname } = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const nome = user?.nome || user?.nomeFantasia || '';
  const ehEmpresa = user?.perfil === 'EMPRESA';

  return (
    <header className="app-header">
      <div className="app-header-inner">
        <Link to="/" className="brand" aria-label="Moeda Estudantil, página inicial">
          <LogoMark className="brand-mark" />
          <span className="brand-name">
            Moeda <span>Estudantil</span>
          </span>
        </Link>

        {user ? (
          <div className="header-actions">
            <div className="user-chip">
              <span className={`avatar avatar-sm${ehEmpresa ? ' avatar-gold' : ''}`}>{iniciais(nome)}</span>
              <span className="user-chip-text">
                <span className="user-chip-name">{nome}</span>
                <span className={`role-badge ${ehEmpresa ? 'role-empresa' : 'role-aluno'}`}>
                  {ehEmpresa ? 'Empresa' : 'Aluno'}
                </span>
              </span>
            </div>
            <button type="button" className="btn btn-ghost btn-sm" onClick={handleLogout} aria-label="Sair da conta">
              <LogOut size={16} />
              <span className="hide-sm">Sair</span>
            </button>
          </div>
        ) : (
          pathname !== '/login' && (
            <Link to="/login" className="btn btn-ghost btn-sm">
              <LogIn size={16} /> Entrar
            </Link>
          )
        )}
      </div>
    </header>
  );
}
