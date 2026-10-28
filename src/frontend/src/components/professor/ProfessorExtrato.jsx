import { ArrowDownLeft, ArrowUpRight, Coins, History, Inbox, Sparkles, Wallet } from 'lucide-react';
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

export default function ProfessorExtrato({ dashboard }) {
  const saldoAnimado = useCountUp(dashboard?.saldoDisponivel ?? 0);
  const totalEnviadoAnimado = useCountUp(dashboard?.totalEnviado ?? 0);
  const creditoSemestral = dashboard?.creditoSemestral ?? 1000;
  const extrato = dashboard?.extrato || [];

  return (
    <div className="tab-panel">
      {/* 3 Cards Métricos (Wireframe 09) */}
      <div className="metrics-3col">
        <article className="stat-card reveal" style={{ '--i': 1 }}>
          <span className="icon-badge icon-badge-gold">
            <Wallet size={20} />
          </span>
          <div>
            <span className="stat-label">Saldo disponível</span>
            <strong className="stat-value">{formatarMoedas(saldoAnimado)} moedas</strong>
          </div>
        </article>

        <article className="stat-card reveal" style={{ '--i': 2 }}>
          <span className="icon-badge icon-badge-brand">
            <Coins size={20} />
          </span>
          <div>
            <span className="stat-label">Crédito por semestre</span>
            <strong className="stat-value">{formatarMoedas(creditoSemestral)} moedas</strong>
          </div>
        </article>

        <article className="stat-card reveal" style={{ '--i': 3 }}>
          <span className="icon-badge icon-badge-brand">
            <History size={20} />
          </span>
          <div>
            <span className="stat-label">Enviadas no total</span>
            <strong className="stat-value">{formatarMoedas(totalEnviadoAnimado)} moedas</strong>
          </div>
        </article>
      </div>

      {/* Extrato de Envios (Wireframe 09) */}
      <article className="card reveal" style={{ '--i': 4 }}>
        <div className="card-title-row">
          <h2 className="card-title">
            <History size={18} /> Extrato de envios
          </h2>
          <span className="muted">Histórico de transferências e créditos</span>
        </div>

        {extrato.length === 0 ? (
          <div className="empty-state">
            <div className="empty-illustration">
              <Inbox size={34} />
            </div>
            <h3>Nenhum envio realizado ainda</h3>
            <p>Quando você bonificar um aluno pelo mérito acadêmico, o registro aparecerá aqui.</p>
          </div>
        ) : (
          <div className="data-table-container">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Data</th>
                  <th>Tipo</th>
                  <th>Aluno</th>
                  <th>Motivo</th>
                  <th style={{ textAlign: 'right' }}>Moedas</th>
                </tr>
              </thead>
              <tbody>
                {extrato.map((item, index) => {
                  const ehCredito = item.tipo === 'CREDITO_SEMESTRAL';
                  const valor = item.valor ?? 0;
                  const ehPositivo = valor > 0;

                  return (
                    <tr key={index}>
                      <td style={{ whiteSpace: 'nowrap' }}>{formatarData(item.data)}</td>
                      <td>
                        <span className={`table-badge ${ehCredito ? 'badge-credito' : 'badge-envio'}`}>
                          {ehCredito ? (
                            <>
                              <ArrowDownLeft size={13} /> Crédito semestral
                            </>
                          ) : (
                            <>
                              <ArrowUpRight size={13} /> Envio
                            </>
                          )}
                        </span>
                      </td>
                      <td>{item.nomeAluno || '—'}</td>
                      <td>{item.motivo || 'Reconhecimento acadêmico'}</td>
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

