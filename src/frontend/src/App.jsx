import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Header from './components/Header';
import LoginPage from './pages/LoginPage';
import CadastroAlunoPage from './pages/CadastroAlunoPage';
import CadastroEmpresaPage from './pages/CadastroEmpresaPage';
import AlunoDashboard from './pages/AlunoDashboard';
import EmpresaDashboard from './pages/EmpresaDashboard';

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Header />
        <Routes>
          <Route path="/" element={<Navigate to="/login" replace />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/cadastro-aluno" element={<CadastroAlunoPage />} />
          <Route path="/cadastro-empresa" element={<CadastroEmpresaPage />} />
          <Route path="/aluno/dashboard" element={<AlunoDashboard />} />
          <Route path="/empresa/dashboard" element={<EmpresaDashboard />} />
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
