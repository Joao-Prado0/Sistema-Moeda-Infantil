import { Navigate, useLocation, Routes, Route } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import LoginPage from '../pages/LoginPage';
import CadastroAlunoPage from '../pages/CadastroAlunoPage';
import CadastroEmpresaPage from '../pages/CadastroEmpresaPage';
import AlunoDashboard from '../pages/AlunoDashboardPage';
import ProfessorDashboard from '../pages/ProfessorDashboardPage';
import EmpresaDashboard from '../pages/EmpresaDashboardPage';

function rotaInicial(user) {
  if (!user) return '/login';
  if (user.perfil === 'PROFESSOR') return '/professor/dashboard';
  if (user.perfil === 'EMPRESA') return '/empresa/dashboard';
  return '/aluno/dashboard';
}

function RotaInicial() {
  const { user } = useAuth();
  return <Navigate to={rotaInicial(user)} replace />;
}

function SomenteVisitante({ children }) {
  const { user } = useAuth();
  return user ? <Navigate to={rotaInicial(user)} replace /> : children;
}

function RotaProtegida({ perfil, children }) {
  const { user } = useAuth();
  if (!user) return <Navigate to="/login" replace />;
  if (user.perfil !== perfil) return <Navigate to={rotaInicial(user)} replace />;
  return children;
}

export function AppRoutes() {
  const location = useLocation();

  return (
    <main key={location.pathname} className="page-transition">
      <Routes location={location}>
        <Route path="/" element={<RotaInicial />} />
        <Route
          path="/login"
          element={
            <SomenteVisitante>
              <LoginPage />
            </SomenteVisitante>
          }
        />
        <Route path="/cadastro-aluno" element={<CadastroAlunoPage />} />
        <Route path="/cadastro-empresa" element={<CadastroEmpresaPage />} />
        <Route
          path="/aluno/dashboard"
          element={
            <RotaProtegida perfil="ALUNO">
              <AlunoDashboard />
            </RotaProtegida>
          }
        />
        <Route
          path="/professor/dashboard"
          element={
            <RotaProtegida perfil="PROFESSOR">
              <ProfessorDashboard />
            </RotaProtegida>
          }
        />
        <Route
          path="/empresa/dashboard"
          element={
            <RotaProtegida perfil="EMPRESA">
              <EmpresaDashboard />
            </RotaProtegida>
          }
        />
        <Route path="*" element={<RotaInicial />} />
      </Routes>
    </main>
  );
}

export default AppRoutes;

