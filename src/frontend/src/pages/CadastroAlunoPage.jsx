import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  ArrowLeft,
  ArrowRight,
  Check,
  CircleAlert,
  Fingerprint,
  GraduationCap,
  IdCard,
  KeyRound,
  LoaderCircle,
  Lock,
  Mail,
  MapPin,
  School,
  UserRound,
} from 'lucide-react';
import { alunoApi, instituicaoApi } from '../services/api';
import { useToast } from '../context/ToastContext';
import { Field, PasswordField, PasswordMatch, PasswordStrength, SelectField } from '../components/Field';
import { mascaras } from '../utils/formatters';

const ETAPAS = [
  { titulo: 'Dados pessoais', descricao: 'Conte um pouco sobre você.', icone: UserRound },
  { titulo: 'Endereço', descricao: 'Onde você mora atualmente.', icone: MapPin },
  { titulo: 'Vínculo acadêmico', descricao: 'Sua instituição de ensino e seu curso.', icone: School },
  { titulo: 'Acesso', descricao: 'Crie uma senha para entrar no sistema.', icone: KeyRound },
];

const MASCARAS_CAMPOS = {
  cpf: mascaras.cpf,
  end_cep: mascaras.cep,
};

export default function CadastroAlunoPage() {
  const navigate = useNavigate();
  const toast = useToast();
  const [instituicoes, setInstituicoes] = useState([]);
  const [cursos, setCursos] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [etapa, setEtapa] = useState(0);
  const [direcao, setDirecao] = useState('avancar');

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
    const { name } = e.target;
    const value = MASCARAS_CAMPOS[name] ? MASCARAS_CAMPOS[name](e.target.value) : e.target.value;
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

  const ultimaEtapa = etapa === ETAPAS.length - 1;
  const etapaAtual = ETAPAS[etapa];

  const irParaEtapa = (destino) => {
    setDirecao(destino > etapa ? 'avancar' : 'voltar');
    setEtapa(destino);
    setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    // Só os campos da etapa atual estão na tela, então o navegador valida etapa por etapa
    if (!ultimaEtapa) {
      irParaEtapa(etapa + 1);
      return;
    }

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
      toast.sucesso('Conta criada com sucesso! Agora é só entrar.');
      navigate('/login');
    } catch (err) {
      setError(err.message || 'Erro ao cadastrar aluno.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page page-form">
      <Link to="/login" className="back-link">
        <ArrowLeft size={16} /> Voltar para o login
      </Link>

      <div className="card form-card reveal">
        <header className="form-card-header">
          <span className="icon-badge icon-badge-brand icon-badge-lg">
            <GraduationCap size={26} />
          </span>
          <div>
            <h1>Crie sua conta de aluno</h1>
            <p>Em poucos passos você estará pronto para receber moedas e trocá-las por vantagens.</p>
          </div>
        </header>

        <ol className="stepper" aria-label="Etapas do cadastro">
          {ETAPAS.map((item, indice) => {
            const Icone = item.icone;
            const concluida = indice < etapa;
            const atual = indice === etapa;
            return (
              <li key={item.titulo} className={`step${concluida ? ' is-done' : ''}${atual ? ' is-current' : ''}`}>
                <button
                  type="button"
                  className="step-dot"
                  onClick={() => irParaEtapa(indice)}
                  disabled={!concluida}
                  aria-current={atual ? 'step' : undefined}
                  aria-label={`Etapa ${indice + 1}: ${item.titulo}`}
                >
                  {concluida ? <Check size={18} strokeWidth={3} /> : <Icone size={18} />}
                </button>
                <span className="step-label">{item.titulo}</span>
              </li>
            );
          })}
        </ol>

        {error && (
          <div key={error} className="alert alert-error" role="alert">
            <CircleAlert size={18} />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div key={etapa} className={`step-panel${direcao === 'voltar' ? ' from-left' : ''}`}>
            <div className="step-heading">
              <h2>{etapaAtual.titulo}</h2>
              <p>{etapaAtual.descricao}</p>
            </div>

            {etapa === 0 && (
              <div className="form-grid">
                <Field
                  className="span-2"
                  label="Nome completo"
                  icon={UserRound}
                  name="nome"
                  placeholder="Maria Silva"
                  autoComplete="name"
                  value={formData.nome}
                  onChange={handleChange}
                  required
                  autoFocus
                />
                <Field
                  className="span-2"
                  label="E-mail"
                  icon={Mail}
                  type="email"
                  name="email"
                  placeholder="maria@email.com"
                  autoComplete="email"
                  value={formData.email}
                  onChange={handleChange}
                  required
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
                  label="RG"
                  icon={Fingerprint}
                  name="rg"
                  placeholder="MG-00.000.000"
                  value={formData.rg}
                  onChange={handleChange}
                  required
                />
              </div>
            )}

            {etapa === 1 && (
              <div className="form-grid cols-6">
                <Field
                  className="span-4"
                  label="Logradouro"
                  name="end_logradouro"
                  placeholder="Rua / Avenida"
                  autoComplete="address-line1"
                  value={formData.endereco.logradouro}
                  onChange={handleChange}
                  required
                  autoFocus
                />
                <Field
                  className="span-2"
                  label="Número"
                  name="end_numero"
                  placeholder="123"
                  value={formData.endereco.numero}
                  onChange={handleChange}
                  required
                />
                <Field
                  className="span-3"
                  label="Complemento"
                  name="end_complemento"
                  placeholder="Apto, bloco (opcional)"
                  autoComplete="address-line2"
                  value={formData.endereco.complemento}
                  onChange={handleChange}
                />
                <Field
                  className="span-3"
                  label="Bairro"
                  name="end_bairro"
                  placeholder="Bairro"
                  value={formData.endereco.bairro}
                  onChange={handleChange}
                  required
                />
                <Field
                  className="span-3"
                  label="Cidade"
                  name="end_cidade"
                  autoComplete="address-level2"
                  value={formData.endereco.cidade}
                  onChange={handleChange}
                  required
                />
                <Field
                  className="span-1"
                  label="Estado"
                  name="end_estado"
                  autoComplete="address-level1"
                  value={formData.endereco.estado}
                  onChange={handleChange}
                  required
                />
                <Field
                  className="span-2"
                  label="CEP"
                  name="end_cep"
                  placeholder="00000-000"
                  inputMode="numeric"
                  maxLength={9}
                  autoComplete="postal-code"
                  value={formData.endereco.cep}
                  onChange={handleChange}
                  required
                />
              </div>
            )}

            {etapa === 2 && (
              <div className="form-grid">
                <SelectField
                  label="Instituição de ensino"
                  icon={School}
                  name="instituicaoId"
                  options={instituicoes}
                  value={formData.instituicaoId}
                  onChange={handleChange}
                  required
                  autoFocus
                />
                <SelectField
                  label="Curso"
                  icon={GraduationCap}
                  name="cursoId"
                  options={cursos}
                  value={formData.cursoId}
                  onChange={handleChange}
                  required
                />
              </div>
            )}

            {etapa === 3 && (
              <>
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
                    autoFocus
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
              </>
            )}
          </div>

          <div className="form-actions">
            {etapa === 0 ? (
              <Link to="/login" className="btn btn-ghost">
                Cancelar
              </Link>
            ) : (
              <button type="button" className="btn btn-ghost" onClick={() => irParaEtapa(etapa - 1)}>
                <ArrowLeft size={18} /> Voltar
              </button>
            )}
            <span className="step-counter">
              Etapa {etapa + 1} de {ETAPAS.length}
            </span>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? (
                <>
                  <LoaderCircle size={18} className="spin" /> Criando conta...
                </>
              ) : ultimaEtapa ? (
                <>
                  Criar conta <Check size={18} />
                </>
              ) : (
                <>
                  Continuar <ArrowRight size={18} className="btn-arrow" />
                </>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
