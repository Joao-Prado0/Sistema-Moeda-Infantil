import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { alunoApi, instituicaoApi } from '../services/api';

export default function CadastroAlunoPage() {
  const navigate = useNavigate();
  const [instituicoes, setInstituicoes] = useState([]);
  const [cursos, setCursos] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const [formData, setFormData] = useState({
    nome: '',
    email: '',
    cpf: '',
    rg: '',
    instituicaoId: '',
    cursoId: '',
    senha: '',
    confirmarSenha: '',
    endereco: {
      logradouro: '',
      numero: '',
      complemento: '',
      bairro: '',
      cidade: 'Belo Horizonte',
      estado: 'MG',
      cep: '',
    },
  });

  useEffect(() => {
    async function carregarDados() {
      try {
        const [instRes, cursosRes] = await Promise.all([
          instituicaoApi.listar(),
          instituicaoApi.listarCursos(),
        ]);
        setInstituicoes(instRes);
        setCursos(cursosRes);
        if (instRes.length > 0) {
          setFormData((prev) => ({ ...prev, instituicaoId: instRes[0].id }));
        }
        if (cursosRes.length > 0) {
          setFormData((prev) => ({ ...prev, cursoId: cursosRes[0].id }));
        }
      } catch (err) {
        console.error('Erro ao carregar instituições/cursos:', err);
      }
    }
    carregarDados();
  }, []);

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

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (formData.senha !== formData.confirmarSenha) {
      setError('As senhas digitadas não coincidem.');
      return;
    }

    setLoading(true);
    try {
      const payload = {
        nome: formData.nome,
        email: formData.email,
        senha: formData.senha,
        cpf: formData.cpf,
        rg: formData.rg,
        instituicaoId: Number(formData.instituicaoId),
        cursoId: Number(formData.cursoId),
        endereco: formData.endereco,
      };

      await alunoApi.cadastrar(payload);
      alert('Aluno cadastrado com sucesso!');
      navigate('/login');
    } catch (err) {
      setError(err.message || 'Erro ao cadastrar aluno.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="center-container">
      <div className="card card-form">
        <h2>Cadastro de aluno</h2>
        <p className="card-subtitle">Preencha seus dados para começar a receber e trocar moedas</p>

        {error && <div className="alert-error">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-section-title">Dados pessoais</div>
          <div className="form-grid">
            <div className="form-group">
              <label>Nome completo</label>
              <input
                type="text"
                name="nome"
                placeholder="Maria Silva"
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
                placeholder="maria@email.com"
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
                placeholder="000.000.000-00"
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
                placeholder="MG-00.000.000"
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
                placeholder="Rua / Avenida"
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
                placeholder="123"
                value={formData.endereco.numero}
                onChange={handleChange}
                required
              />
            </div>
            <div className="form-group">
              <label>Complemento</label>
              <input
                type="text"
                name="end_complemento"
                placeholder="Apto, bloco (opcional)"
                value={formData.endereco.complemento}
                onChange={handleChange}
              />
            </div>
            <div className="form-group">
              <label>Bairro</label>
              <input
                type="text"
                name="end_bairro"
                placeholder="Bairro"
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
            <div className="form-group full-width">
              <label>CEP</label>
              <input
                type="text"
                name="end_cep"
                placeholder="00000-000"
                value={formData.endereco.cep}
                onChange={handleChange}
                required
              />
            </div>
          </div>

          <div className="form-section-title">Vínculo acadêmico</div>
          <div className="form-grid">
            <div className="form-group">
              <label>Instituição de ensino</label>
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

          <div className="form-section-title">Acesso</div>
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

