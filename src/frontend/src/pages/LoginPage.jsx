import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  ArrowRight,
  ChevronRight,
  CircleAlert,
  Gift,
  GraduationCap,
  HandCoins,
  LoaderCircle,
  Lock,
  Mail,
  Receipt,
  Sparkles,
  Store,
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { authApi } from '../services/api';
import LogoMark from '../components/LogoMark';
import { Field, PasswordField } from '../components/Field';

// As moedas "apenasDesktop" ficariam sobre o texto quando o painel encolhe
const MOEDAS_FLUTUANTES = [
  { tamanho: 54, top: '9%', left: '80%', duracao: 7, atraso: 0 },
  { tamanho: 30, top: '31%', left: '90%', duracao: 9, atraso: 1.2 },
  { tamanho: 40, top: '90%', left: '8%', duracao: 8, atraso: 0.6, apenasDesktop: true },
  { tamanho: 24, top: '89%', left: '38%', duracao: 10, atraso: 2, apenasDesktop: true },
  { tamanho: 28, top: '5%', left: '48%', duracao: 11, atraso: 1.6, apenasDesktop: true },
];

const BENEFICIOS = [
  { icone: HandCoins, texto: 'Receba moedas pelo seu mérito em sala' },
  { icone: Gift, texto: 'Troque por vantagens em empresas parceiras' },
  { icone: Receipt, texto: 'Acompanhe cada movimentação pelo extrato' },
];

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
      const userData = await authApi.login({ email, senha });
      login(userData);

      if (userData.perfil === 'PROFESSOR') {
        navigate('/professor/dashboard');
      } else if (userData.perfil === 'EMPRESA') {
        navigate('/empresa/dashboard');
      } else {
        navigate('/aluno/dashboard');
      }
    } catch (err) {
      setError(err.message || 'Erro ao realizar login.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <section className="auth-hero">
        <div className="auth-hero-grid" aria-hidden="true" />
        {MOEDAS_FLUTUANTES.map((moeda, i) => (
          <span
            key={i}
            className={`float-coin${moeda.apenasDesktop ? ' desktop-only' : ''}`}
            aria-hidden="true"
            style={{
              top: moeda.top,
              left: moeda.left,
              '--size': `${moeda.tamanho}px`,
              '--duration': `${moeda.duracao}s`,
              '--delay': `${moeda.atraso}s`,
            }}
          >
            <LogoMark />
          </span>
        ))}
        <div className="hero-coin" aria-hidden="true">
          <LogoMark />
        </div>

        <div className="auth-hero-content reveal">
          <span className="badge-pill">
            <Sparkles size={14} /> Mérito que vale vantagens
          </span>
          <h1>
            Reconhecimento <br />
            <span>acadêmico real.</span>
          </h1>
          <p>
            Professores distribuem moedas por participação e destaque. Alunos trocam por experiências e
            produtos em parceiros.
          </p>

          <ul className="benefit-list">
            {BENEFICIOS.map(({ icone: Icone, texto }) => (
              <li key={texto}>
                <span className="benefit-icon">
                  <Icone size={18} />
                </span>
                <span>{texto}</span>
              </li>
            ))}
          </ul>
        </div>
      </section>

      <section className="auth-panel">
        <div className="auth-card">
          <header className="auth-card-header reveal" style={{ '--i': 1 }}>
            <span className="eyebrow-text">Bem-vindo de volta</span>
            <h2>Entrar na sua conta</h2>
            <p>Acesse seu painel com seu e-mail e sua senha cadastrados.</p>
          </header>

          {error && (
            <div className="alert alert-danger" role="alert">
              <CircleAlert size={18} />
              <span>{error}</span>
            </div>
          )}

          <form onSubmit={handleSubmit} className="auth-form reveal" style={{ '--i': 3 }}>
            <Field
              label="E-mail"
              icon={Mail}
              type="email"
              placeholder="seu.email@exemplo.com"
              autoComplete="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
            <PasswordField
              label="Senha"
              icon={Lock}
              placeholder="Sua senha"
              autoComplete="current-password"
              value={senha}
              onChange={(e) => setSenha(e.target.value)}
              required
            />
            <button type="submit" className="btn btn-primary btn-lg btn-block" disabled={loading}>
              {loading ? (
                <>
                  <LoaderCircle size={18} className="spin" /> Entrando...
                </>
              ) : (
                <>
                  Entrar <ArrowRight size={18} className="btn-arrow" />
                </>
              )}
            </button>
          </form>

          <div className="divider reveal" style={{ '--i': 4 }}>
            <span>Ainda não tem conta?</span>
          </div>

          <div className="choice-grid reveal" style={{ '--i': 5 }}>
            <Link to="/cadastro-aluno" className="choice-card">
              <span className="icon-badge icon-badge-brand">
                <GraduationCap size={20} />
              </span>
              <span className="choice-text">
                <strong>Sou aluno</strong>
                <small>Quero receber moedas</small>
              </span>
              <ChevronRight size={18} className="choice-arrow" />
            </Link>
            <Link to="/cadastro-empresa" className="choice-card">
              <span className="icon-badge icon-badge-gold">
                <Store size={20} />
              </span>
              <span className="choice-text">
                <strong>Sou empresa</strong>
                <small>Quero oferecer vantagens</small>
              </span>
              <ChevronRight size={18} className="choice-arrow" />
            </Link>
          </div>
        </div>
      </section>
    </div>
  );
}
