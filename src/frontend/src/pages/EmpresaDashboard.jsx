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
  Trash2,
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import { empresaApi } from '../services/api';
import DashboardShell from '../components/DashboardShell';
import ConfirmDialog from '../components/ConfirmDialog';
import FormSkeleton from '../components/FormSkeleton';
import { Field, PasswordField, PasswordStrength } from '../components/Field';
import { iniciais, mascaras, membroDesde } from '../utils/formatters';

const SECOES = [
  { id: 'empresa', rotulo: 'Minha empresa', icone: Building2 },
  { id: 'vantagens', rotulo: 'Vantagens', icone: Gift, emBreve: true },
];

export default function EmpresaDashboard() {
  const { user, login, logout } = useAuth();
  const toast = useToast();
  const navigate = useNavigate();
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
    if (!user) return;

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
    <DashboardShell titulo="Painel da empresa" itens={SECOES} ativo="empresa">
      <div className="tab-panel">
        <div className="profile-hero reveal">
          <span className="avatar avatar-lg avatar-gold">{iniciais(empresa?.nomeFantasia)}</span>
          <div className="profile-hero-text">
            <span className="eyebrow-text">Empresa parceira</span>
            <h1>{empresa?.nomeFantasia}</h1>
            <div className="meta-row">
              <span className="meta-chip">
                <Hash size={14} /> {empresa?.cnpj}
              </span>
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
          <FormSkeleton campos={3} />
        ) : (
          <form onSubmit={handleUpdate} className="card form-card reveal" style={{ '--i': 1 }}>
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
                  autoComplete="organization"
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
                  label="E-mail"
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
                <KeyRound size={18} /> Segurança
              </h2>
              <div className="form-grid">
                <PasswordField
                  label="Nova senha (opcional)"
                  icon={Lock}
                  name="senha"
                  placeholder="Deixe em branco para manter"
                  autoComplete="new-password"
                  minLength={6}
                  value={formData.senha}
                  onChange={handleChange}
                />
              </div>
              <div className="password-feedback">
                <PasswordStrength senha={formData.senha} />
              </div>
            </section>

            <div className="form-actions">
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

        <section className="danger-zone reveal" style={{ '--i': 2 }}>
          <div>
            <h2>Excluir cadastro da empresa</h2>
            <p>Esta ação é permanente e não pode ser desfeita.</p>
          </div>
          <button type="button" className="btn btn-danger-soft" onClick={() => setConfirmandoExclusao(true)}>
            <Trash2 size={18} /> Excluir cadastro
          </button>
        </section>
      </div>

      <ConfirmDialog
        aberto={confirmandoExclusao}
        titulo="Excluir o cadastro da empresa?"
        descricao="Todos os dados da empresa parceira serão removidos permanentemente. Essa ação não pode ser desfeita."
        textoConfirmar="Sim, excluir cadastro"
        textoCarregando="Excluindo..."
        carregando={excluindo}
        onConfirmar={handleDelete}
        onCancelar={() => setConfirmandoExclusao(false)}
      />
    </DashboardShell>
  );
}
