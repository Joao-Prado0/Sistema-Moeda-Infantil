import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { empresaApi } from '../services/api';

export default function CadastroEmpresaPage() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const [formData, setFormData] = useState({
    nomeFantasia: '',
    cnpj: '',
    email: '',
    senha: '',
    confirmarSenha: '',
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (formData.senha !== formData.confirmarSenha) {
      setError('As senhas digitadas não coincidem.');
      return;
    }

    setLoading(true);
    try {
      await empresaApi.cadastrar({
        nomeFantasia: formData.nomeFantasia,
        cnpj: formData.cnpj,
        email: formData.email,
        senha: formData.senha,
      });

      alert('Empresa parceira cadastrada com sucesso!');
      navigate('/login');
    } catch (err) {
      setError(err.message || 'Erro ao cadastrar empresa parceira.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="center-container">
      <div className="card card-form" style={{ maxWidth: '540px' }}>
        <h2>Cadastro de empresa parceira</h2>
        <p className="card-subtitle">Depois do cadastro você poderá cadastrar as vantagens oferecidas.</p>

        {error && <div className="alert-error">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Nome fantasia</label>
            <input
              type="text"
              name="nomeFantasia"
              placeholder="Restaurante Universitário"
              value={formData.nomeFantasia}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-grid">
            <div className="form-group">
              <label>CNPJ</label>
              <input
                type="text"
                name="cnpj"
                placeholder="00.000.000/0000-00"
                value={formData.cnpj}
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-group">
              <label>E-mail</label>
              <input
                type="email"
                name="email"
                placeholder="contato@empresa.com"
                value={formData.email}
                onChange={handleChange}
                required
              />
            </div>
          </div>

          <div className="form-grid">
            <div className="form-group">
              <label>Senha</label>
              <input
                type="password"
                name="senha"
                placeholder="********"
                value={formData.senha}
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-group">
              <label>Confirmar senha</label>
              <input
                type="password"
                name="confirmarSenha"
                placeholder="********"
                value={formData.confirmarSenha}
                onChange={handleChange}
                required
              />
            </div>
          </div>

          <div className="btn-group">
            <Link to="/login" className="btn-secondary">
              Cancelar
            </Link>
            <button type="submit" className="btn-primary" disabled={loading}>
              {loading ? 'Cadastrando...' : 'Cadastrar'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

