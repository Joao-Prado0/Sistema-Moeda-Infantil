import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  ArrowLeft,
  Building2,
  Check,
  CircleAlert,
  Hash,
  KeyRound,
  LoaderCircle,
  Lock,
  Mail,
  Store,
} from 'lucide-react';
import { empresaApi } from '../services/api';
import { useToast } from '../context/ToastContext';
import { Field, PasswordField, PasswordMatch, PasswordStrength } from '../components/Field';
import { mascaras } from '../utils/formatters';

export default function CadastroEmpresaPage() {
  const navigate = useNavigate();
  const toast = useToast();
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
    setFormData((prev) => ({ ...prev, [name]: name === 'cnpj' ? mascaras.cnpj(value) : value }));
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

      toast.sucesso('Empresa parceira cadastrada com sucesso! Agora é só entrar.');
      navigate('/login');
    } catch (err) {
      setError(err.message || 'Erro ao cadastrar empresa parceira.');
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
          <span className="icon-badge icon-badge-gold icon-badge-lg">
            <Store size={26} />
          </span>
          <div>
            <h1>Seja uma empresa parceira</h1>
            <p>Depois do cadastro você poderá cadastrar as vantagens oferecidas aos estudantes.</p>
          </div>
        </header>

        {error && (
          <div key={error} className="alert alert-error" role="alert">
            <CircleAlert size={18} />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <section className="form-section">
            <h2 className="form-section-title">
              <Building2 size={18} /> Dados da empresa
            </h2>
            <div className="form-grid">
              <Field
                className="span-2"
                label="Nome fantasia"
                icon={Store}
                name="nomeFantasia"
                placeholder="Restaurante Universitário"
                autoComplete="organization"
                value={formData.nomeFantasia}
                onChange={handleChange}
                required
                autoFocus
              />
              <Field
                label="CNPJ"
                icon={Hash}
                name="cnpj"
                placeholder="00.000.000/0000-00"
                inputMode="numeric"
                maxLength={18}
                value={formData.cnpj}
                onChange={handleChange}
                required
              />
              <Field
                label="E-mail"
                icon={Mail}
                type="email"
                name="email"
                placeholder="contato@empresa.com"
                autoComplete="email"
                value={formData.email}
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
            <Link to="/login" className="btn btn-ghost">
              Cancelar
            </Link>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? (
                <>
                  <LoaderCircle size={18} className="spin" /> Criando conta...
                </>
              ) : (
                <>
                  Criar conta <Check size={18} />
                </>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
