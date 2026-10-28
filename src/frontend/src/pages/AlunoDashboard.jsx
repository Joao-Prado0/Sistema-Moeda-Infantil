import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  CalendarDays,
  Fingerprint,
  Gift,
  GraduationCap,
  IdCard,
  KeyRound,
  LayoutDashboard,
  LoaderCircle,
  Lock,
  Mail,
  MapPin,
  Receipt,
  Save,
  School,
  Trash2,
  UserRound,
  UserRoundPen,
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import { alunoApi, instituicaoApi } from '../services/api';
import DashboardShell from '../components/DashboardShell';
import ConfirmDialog from '../components/ConfirmDialog';
import FormSkeleton from '../components/FormSkeleton';
import { Field, PasswordField, PasswordStrength, SelectField } from '../components/Field';
import AlunoExtrato from '../components/aluno/AlunoExtrato';
import AlunoCatalogoVantagens from '../components/aluno/AlunoCatalogoVantagens';
import AlunoCupons from '../components/aluno/AlunoCupons';
import { iniciais, mascaras, membroDesde, primeiroNome } from '../utils/formatters';

const ABAS = [
  { id: 'extrato', rotulo: 'Início e extrato', icone: LayoutDashboard },
  { id: 'vantagens', rotulo: 'Vantagens', icone: Gift },
  { id: 'cupons', rotulo: 'Meus cupons', icone: Receipt },
  { id: 'perfil', rotulo: 'Meu perfil', icone: UserRoundPen },
];

const MASCARAS_CAMPOS = {
  cpf: mascaras.cpf,
  end_cep: mascaras.cep,
};

export default function AlunoDashboard() {
  const { user, login, logout } = useAuth();
  const toast = useToast();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('extrato');
  const [aluno, setAluno] = useState(user);
  const [dashboard, setDashboard] = useState(null);
  const [instituicoes, setInstituicoes] = useState([]);
  const [cursos, setCursos] = useState([]);
  const [loading, setLoading] = useState(false);
  const [carregando, setCarregando] = useState(true);
  const [confirmandoExclusao, setConfirmandoExclusao] = useState(false);
  const [excluindo, setExcluindo] = useState(false);

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

  async function carregarDadosCompletos() {
    if (!user?.id) return;
    try {
      const [instRes, cursosRes, alunoAtualizado, dashboardRes] = await Promise.all([
        instituicaoApi.listar().catch(() => []),
        instituicaoApi.listarCursos().catch(() => []),
        alunoApi.buscarPorId(user.id),
        alunoApi.obterDashboard(user.id).catch(() => null),
      ]);

      setInstituicoes(instRes || []);
      setCursos(cursosRes || []);
      setAluno(alunoAtualizado);
      setDashboard(dashboardRes);

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
      toast.erro('Não foi possível carregar os dados atualizados.');
    } finally {
      setCarregando(false);
    }
  }

  useEffect(() => {
    carregarDadosCompletos();
  }, [user]);

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

  const handleUpdate = async (e) => {
    e.preventDefault();
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
      toast.sucesso('Dados cadastrais atualizados com sucesso!');
    } catch (err) {
      toast.erro(err.message || 'Erro ao atualizar dados.');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    setExcluindo(true);
    try {
      await alunoApi.deletar(aluno.id);
      toast.sucesso('Conta excluída com sucesso.');
      logout();
      navigate('/login', { replace: true });
    } catch (err) {
      toast.erro('Erro ao excluir conta: ' + err.message);
      setExcluindo(false);
      setConfirmandoExclusao(false);
    }
  };

  const desde = membroDesde(aluno?.dataCriacao);
  const saldoAtual = dashboard?.saldoAtual ?? aluno?.saldo ?? 0;

  return (
    <DashboardShell titulo="Painel do Aluno" itens={ABAS} ativo={activeTab} onSelecionar={setActiveTab}>
      <div className="tab-panel">
        <header className="page-heading reveal">
          <h1>
            Olá, {primeiroNome(aluno?.nome) || 'aluno'}{' '}
            <span className="wave" aria-hidden="true">
              👋
            </span>
          </h1>
          <p>
            {aluno?.cursoNome && aluno?.instituicaoNome
              ? `${aluno.cursoNome} · ${aluno.instituicaoNome}`
              : 'Acompanhe seu saldo e suas movimentações de mérito acadêmico.'}
          </p>
        </header>

        {carregando ? (
          <div style={{ display: 'grid', placeItems: 'center', padding: '60px 0' }}>
            <LoaderCircle size={36} className="spin" color="var(--brand-600)" />
            <p style={{ marginTop: '12px', color: 'var(--text-muted)' }}>Carregando dados do aluno...</p>
          </div>
        ) : (
          <>
            {activeTab === 'extrato' && <AlunoExtrato dashboard={dashboard} />}

            {activeTab === 'vantagens' && (
              <AlunoCatalogoVantagens
                alunoId={aluno?.id}
                saldoAtual={saldoAtual}
                onVantagemResgatada={() => carregarDadosCompletos()}
              />
            )}

            {activeTab === 'cupons' && <AlunoCupons alunoId={aluno?.id} />}

            {activeTab === 'perfil' && (
              <div key="perfil" className="tab-panel">
                <div className="profile-hero reveal">
                  <span className="avatar avatar-lg">{iniciais(aluno?.nome)}</span>
                  <div className="profile-hero-text">
                    <span className="eyebrow-text">Meu perfil</span>
                    <h1>{aluno?.nome}</h1>
                    <div className="meta-row">
                      <span className="meta-chip">
                        <Mail size={14} /> {aluno?.email}
                      </span>
                      {desde && (
                        <span className="meta-chip">
                          <CalendarDays size={14} /> Membro desde {desde}
                        </span>
                      )}
                    </div>
                  </div>
                </div>

                <form onSubmit={handleUpdate} className="card form-card reveal" style={{ '--i': 1 }}>
                  <section className="form-section">
                    <h2 className="form-section-title">
                      <UserRound size={18} /> Dados pessoais
                    </h2>
                    <div className="form-grid">
                      <Field
                        label="Nome completo"
                        icon={UserRound}
                        name="nome"
                        autoComplete="name"
                        value={formData.nome}
                        onChange={handleChange}
                        required
                      />
                      <Field
                        label="E-mail"
                        icon={Mail}
                        type="email"
                        name="email"
                        autoComplete="email"
                        value={formData.email}
                        onChange={handleChange}
                        required
                      />
                      <Field
                        label="CPF"
                        icon={IdCard}
                        name="cpf"
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
                        value={formData.rg}
                        onChange={handleChange}
                        required
                      />
                    </div>
                  </section>

                  <section className="form-section">
                    <h2 className="form-section-title">
                      <MapPin size={18} /> Endereço
                    </h2>
                    <div className="form-grid cols-6">
                      <Field
                        className="span-4"
                        label="Logradouro"
                        name="end_logradouro"
                        autoComplete="address-line1"
                        value={formData.endereco.logradouro}
                        onChange={handleChange}
                        required
                      />
                      <Field
                        className="span-2"
                        label="Número"
                        name="end_numero"
                        value={formData.endereco.numero}
                        onChange={handleChange}
                        required
                      />
                      <Field
                        className="span-3"
                        label="Complemento"
                        name="end_complemento"
                        placeholder="Opcional"
                        autoComplete="address-line2"
                        value={formData.endereco.complemento || ''}
                        onChange={handleChange}
                      />
                      <Field
                        className="span-3"
                        label="Bairro"
                        name="end_bairro"
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
                        inputMode="numeric"
                        maxLength={9}
                        autoComplete="postal-code"
                        value={formData.endereco.cep}
                        onChange={handleChange}
                        required
                      />
                    </div>
                  </section>

                  <section className="form-section">
                    <h2 className="form-section-title">
                      <School size={18} /> Vínculo acadêmico
                    </h2>
                    <div className="form-grid">
                      <SelectField
                        label="Instituição"
                        icon={School}
                        name="instituicaoId"
                        options={instituicoes}
                        value={formData.instituicaoId}
                        onChange={handleChange}
                        required
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
                  </section>

                  <section className="form-section">
                    <h2 className="form-section-title">
                      <KeyRound size={18} /> Alterar senha
                    </h2>
                    <div className="form-grid">
                      <PasswordField
                        label="Nova senha"
                        icon={Lock}
                        name="senha"
                        placeholder="Deixe em branco para manter a atual"
                        autoComplete="new-password"
                        value={formData.senha}
                        onChange={handleChange}
                      />
                    </div>
                    {formData.senha && <PasswordStrength password={formData.senha} />}
                  </section>

                  <div className="form-actions">
                    <button
                      type="button"
                      className="btn btn-danger btn-sm"
                      onClick={() => setConfirmandoExclusao(true)}
                    >
                      <Trash2 size={16} /> Excluir conta
                    </button>
                    <button type="submit" className="btn btn-primary" disabled={loading}>
                      {loading ? (
                        <>
                          <LoaderCircle size={18} className="spin" /> Salvando...
                        </>
                      ) : (
                        <>
                          <Save size={18} /> Salvar alterações
                        </>
                      )}
                    </button>
                  </div>
                </form>

                <ConfirmDialog
                  aberto={confirmandoExclusao}
                  titulo="Excluir minha conta?"
                  descricao="Essa ação não pode ser desfeita. Todo o seu saldo, histórico de transações e cupons serão excluídos permanentemente."
                  rotuloConfirmar="Sim, excluir conta"
                  perigoso
                  carregando={excluindo}
                  onConfirmar={handleDelete}
                  onCancelar={() => setConfirmandoExclusao(false)}
                />
              </div>
            )}
          </>
        )}
      </div>
    </DashboardShell>
  );
}
