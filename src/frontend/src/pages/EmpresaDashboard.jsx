import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Building2,
  CalendarDays,
  Gift,
  Hash,
  KeyRound,
  LoaderCircle,
  Lock,
  Mail,
  Save,
  Store,
  TicketCheck,
  Trash2,
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import { empresaApi } from '../services/api';
import DashboardShell from '../components/DashboardShell';
import ConfirmDialog from '../components/ConfirmDialog';
import FormSkeleton from '../components/FormSkeleton';
import { Field, PasswordField, PasswordStrength } from '../components/Field';
import EmpresaVantagens from '../components/empresa/EmpresaVantagens';
import EmpresaConferirCupons from '../components/empresa/EmpresaConferirCupons';
import { iniciais, mascaras, membroDesde } from '../utils/formatters';

const SECOES = [
  { id: 'vantagens', rotulo: 'Minhas vantagens', icone: Gift },
  { id: 'cupons', rotulo: 'Conferir cupons', icone: TicketCheck },
  { id: 'empresa', rotulo: 'Meu perfil', icone: Building2 },
];

export default function EmpresaDashboard() {
  const { user, login, logout } = useAuth();
  const toast = useToast();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('vantagens');
  const [empresa, setEmpresa] = useState(user);
  const [loading, setLoading] = useState(false);
  const [carregando, setCarregando] = useState(true);
  const [confirmandoExclusao, setConfirmandoExclusao] = useState(false);
  const [excluindo, setExcluindo] = useState(false);

  const [formData, setFormData] = useState({
    nomeFantasia: '',
    email: '',
    cnpj: '',
    senha: '',
  });

  useEffect(() => {
    if (!user?.id) return;

    async function carregar() {
      try {
        const empAtualizada = await empresaApi.buscarPorId(user.id);
        setEmpresa(empAtualizada);
        setFormData({
          nomeFantasia: empAtualizada.nomeFantasia || '',
          email: empAtualizada.email || '',
          cnpj: empAtualizada.cnpj || '',
          senha: '',
        });
      } catch (err) {
        console.error('Erro ao buscar dados da empresa:', err);
        toast.erro('Não foi possível carregar os dados da empresa.');
      } finally {
        setCarregando(false);
      }
    }
    carregar();
  }, [user, toast]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: name === 'cnpj' ? mascaras.cnpj(value) : value }));
  };

  const handleUpdate = async (e) => {
    e.preventDefault();
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
      toast.sucesso('Dados da empresa atualizados com sucesso!');
    } catch (err) {
      toast.erro(err.message || 'Erro ao atualizar empresa.');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    setExcluindo(true);
    try {
      await empresaApi.deletar(empresa.id);
      toast.sucesso('Cadastro da empresa excluído com sucesso.');
      logout();
      navigate('/login', { replace: true });
    } catch (err) {
      toast.erro('Erro ao excluir empresa: ' + err.message);
      setExcluindo(false);
      setConfirmandoExclusao(false);
    }
  };

  const desde = membroDesde(empresa?.dataCriacao);

  return (
    <DashboardShell
      titulo="Painel da Empresa"
      itens={SECOES}
      ativo={activeTab}
      onSelecionar={setActiveTab}
    >
      <div className="tab-panel">
        {activeTab === 'vantagens' && <EmpresaVantagens empresaId={empresa?.id} />}

        {activeTab === 'cupons' && <EmpresaConferirCupons empresaId={empresa?.id} />}

        {activeTab === 'empresa' && (
          <div key="empresa" className="tab-panel">
            <div className="profile-hero reveal">
              <span className="avatar avatar-lg avatar-gold">{iniciais(empresa?.nomeFantasia)}</span>
              <div className="profile-hero-text">
                <span className="eyebrow-text">Empresa parceira</span>
                <h1>{empresa?.nomeFantasia}</h1>
                <div className="meta-row">
                  <span className="meta-chip">
                    <Mail size={14} /> {empresa?.email}
                  </span>
                  {desde && (
                    <span className="meta-chip">
                      <CalendarDays size={14} /> Parceira desde {desde}
                    </span>
                  )}
                </div>
              </div>
            </div>

            {carregando ? (
              <FormSkeleton campos={4} />
            ) : (
              <form onSubmit={handleUpdate} className="card form-card reveal" style={{ '--i': 1 }}>
                <section className="form-section">
                  <h2 className="form-section-title">
                    <Store size={18} /> Dados da empresa
                  </h2>
                  <div className="form-grid">
                    <Field
                      label="Nome fantasia"
                      icon={Store}
                      name="nomeFantasia"
                      value={formData.nomeFantasia}
                      onChange={handleChange}
                      required
                    />
                    <Field
                      label="CNPJ"
                      icon={Hash}
                      name="cnpj"
                      inputMode="numeric"
                      maxLength={18}
                      value={formData.cnpj}
                      onChange={handleChange}
                      required
                    />
                    <Field
                      className="span-2"
                      label="E-mail de contato"
                      icon={Mail}
                      type="email"
                      name="email"
                      autoComplete="email"
                      value={formData.email}
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
                    <Trash2 size={16} /> Excluir cadastro
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
            )}

            <ConfirmDialog
              aberto={confirmandoExclusao}
              titulo="Excluir cadastro da empresa?"
              descricao="Essa ação não pode ser desfeita. Todas as vantagens cadastradas e cupons vinculados serão excluídos permanentemente."
              rotuloConfirmar="Sim, excluir cadastro"
              perigoso
              carregando={excluindo}
              onConfirmar={handleDelete}
              onCancelar={() => setConfirmandoExclusao(false)}
            />
          </div>
        )}
      </div>
    </DashboardShell>
  );
}
