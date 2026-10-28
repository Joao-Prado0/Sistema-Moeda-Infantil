import { useEffect, useState } from 'react';
import { Check, Copy, Inbox, LoaderCircle, Receipt, TicketCheck } from 'lucide-react';
import { alunoApi } from '../../services/api';
import { useToast } from '../../context/ToastContext';
import { formatarMoedas } from '../../utils/formatters';

function formatarData(dataIso) {
  if (!dataIso) return '—';
  try {
    const d = new Date(dataIso);
    return d.toLocaleDateString('pt-BR');
  } catch {
    return dataIso;
  }
}

export default function AlunoCupons({ alunoId }) {
  const toast = useToast();
  const [cupons, setCupons] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [copiadoId, setCopiadoId] = useState(null);

  async function carregarCupons() {
    try {
      const lista = await alunoApi.listarCupons(alunoId);
      setCupons(lista || []);
    } catch (err) {
      toast.erro('Não foi possível carregar seus cupons: ' + err.message);
    } finally {
      setCarregando(false);
    }
  }

  useEffect(() => {
    carregarCupons();
  }, [alunoId]);

  const handleCopiarCodigo = (codigo, id) => {
    navigator.clipboard.writeText(codigo);
    setCopiadoId(id);
    toast.sucesso('Código do cupom copiado para a área de transferência!');
    setTimeout(() => setCopiadoId(null), 2500);
  };

  return (
    <div className="tab-panel">
      <article className="card reveal">
        <header className="card-title-row">
          <div>
            <h2 className="card-title">
              <Receipt size={18} /> Meus cupons
            </h2>
            <p className="muted">Apresente o código do cupom na empresa parceira para usufruir da vantagem.</p>
          </div>
        </header>

        {carregando ? (
          <div style={{ display: 'grid', placeItems: 'center', padding: '60px 0' }}>
            <LoaderCircle size={36} className="spin" color="var(--brand-600)" />
            <p style={{ marginTop: '12px', color: 'var(--text-muted)' }}>Carregando cupons...</p>
          </div>
        ) : cupons.length === 0 ? (
          <div className="empty-state">
            <div className="empty-illustration">
              <Inbox size={34} />
            </div>
            <h3>Nenhum cupom gerado ainda</h3>
            <p>Acesse o catálogo de vantagens e resgate benefícios com suas moedas.</p>
          </div>
        ) : (
          <div className="data-table-container">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Código de conferência</th>
                  <th>Vantagem</th>
                  <th>Empresa</th>
                  <th>Data do resgate</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {cupons.map((c) => {
                  const ehUtilizado = c.status === 'UTILIZADO';
                  const foiCopiado = copiadoId === c.id;

                  return (
                    <tr key={c.id}>
                      <td>
                        <div style={{ display: 'inline-flex', alignItems: 'center', gap: '8px' }}>
                          <code
                            style={{
                              fontFamily: 'monospace',
                              fontWeight: 700,
                              fontSize: '0.9rem',
                              padding: '3px 8px',
                              borderRadius: '6px',
                              background: 'var(--surface-muted)',
                              border: '1px solid var(--border)',
                              letterSpacing: '0.04em',
                            }}
                          >
                            {c.codigo}
                          </code>
                          <button
                            type="button"
                            className="btn btn-ghost btn-sm"
                            style={{ padding: '4px 6px', height: 'auto' }}
                            onClick={() => handleCopiarCodigo(c.codigo, c.id)}
                            title="Copiar código"
                          >
                            {foiCopiado ? <Check size={14} color="var(--success-700)" /> : <Copy size={14} />}
                          </button>
                        </div>
                      </td>
                      <td style={{ fontWeight: 600 }}>{c.nomeVantagem}</td>
                      <td>{c.nomeEmpresa}</td>
                      <td>{formatarData(c.dataResgate)}</td>
                      <td>
                        <span className={`table-badge ${ehUtilizado ? 'badge-utilizado' : 'badge-gerado'}`}>
                          {ehUtilizado ? (
                            <>
                              <TicketCheck size={13} /> Utilizado
                            </>
                          ) : (
                            'Gerado'
                          )}
                        </span>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </article>
    </div>
  );
}

