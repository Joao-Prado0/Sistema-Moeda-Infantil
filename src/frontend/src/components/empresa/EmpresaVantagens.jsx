import { useEffect, useState } from 'react';
import { Edit2, Gift, LoaderCircle, Plus, Trash2 } from 'lucide-react';
import { empresaApi } from '../../services/api';
import { useToast } from '../../context/ToastContext';
import { formatarMoedas } from '../../utils/formatters';
import ModalVantagemForm from './ModalVantagemForm';
import ConfirmDialog from '../ConfirmDialog';

export default function EmpresaVantagens({ empresaId }) {
  const toast = useToast();
  const [vantagens, setVantagens] = useState([]);
  const [carregando, setCarregando] = useState(true);

  const [modalAberto, setModalAberto] = useState(false);
  const [vantagemEmEdicao, setVantagemEmEdicao] = useState(null);

  const [vantagemParaExcluir, setVantagemParaExcluir] = useState(null);
  const [excluindo, setExcluindo] = useState(false);

  async function carregarVantagens() {
    if (!empresaId) return;
    try {
      const lista = await empresaApi.listarVantagens(empresaId);
      setVantagens(lista || []);
    } catch (err) {
      toast.erro('Não foi possível carregar as vantagens: ' + err.message);
    } finally {
      setCarregando(false);
    }
  }

  useEffect(() => {
    carregarVantagens();
  }, [empresaId]);

  const handleExcluir = async () => {
    if (!vantagemParaExcluir) return;
    setExcluindo(true);
    try {
      await empresaApi.excluirVantagem(empresaId, vantagemParaExcluir.id);
      toast.sucesso('Vantagem excluída com sucesso.');
      setVantagemParaExcluir(null);
      carregarVantagens();
    } catch (err) {
      toast.erro(err.message || 'Erro ao excluir vantagem. Pode haver cupons vinculados a ela.');
    } finally {
      setExcluindo(false);
    }
  };

  return (
    <div className="tab-panel">
      <header className="page-heading reveal" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '16px' }}>
        <div>
          <h2>Minhas vantagens</h2>
          <p>Cadastre e gerencie os benefícios e descontos oferecidos aos estudantes.</p>
        </div>
        <button
          type="button"
          className="btn btn-primary btn-sm"
          onClick={() => {
            setVantagemEmEdicao(null);
            setModalAberto(true);
          }}
        >
          <Plus size={16} /> Nova vantagem
        </button>
      </header>

      {carregando ? (
        <div style={{ display: 'grid', placeItems: 'center', padding: '60px 0' }}>
          <LoaderCircle size={36} className="spin" color="var(--brand-600)" />
          <p style={{ marginTop: '12px', color: 'var(--text-muted)' }}>Carregando suas vantagens...</p>
        </div>
      ) : vantagens.length === 0 ? (
        <div className="card empty-state reveal">
          <div className="empty-illustration">
            <Gift size={34} />
          </div>
          <h3>Você ainda não cadastrou vantagens</h3>
          <p>Clique no botão acima para adicionar seu primeiro produto ou desconto.</p>
        </div>
      ) : (
        <div className="card reveal" style={{ padding: 0, overflow: 'hidden' }}>
          <div className="data-table-container" style={{ border: 'none', boxShadow: 'none' }}>
            <table className="data-table">
              <thead>
                <tr>
                  <th style={{ width: '80px' }}>Foto</th>
                  <th>Título</th>
                  <th>Descrição</th>
                  <th>Custo</th>
                  <th style={{ textAlign: 'right' }}>Ações</th>
                </tr>
              </thead>
              <tbody>
                {vantagens.map((v) => (
                  <tr key={v.id}>
                    <td>
                      <div
                        style={{
                          width: '56px',
                          height: '48px',
                          borderRadius: '8px',
                          overflow: 'hidden',
                          background: 'var(--surface-muted)',
                          display: 'grid',
                          placeItems: 'center',
                          border: '1px solid var(--border)',
                        }}
                      >
                        {v.fotoUrl ? (
                          <img
                            src={v.fotoUrl}
                            alt={v.titulo}
                            style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                            onError={(e) => {
                              e.target.style.display = 'none';
                            }}
                          />
                        ) : (
                          <Gift size={20} color="var(--brand-500)" />
                        )}
                      </div>
                    </td>
                    <td style={{ fontWeight: 600 }}>{v.titulo}</td>
                    <td style={{ color: 'var(--text-muted)', fontSize: '0.88rem', maxWidth: '320px' }}>
                      {v.descricao}
                    </td>
                    <td style={{ fontWeight: 700, color: 'var(--gold-700)', whiteSpace: 'nowrap' }}>
                      {formatarMoedas(v.custoMoedas)} moedas
                    </td>
                    <td style={{ textAlign: 'right', whiteSpace: 'nowrap' }}>
                      <div style={{ display: 'inline-flex', gap: '8px' }}>
                        <button
                          type="button"
                          className="btn btn-ghost btn-sm"
                          style={{ padding: '6px 10px', height: 'auto' }}
                          onClick={() => {
                            setVantagemEmEdicao(v);
                            setModalAberto(true);
                          }}
                          title="Editar vantagem"
                        >
                          <Edit2 size={15} /> Editar
                        </button>
                        <button
                          type="button"
                          className="btn btn-ghost btn-sm"
                          style={{ padding: '6px 10px', height: 'auto', color: 'var(--danger-600)' }}
                          onClick={() => setVantagemParaExcluir(v)}
                          title="Excluir vantagem"
                        >
                          <Trash2 size={15} /> Excluir
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {modalAberto && (
        <ModalVantagemForm
          empresaId={empresaId}
          vantagemEmEdicao={vantagemEmEdicao}
          onClose={() => setModalAberto(false)}
          onSucesso={() => carregarVantagens()}
        />
      )}

      <ConfirmDialog
        aberto={Boolean(vantagemParaExcluir)}
        titulo="Excluir vantagem?"
        descricao={`Tem certeza que deseja remover a vantagem "${vantagemParaExcluir?.titulo}"? Essa ação não pode ser desfeita.`}
        rotuloConfirmar="Sim, excluir"
        perigoso
        carregando={excluindo}
        onConfirmar={handleExcluir}
        onCancelar={() => setVantagemParaExcluir(null)}
      />
    </div>
  );
}

