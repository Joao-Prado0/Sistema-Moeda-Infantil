import React, { useState, useEffect } from 'react';
import './CadastroProfessorPage.css';
import '../CadastroAlunoPage/CadastroAlunoPage.css';
import { Link, useNavigate } from 'react-router-dom';
import {
  ArrowLeft,
  Building2,
  Check,
  CircleAlert,
  IdCard,
  KeyRound,
  LoaderCircle,
  Lock,
  Mail,
  School,
  UserRound,
} from 'lucide-react';
import { professorApi, instituicaoApi } from '../../services/api';
import { useToast } from '../../contexts/ToastContext';
import { Field, PasswordField, PasswordMatch, PasswordStrength, SelectField } from '../../components/ui/Input';
import { mascaras } from '../../utils/formatters';

export default function CadastroProfessorPage() {
  const navigate = useNavigate();
  const toast = useToast();
  const [instituicoes, setInstituicoes] = useState([]);
  const [departamentos, setDepartamentos] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const [formData, setFormData] = useState({
    nome: '',
    email: '',
    cpf: '',
    instituicaoId: '',
    departamentoId: '',
    senha: '',
    confirmarSenha: '',
  });

  useEffect(() => {
    async function carregarDados() {
      try {
        const [instRes, deptRes] = await Promise.all([
          instituicaoApi.listar(),
          instituicaoApi.listarDepartamentos(),
        ]);
        setInstituicoes(instRes || []);
        setDepartamentos(deptRes || []);
        if (instRes?.length > 0) {
          setFormData((prev) => ({ ...prev, instituicaoId: instRes[0].id }));
        }
        if (deptRes?.length > 0) {
          setFormData((prev) => ({ ...prev, departamentoId: deptRes[0].id }));
        }
      } catch (err) {
        console.error('Erro ao carregar instituições/departamentos:', err);
      }
    }
    carregarDados();
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: name === 'cpf' ? mascaras.cpf(value) : value,
    }));
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
      await professorApi.cadastrar({
        nome: formData.nome,
        email: formData.email,
        cpf: formData.cpf,
        instituicaoId: Number(formData.instituicaoId),
        departamentoId: Number(formData.departamentoId),
        senha: formData.senha,
      });

      toast.sucesso('Conta de professor criada com sucesso! Você já começa com 1.000 moedas.');
      navigate('/login');
    } catch (err) {
      setError(err.message || 'Erro ao cadastrar professor.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page page-form page-form-sm">
      <Link to="/login" className="back-link">
        <ArrowLeft size={16} /> Voltar para o login
      </Link>

      <div className="card form-card reveal">
        <header className="form-card-header">
          <span className="icon-badge icon-badge-professor icon-badge-lg">
            <School size={26} />
          </span>
          <div>
            <h1>Crie sua conta de professor</h1>
            <p>Cadastre-se para receber seu saldo semestral e distribuir moedas pelo mérito dos alunos.</p>
          </div>
        </header>

        {error && (
          <div key={error} className="alert alert-danger" role="alert">
            <CircleAlert size={18} />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <section className="form-section">
            <h2 className="form-section-title">
              <UserRound size={18} /> Dados pessoais
            </h2>
            <div className="form-grid">
              <Field
                className="span-2"
                label="Nome completo"
                icon={UserRound}
                name="nome"
                placeholder="Prof. João Souza"
                autoComplete="name"
                value={formData.nome}
                onChange={handleChange}
                required
                autoFocus
              />
              <Field
                label="CPF"
                icon={IdCard}
                name="cpf"
                placeholder="000.000.000-00"
                inputMode="numeric"
                maxLength={14}
                value={formData.cpf}
                onChange={handleChange}
                required
              />
              <Field
                label="E-mail institucional"
                icon={Mail}
                type="email"
                name="email"
                placeholder="professor@instituicao.edu.br"
                autoComplete="email"
                value={formData.email}
                onChange={handleChange}
                required
              />
            </div>
          </section>

          <section className="form-section">
            <h2 className="form-section-title">
              <School size={18} /> Vínculo institucional
            </h2>
            <div className="form-grid">
              <SelectField
                label="Instituição de ensino"
                icon={School}
                name="instituicaoId"
                options={instituicoes}
                value={formData.instituicaoId}
                onChange={handleChange}
                required
              />
              <SelectField
                label="Departamento"
                icon={Building2}
                name="departamentoId"
                options={departamentos}
                value={formData.departamentoId}
                onChange={handleChange}
                required
              />
            </div>
          </section>

          <section className="form-section">
            <h2 className="form-section-title">
              <KeyRound size={18} /> Acesso
            </h2>
            <div className="form-grid">
              <PasswordField
                label="Senha"
                icon={Lock}
                name="senha"
                placeholder="Mínimo de 6 caracteres"
                autoComplete="new-password"
                minLength={6}
                value={formData.senha}
                onChange={handleChange}
                required
              />
              <PasswordField
                label="Confirmar senha"
                icon={Lock}
                name="confirmarSenha"
                placeholder="Repita a senha"
                autoComplete="new-password"
                value={formData.confirmarSenha}
                onChange={handleChange}
                required
              />
            </div>
            <div className="password-feedback">
              <PasswordStrength senha={formData.senha} />
              <PasswordMatch senha={formData.senha} confirmacao={formData.confirmarSenha} />
            </div>
          </section>

          <div className="form-actions">
            <Link to="/login" className="btn btn-secondary btn-sm">
              Cancelar
            </Link>
            <button type="submit" className="btn btn-primary btn-sm" disabled={loading}>
              {loading ? (
                <>
                  <LoaderCircle size={16} className="spin" /> Criando conta...
                </>
              ) : (
                <>
                  Criar conta <Check size={16} />
                </>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

