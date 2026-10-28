import { BrowserRouter, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import ToastProvider from './components/ToastProvider';
import Header from './components/Header';
import LoginPage from './pages/LoginPage';
import CadastroAlunoPage from './pages/CadastroAlunoPage';
import CadastroEmpresaPage from './pages/CadastroEmpresaPage';
import AlunoDashboard from './pages/AlunoDashboard';
import ProfessorDashboard from './pages/ProfessorDashboard';
import EmpresaDashboard from './pages/EmpresaDashboard';

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

function AnimatedRoutes() {
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

function App() {
  return (
    <AuthProvider>
      <ToastProvider>
        <BrowserRouter>
          <Header />
          <AnimatedRoutes />
        </BrowserRouter>
      </ToastProvider>
    </AuthProvider>
  );
}

export default App;
