import './ProfessorDashboardPage.css';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Building2,
  CalendarDays,
  Coins,
  IdCard,
  KeyRound,
  LayoutDashboard,
  LoaderCircle,
  Lock,
  Mail,
  RotateCcw,
  Save,
  School,
  Send,
  Trash2,
  UserRound,
  UserRoundPen,
} from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';
import { useToast } from '../../contexts/ToastContext';
import { professorApi, instituicaoApi } from '../../services/api';
import DashboardShell from '../../layouts/AppLayout';
import ConfirmDialog from '../../components/ui/ConfirmDialog';
import { Field, PasswordField, PasswordStrength, SelectField } from '../../components/ui/Input';
import ProfessorExtrato from '../../resources/Professor/components/ProfessorExtrato';
import ProfessorEnviarMoedas from '../../resources/Professor/components/ProfessorEnviarMoedas';
import { iniciais, mascaras, membroDesde } from '../../utils/formatters';

const ABAS = [
  { id: 'extrato', rotulo: 'Início e extrato', icone: LayoutDashboard },
  { id: 'envio', rotulo: 'Enviar moedas', icone: Send },
  { id: 'perfil', rotulo: 'Meu perfil', icone: UserRoundPen },
];

export default function ProfessorDashboard() {
  const { user, login, logout } = useAuth();
  const toast = useToast();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('extrato');
  const [dashboard, setDashboard] = useState(null);
  const [professor, setProfessor] = useState(user);
  const [instituicoes, setInstituicoes] = useState([]);
  const [departamentos, setDepartamentos] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [aplicandoCredito, setAplicandoCredito] = useState(false);
  const [salvando, setSalvando] = useState(false);
  const [confirmandoExclusao, setConfirmandoExclusao] = useState(false);
  const [excluindo, setExcluindo] = useState(false);

  const [formData, setFormData] = useState({
    nome: '',
    email: '',
    cpf: '',
    instituicaoId: '',
    departamentoId: '',
    senha: '',
  });

  async function carregarDadosCompletos() {
    if (!user?.id) return;
    try {
      const [dashData, profData, instList, deptList] = await Promise.all([
        professorApi.obterDashboard(user.id).catch(() => null),
        professorApi.buscarPorId(user.id).catch(() => null),
        instituicaoApi.listar().catch(() => []),
        instituicaoApi.listarDepartamentos().catch(() => []),
      ]);

      setDashboard(dashData);
      setInstituicoes(instList || []);
      setDepartamentos(deptList || []);

      const profAtual = profData || user;
      setProfessor(profAtual);

      setFormData({
        nome: profAtual.nome || '',
        email: profAtual.email || '',
        cpf: profAtual.cpf || '',
        instituicaoId: profAtual.instituicaoId || '',
        departamentoId: profAtual.departamentoId || '',
        senha: '',
      });
    } catch (err) {
      toast.erro(err.message || 'Erro ao carregar dados do professor.');
    } finally {
      setCarregando(false);
    }
  }

  useEffect(() => {
    carregarDadosCompletos();
  }, [user]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: name === 'cpf' ? mascaras.cpf(value) : value,
    }));
  };

  const handleReset = () => {
    if (!professor) return;
    setFormData({
      nome: professor.nome || '',
      email: professor.email || '',
      cpf: professor.cpf || '',
      instituicaoId: professor.instituicaoId || '',
      departamentoId: professor.departamentoId || '',
      senha: '',
    });
    toast.sucesso('Dados originais restaurados.');
  };

  const handleUpdate = async (e) => {
    e.preventDefault();
    setSalvando(true);

    try {
      const atualizado = await professorApi.atualizar(professor.id, {
        nome: formData.nome,
        email: formData.email,
        cpf: formData.cpf,
        instituicaoId: Number(formData.instituicaoId),
        departamentoId: Number(formData.departamentoId),
        senha: formData.senha || undefined,
      });

      setProfessor(atualizado);
      login(atualizado);
      toast.sucesso('Dados cadastrais atualizados com sucesso!');
      await carregarDadosCompletos();
    } catch (err) {
      toast.erro(err.message || 'Erro ao atualizar dados do professor.');
    } finally {
      setSalvando(false);
    }
  };

  const handleDelete = async () => {
    setExcluindo(true);
    try {
      await professorApi.deletar(professor.id);
      toast.sucesso('Conta de professor excluída com sucesso.');
      logout();
      navigate('/login', { replace: true });
    } catch (err) {
      toast.erro('Erro ao excluir conta: ' + err.message);
      setExcluindo(false);
      setConfirmandoExclusao(false);
    }
  };

  const handleCreditoSemestral = async () => {
    setAplicandoCredito(true);
    try {
      await professorApi.aplicarCreditoSemestral();
      toast.sucesso('Crédito semestral de 1.000 moedas aplicado com sucesso!');
      await carregarDadosCompletos();
    } catch (err) {
      toast.erro(err.message || 'Erro ao aplicar crédito semestral.');
    } finally {
      setAplicandoCredito(false);
    }
  };

  const vinculo = [dashboard?.departamentoNome, dashboard?.instituicaoNome]
    .filter(Boolean)
    .join(' · ');

  const nomeBruto = dashboard?.nome || professor?.nome || user?.nome || 'Professor';
  const nomeProfessor = nomeBruto.replace(/^(prof(a)?\.?|professor(a)?)\s+/i, '');
  const desde = membroDesde(professor?.dataCriacao);

  return (
    <DashboardShell
      titulo="Painel do Professor"
      itens={ABAS}
      ativo={activeTab}
      onSelecionar={setActiveTab}
    >
      <div className="tab-panel">
        {activeTab !== 'perfil' && (
          <header
            className="page-heading reveal"
            style={{
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'flex-start',
              flexWrap: 'wrap',
              gap: '16px',
            }}
          >
            <div>
              <h1>
                Olá, Prof. {nomeProfessor}{' '}
                <span className="wave" aria-hidden="true">
                  👋
                </span>
              </h1>
              <p>
                {vinculo ? (
                  <span style={{ display: 'inline-flex', alignItems: 'center', gap: '6px' }}>
                    <School size={15} /> {vinculo}
                  </span>
                ) : (
                  'Reconheça o mérito dos seus alunos distribuindo moedas estudantis.'
                )}
              </p>
            </div>

            {activeTab === 'extrato' && (
              <button
                type="button"
                className="btn btn-secondary btn-sm"
                onClick={handleCreditoSemestral}
                disabled={aplicandoCredito || carregando}
                title="Simula a virada de semestre creditando 1.000 moedas acumuláveis ao saldo"
              >
                {aplicandoCredito ? (
                  <>
                    <LoaderCircle size={14} className="spin" /> Creditando...
                  </>
                ) : (
                  <>
                    <Coins size={14} /> +1.000 Crédito semestral
                  </>
                )}
              </button>
            )}
          </header>
        )}

        {carregando ? (
          <div className="loading-state" style={{ display: 'grid', placeItems: 'center', padding: '60px 0' }}>
            <LoaderCircle size={36} className="spin" color="var(--brand-600)" />
            <p style={{ marginTop: '12px', color: 'var(--text-muted)' }}>Carregando dados do professor...</p>
          </div>
        ) : (
          <>
            {activeTab === 'extrato' && <ProfessorExtrato dashboard={dashboard} />}

            {activeTab === 'envio' && (
              <ProfessorEnviarMoedas
                professorId={user?.id}
                saldoDisponivel={dashboard?.saldoDisponivel}
                onSucesso={() => {
                  carregarDadosCompletos();
                  setActiveTab('extrato');
                }}
                onCancelar={() => setActiveTab('extrato')}
              />
            )}

            {activeTab === 'perfil' && (
              <div key="perfil" className="tab-panel">
                <div className="profile-hero reveal">
                  <span className="avatar avatar-lg">{iniciais(professor?.nome)}</span>
                  <div className="profile-hero-text">
                    <span className="eyebrow-text">Meu perfil</span>
                    <h1>Prof. {nomeProfessor}</h1>
                    <div className="meta-row">
                      <span className="meta-chip">
                        <Mail size={14} /> {professor?.email}
                      </span>
                      {desde && (
                        <span className="meta-chip">
                          <CalendarDays size={14} /> Docente desde {desde}
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
                        label="E-mail institucional"
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
                    </div>
                  </section>

                  <section className="form-section">
                    <h2 className="form-section-title">
                      <School size={18} /> Vínculo docente
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
                    <div className="form-actions-right">
                      <button
                        type="button"
                        className="btn btn-secondary btn-sm"
                        onClick={handleReset}
                        disabled={salvando}
                      >
                        <RotateCcw size={15} /> Cancelar
                      </button>
                      <button type="submit" className="btn btn-primary" disabled={salvando}>
                        {salvando ? (
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
                  </div>
                </form>

                <ConfirmDialog
                  aberto={confirmandoExclusao}
                  titulo="Excluir minha conta?"
                  descricao="Essa ação não pode ser desfeita. Todo o seu saldo de moedas e histórico de envios serão excluídos permanentemente."
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
