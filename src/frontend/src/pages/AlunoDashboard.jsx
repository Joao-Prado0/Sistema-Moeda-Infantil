import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { alunoApi, instituicaoApi } from '../services/api';
import { NavLink } from 'react-router-dom';

export default function AlunoDashboard() {
  const { user, login } = useAuth();
  const [activeTab, setActiveTab] = useState('extrato');
  const [aluno, setAluno] = useState(user);
  const [instituicoes, setInstituicoes] = useState([]);
  const [cursos, setCursos] = useState([]);
  const [loading, setLoading] = useState(false);
  const [msg, setMsg] = useState({ tipo: '', texto: '' });

  const [formData, setFormData] = useState({
    nome: '',
    email: '',
    cpf: '',
    rg: '',
    instituicaoId: '',
    cursoId: '',
    senha: '',
    endereco: {
      logradouro: '',
      numero: '',
      complemento: '',
      bairro: '',
      cidade: '',
      estado: '',
      cep: '',
    },
  });

  useEffect(() => {
    async function carregar() {
      try {
        const [instRes, cursosRes, alunoAtualizado] = await Promise.all([
          instituicaoApi.listar(),
          instituicaoApi.listarCursos(),
          alunoApi.buscarPorId(user?.id || 1),
        ]);
        setInstituicoes(instRes);
        setCursos(cursosRes);
        setAluno(alunoAtualizado);

        setFormData({
          nome: alunoAtualizado.nome || '',
          email: alunoAtualizado.email || '',
          cpf: alunoAtualizado.cpf || '',
          rg: alunoAtualizado.rg || '',
          instituicaoId: alunoAtualizado.instituicaoId || '',
          cursoId: alunoAtualizado.cursoId || '',
          senha: '',
          endereco: alunoAtualizado.endereco || {
            logradouro: '',
            numero: '',
            complemento: '',
            bairro: '',
            cidade: '',
            estado: '',
            cep: '',
          },
        });
      } catch (err) {
        console.error('Erro ao carregar dados do aluno:', err);
      }
    }
    carregar();
  }, [user]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    if (name.startsWith('end_')) {
      const field = name.replace('end_', '');
      setFormData((prev) => ({
        ...prev,
        endereco: { ...prev.endereco, [field]: value },
      }));
    } else {
      setFormData((prev) => ({ ...prev, [name]: value }));
    }
  };

  const handleUpdate = async (e) => {
    e.preventDefault();
    setMsg({ tipo: '', texto: '' });
    setLoading(true);

    try {
      const atualizado = await alunoApi.atualizar(aluno.id, {
        nome: formData.nome,
        email: formData.email,
        cpf: formData.cpf,
        rg: formData.rg,
        instituicaoId: Number(formData.instituicaoId),
        cursoId: Number(formData.cursoId),
        endereco: formData.endereco,
        senha: formData.senha || undefined,
      });

      setAluno(atualizado);
      login(atualizado);
      setMsg({ tipo: 'sucesso', texto: 'Dados cadastrais atualizados com sucesso!' });
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message || 'Erro ao atualizar dados.' });
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (window.confirm('Tem certeza de que deseja excluir permanentemente sua conta de aluno?')) {
      try {
        await alunoApi.deletar(aluno.id);
        alert('Conta excluída com sucesso.');
        window.location.href = '/login';
      } catch (err) {
        alert('Erro ao excluir conta: ' + err.message);
      }
    }
  };

  return (
    <div className="main-layout">
      <aside className="sidebar">
        <button
          type="button"
          className={`sidebar-link ${activeTab === 'extrato' ? 'active' : ''}`}
          onClick={() => setActiveTab('extrato')}
          style={{ width: '100%', border: 'none', textAlign: 'left', cursor: 'pointer' }}
        >
          Início e extrato
        </button>
        <button
          type="button"
          className={`sidebar-link ${activeTab === 'perfil' ? 'active' : ''}`}
          onClick={() => setActiveTab('perfil')}
          style={{ width: '100%', border: 'none', textAlign: 'left', cursor: 'pointer' }}
        >
          Meu perfil (CRUD)
        </button>
      </aside>

      <main className="content-area">
        {activeTab === 'extrato' ? (
          <div>
            <h2>Olá, {aluno?.nome?.split(' ')[0] || 'Aluno'}</h2>
            <p className="card-subtitle">Acompanhe seu saldo e movimentações de mérito</p>

            <div className="stats-grid">
              <div className="stat-card">
                <div className="stat-label">Saldo atual</div>
                <div className="stat-value">{aluno?.saldo || 0} moedas</div>
              </div>
              <div className="stat-card">
                <div className="stat-label">Curso</div>
                <div style={{ fontSize: '1.2rem', fontWeight: 600 }}>{aluno?.cursoNome || 'Não informado'}</div>
              </div>
              <div className="stat-card">
                <div className="stat-label">Instituição</div>
                <div style={{ fontSize: '1.2rem', fontWeight: 600 }}>{aluno?.instituicaoNome || 'PUC Minas'}</div>
              </div>
            </div>

            <div className="card" style={{ marginTop: '24px' }}>
              <h3>Extrato</h3>
              <p style={{ color: 'var(--text-secondary)', marginTop: '8px' }}>
                Nenhuma transação registrada até o momento.
              </p>
            </div>
          </div>
        ) : (
          <div className="card card-form" style={{ maxWidth: '700px' }}>
            <h2>Meu Perfil - Consultar e Editar</h2>
            <p className="card-subtitle">Gerencie e atualize seus dados cadastrais (US03)</p>

            {msg.texto && (
              <div className={msg.tipo === 'sucesso' ? 'alert-success' : 'alert-error'}>
                {msg.texto}
              </div>
            )}

            <form onSubmit={handleUpdate}>
              <div className="form-section-title">Dados pessoais</div>
              <div className="form-grid">
                <div className="form-group">
                  <label>Nome completo</label>
                  <input
                    type="text"
                    name="nome"
                    value={formData.nome}
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
                <div className="form-group">
                  <label>CPF</label>
                  <input
                    type="text"
                    name="cpf"
                    value={formData.cpf}
                    onChange={handleChange}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>RG</label>
                  <input
                    type="text"
                    name="rg"
                    value={formData.rg}
                    onChange={handleChange}
                    required
                  />
                </div>
              </div>

              <div className="form-section-title">Endereço</div>
              <div className="form-grid">
                <div className="form-group">
                  <label>Logradouro</label>
                  <input
                    type="text"
                    name="end_logradouro"
                    value={formData.endereco.logradouro}
                    onChange={handleChange}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Número</label>
                  <input
                    type="text"
                    name="end_numero"
                    value={formData.endereco.numero}
                    onChange={handleChange}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Bairro</label>
                  <input
                    type="text"
                    name="end_bairro"
                    value={formData.endereco.bairro}
                    onChange={handleChange}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Cidade</label>
                  <input
                    type="text"
                    name="end_cidade"
                    value={formData.endereco.cidade}
                    onChange={handleChange}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Estado</label>
                  <input
                    type="text"
                    name="end_estado"
                    value={formData.endereco.estado}
                    onChange={handleChange}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>CEP</label>
                  <input
                    type="text"
                    name="end_cep"
                    value={formData.endereco.cep}
                    onChange={handleChange}
                    required
                  />
                </div>
              </div>

              <div className="form-section-title">Vínculo acadêmico</div>
              <div className="form-grid">
                <div className="form-group">
                  <label>Instituição</label>
                  <select
                    name="instituicaoId"
                    value={formData.instituicaoId}
                    onChange={handleChange}
                    required
                  >
                    {instituicoes.map((inst) => (
                      <option key={inst.id} value={inst.id}>
                        {inst.nome}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="form-group">
                  <label>Curso</label>
                  <select
                    name="cursoId"
                    value={formData.cursoId}
                    onChange={handleChange}
                    required
                  >
                    {cursos.map((c) => (
                      <option key={c.id} value={c.id}>
                        {c.nome}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="form-section-title">Alterar Senha (opcional)</div>
              <div className="form-group">
                <label>Nova Senha</label>
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
                  Excluir meu cadastro
                </button>
                <button type="submit" className="btn-primary" disabled={loading}>
                  {loading ? 'Salvando...' : 'Salvar alterações'}
                </button>
              </div>
            </form>
          </div>
        )}
      </main>
    </div>
  );
}

