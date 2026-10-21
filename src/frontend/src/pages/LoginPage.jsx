import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { alunoApi, empresaApi } from '../services/api';

export default function LoginPage() {
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      // Procura primeiro em alunos
      const alunos = await alunoApi.listar();
      const aluno = alunos.find((a) => a.email.toLowerCase() === email.toLowerCase());

      if (aluno) {
        login(aluno);
        navigate('/aluno/dashboard');
        return;
      }

      // Procura em empresas
      const empresas = await empresaApi.listar();
      const empresa = empresas.find((emp) => emp.email.toLowerCase() === email.toLowerCase());

      if (empresa) {
        login(empresa);
        navigate('/empresa/dashboard');
        return;
      }

      setError('Credenciais inválidas. Verifique o e-mail informado.');
    } catch (err) {
      setError(err.message || 'Erro ao realizar login.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="center-container">
      <div className="card card-login">
        <h2>Entrar</h2>
        <p className="card-subtitle">Acesse com seu e-mail e senha</p>

        {error && <div className="alert-error">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="email">E-mail</label>
            <input
              id="email"
              type="email"
              placeholder="voce@email.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="senha">Senha</label>
            <input
              id="senha"
              type="password"
              placeholder="********"
              value={senha}
              onChange={(e) => setSenha(e.target.value)}
              required
            />
          </div>

          <button
            type="submit"
            className="btn-primary"
            style={{ width: '100%', marginTop: '12px' }}
            disabled={loading}
          >
            {loading ? 'Entrando...' : 'Entrar'}
          </button>
        </form>

        <div className="footer-links">
          <span>Não tem conta?</span>
          <div className="footer-action-links">
            <Link to="/cadastro-aluno">Cadastrar como aluno</Link>
            <Link to="/cadastro-empresa">Cadastrar como empresa</Link>
          </div>
        </div>
      </div>
    </div>
  );
}

