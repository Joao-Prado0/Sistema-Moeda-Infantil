import { useEffect, useState } from 'react';
import {
  Coins,
  LayoutDashboard,
  LoaderCircle,
  PlusCircle,
  School,
  Send,
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import { professorApi } from '../services/api';
import DashboardShell from '../components/DashboardShell';
import ProfessorExtrato from '../components/professor/ProfessorExtrato';
import ProfessorEnviarMoedas from '../components/professor/ProfessorEnviarMoedas';

const ABAS = [
  { id: 'extrato', rotulo: 'Início e extrato', icone: LayoutDashboard },
  { id: 'envio', rotulo: 'Enviar moedas', icone: Send },
];

export default function ProfessorDashboard() {
  const { user } = useAuth();
  const toast = useToast();
  const [activeTab, setActiveTab] = useState('extrato');
  const [dashboard, setDashboard] = useState(null);
  const [carregando, setCarregando] = useState(true);
  const [aplicandoCredito, setAplicandoCredito] = useState(false);

  async function carregarDados() {
    if (!user?.id) return;
    try {
      const data = await professorApi.obterDashboard(user.id);
      setDashboard(data);
    } catch (err) {
      toast.erro(err.message || 'Erro ao carregar dados do professor.');
    } finally {
      setCarregando(false);
    }
  }

  useEffect(() => {
    carregarDados();
  }, [user]);

  const handleCreditoSemestral = async () => {
    setAplicandoCredito(true);
    try {
      await professorApi.aplicarCreditoSemestral();
      toast.sucesso('Crédito semestral de 1.000 moedas aplicado com sucesso!');
      await carregarDados();
    } catch (err) {
      toast.erro(err.message || 'Erro ao aplicar crédito semestral.');
    } finally {
      setAplicandoCredito(false);
    }
  };

  const vinculo = [dashboard?.departamentoNome, dashboard?.instituicaoNome]
    .filter(Boolean)
    .join(' · ');

  return (
    <DashboardShell
      titulo="Painel do Professor"
      itens={ABAS}
      ativo={activeTab}
      onSelecionar={setActiveTab}
    >
      <div className="tab-panel">
        <header className="page-heading reveal" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '16px' }}>
          <div>
            <h1>
              Olá, Prof. {dashboard?.nome || user?.nome || 'Professor'}{' '}
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
                  carregarDados();
                  setActiveTab('extrato');
                }}
                onCancelar={() => setActiveTab('extrato')}
              />
            )}
          </>
        )}
      </div>
    </DashboardShell>
  );
}
