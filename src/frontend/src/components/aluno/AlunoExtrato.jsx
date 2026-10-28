import { ArrowDownLeft, ArrowUpRight, Coins, Gift, History, Inbox, Wallet } from 'lucide-react';
import useCountUp from '../../hooks/useCountUp';
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

export default function AlunoExtrato({ dashboard }) {
  const saldoAnimado = useCountUp(dashboard?.saldoAtual ?? 0);
  const totalRecebidoAnimado = useCountUp(dashboard?.totalRecebido ?? 0);
  const totalTrocadoAnimado = useCountUp(dashboard?.totalTrocado ?? 0);
  const extrato = dashboard?.extrato || [];

  return (
    <div className="tab-panel">
      {/* 3 Cards Métricos (Wireframe 04) */}
      <div className="metrics-3col">
        <article className="stat-card reveal" style={{ '--i': 1 }}>
          <span className="icon-badge icon-badge-gold">
            <Wallet size={20} />
          </span>
          <div>
            <span className="stat-label">Saldo atual</span>
            <strong className="stat-value">{formatarMoedas(saldoAnimado)} moedas</strong>
          </div>
        </article>

        <article className="stat-card reveal" style={{ '--i': 2 }}>
          <span className="icon-badge icon-badge-brand">
            <Coins size={20} />
          </span>
          <div>
            <span className="stat-label">Recebidas no total</span>
            <strong className="stat-value">{formatarMoedas(totalRecebidoAnimado)}</strong>
          </div>
        </article>

        <article className="stat-card reveal" style={{ '--i': 3 }}>
          <span className="icon-badge icon-badge-brand">
            <Gift size={20} />
          </span>
          <div>
            <span className="stat-label">Trocadas no total</span>
            <strong className="stat-value">{formatarMoedas(totalTrocadoAnimado)}</strong>
          </div>
        </article>
      </div>

      {/* Extrato de Transações (Wireframe 04) */}
      <article className="card reveal" style={{ '--i': 4 }}>
        <div className="card-title-row">
          <h2 className="card-title">
            <History size={18} /> Extrato
          </h2>
          <span className="muted">Movimentações de mérito acadêmico e resgates</span>
        </div>

        {extrato.length === 0 ? (
          <div className="empty-state">
            <div className="empty-illustration">
              <Inbox size={34} />
            </div>
            <h3>Nenhuma movimentação ainda</h3>
            <p>Quando você receber moedas ou resgatar uma vantagem, tudo aparecerá aqui.</p>
          </div>
        ) : (
          <div className="data-table-container">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Data</th>
                  <th>Tipo</th>
                  <th>Descrição</th>
                  <th style={{ textAlign: 'right' }}>Moedas</th>
                </tr>
              </thead>
              <tbody>
                {extrato.map((item, index) => {
                  const ehResgate = item.tipo === 'RESGATE_VANTAGEM';
                  const valor = item.valor ?? 0;
                  const ehPositivo = valor > 0;

                  return (
                    <tr key={index}>
                      <td style={{ whiteSpace: 'nowrap' }}>{formatarData(item.data)}</td>
                      <td>
                        <span className={`table-badge ${ehResgate ? 'badge-troca' : 'badge-recebimento'}`}>
                          {ehResgate ? (
                            <>
                              <ArrowUpRight size={13} /> Troca
                            </>
                          ) : (
                            <>
                              <ArrowDownLeft size={13} /> Recebimento
                            </>
                          )}
                        </span>
                      </td>
                      <td>{item.descricao || 'Movimentação de moedas'}</td>
                      <td style={{ textAlign: 'right' }}>
                        <span className={ehPositivo ? 'moedas-pos' : 'moedas-neg'}>
                          {ehPositivo ? `+ ${formatarMoedas(valor)}` : `- ${formatarMoedas(Math.abs(valor))}`}
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

