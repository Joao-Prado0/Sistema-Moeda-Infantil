import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { empresaApi } from '../services/api';

export default function EmpresaDashboard() {
  const { user, login } = useAuth();
  const [empresa, setEmpresa] = useState(user);
  const [loading, setLoading] = useState(false);
  const [msg, setMsg] = useState({ tipo: '', texto: '' });

  const [formData, setFormData] = useState({
    nomeFantasia: '',
    email: '',
    cnpj: '',
    senha: '',
  });

  useEffect(() => {
    async function carregar() {
      try {
        const empAtualizada = await empresaApi.buscarPorId(user?.id || 1);
        setEmpresa(empAtualizada);
        setFormData({
          nomeFantasia: empAtualizada.nomeFantasia || '',
          email: empAtualizada.email || '',
          cnpj: empAtualizada.cnpj || '',
          senha: '',
        });
      } catch (err) {
        console.error('Erro ao buscar dados da empresa:', err);
      }
    }
    carregar();
  }, [user]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleUpdate = async (e) => {
    e.preventDefault();
    setMsg({ tipo: '', texto: '' });
    setLoading(true);

    try {
      const atualizada = await empresaApi.atualizar(empresa.id, {
        nomeFantasia: formData.nomeFantasia,
        email: formData.email,
        cnpj: formData.cnpj,
        senha: formData.senha || undefined,
      });

      setEmpresa(atualizada);
      login(atualizada);
      setMsg({ tipo: 'sucesso', texto: 'Dados da empresa atualizados com sucesso!' });
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message || 'Erro ao atualizar empresa.' });
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (window.confirm('Tem certeza de que deseja excluir permanentemente o cadastro da empresa parceira?')) {
      try {
        await empresaApi.deletar(empresa.id);
        alert('Cadastro de empresa excluído com sucesso.');
        window.location.href = '/login';
      } catch (err) {
        alert('Erro ao excluir empresa: ' + err.message);
      }
    }
  };

  return (
    <div className="main-layout">
      <aside className="sidebar">
        <button
          type="button"
          className="sidebar-link active"
          style={{ width: '100%', border: 'none', textAlign: 'left', cursor: 'pointer' }}
        >
          Minha Empresa (CRUD)
        </button>
      </aside>

      <main className="content-area">
        <div className="card card-form" style={{ maxWidth: '600px' }}>
          <h2>Dados da Empresa Parceira</h2>
          <p className="card-subtitle">Consulte, altere ou exclua os dados da sua empresa (US14)</p>

          {msg.texto && (
            <div className={msg.tipo === 'sucesso' ? 'alert-success' : 'alert-error'}>
              {msg.texto}
            </div>
          )}

          <form onSubmit={handleUpdate}>
            <div className="form-group">
              <label>Nome fantasia</label>
              <input
                type="text"
                name="nomeFantasia"
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
                  value={formData.email}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>

            <div className="form-group">
              <label>Alterar Senha (opcional)</label>
              <input
                type="password"
                name="senha"
                placeholder="Deixe em branco para não alterar"
                value={formData.senha}
                onChange={handleChange}
              />
            </div>

            <div className="btn-group" style={{ justifyContent: 'space-between', marginTop: '30px' }}>
              <button type="button" className="btn-danger" onClick={handleDelete}>
                Excluir cadastro
              </button>
              <button type="submit" className="btn-primary" disabled={loading}>
                {loading ? 'Salvando...' : 'Salvar alterações'}
              </button>
            </div>
          </form>
        </div>
      </main>
    </div>
  );
}

