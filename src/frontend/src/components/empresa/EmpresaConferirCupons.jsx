import { useEffect, useState } from 'react';
import {
  CheckCircle,
  Inbox,
  LoaderCircle,
  QrCode,
  Search,
  TicketCheck,
} from 'lucide-react';
import { empresaApi } from '../../services/api';
import { useToast } from '../../context/ToastContext';

function formatarData(dataIso) {
  if (!dataIso) return '—';
  try {
    const d = new Date(dataIso);
    return d.toLocaleDateString('pt-BR');
  } catch {
    return dataIso;
  }
}

export default function EmpresaConferirCupons({ empresaId }) {
  const toast = useToast();
  const [codigoInput, setCodigoInput] = useState('');
  const [conferindo, setConferindo] = useState(false);
  const [cupons, setCupons] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [ultimoConferido, setUltimoConferido] = useState(null);

  async function carregarCupons() {
    if (!empresaId) return;
    try {
      const lista = await empresaApi.listarCupons(empresaId);
      setCupons(lista || []);
    } catch (err) {
      toast.erro('Não foi possível carregar os cupons: ' + err.message);
    } finally {
      setCarregando(false);
    }
  }

  useEffect(() => {
    carregarCupons();
  }, [empresaId]);

  const handleConferir = async (e) => {
    e.preventDefault();
    const codigoLimpo = codigoInput.trim();
    if (!codigoLimpo) {
      toast.erro('Informe o código do cupom a conferir.');
      return;
    }

    setConferindo(true);
    setUltimoConferido(null);
    try {
      const resultado = await empresaApi.conferirCupom(empresaId, codigoLimpo);
      toast.sucesso(
        `Cupom validado com sucesso! Aluno: ${resultado.nomeAluno} | Vantagem: ${resultado.tituloVantagem}`
      );
      setUltimoConferido(resultado);
      setCodigoInput('');
      await carregarCupons();
    } catch (err) {
      toast.erro(err.message || 'Código de cupom inválido ou já utilizado.');
    } finally {
      setConferindo(false);
    }
  };

  return (
    <div className="tab-panel">
      {/* Área de Validação Rápida (Wireframe 13) */}
      <article className="card reveal" style={{ maxWidth: '640px' }}>
        <header className="card-title-row" style={{ marginBottom: '16px' }}>
          <div>
            <h2 className="card-title">
              <TicketCheck size={18} /> Conferir cupom presencial
            </h2>
            <p className="muted">Insira o código apresentado pelo estudante para validar a troca.</p>
          </div>
        </header>

        <form onSubmit={handleConferir} style={{ display: 'flex', gap: '12px', alignItems: 'flex-start' }}>
          <div className="field" style={{ flex: 1, margin: 0 }}>
            <div className="input-wrap">
              <input
                className="input"
                type="text"
                placeholder="Ex.: A1B2-C3D4 ou código completo"
                value={codigoInput}
                onChange={(e) => setCodigoInput(e.target.value)}
                disabled={conferindo}
                style={{ fontFamily: 'monospace', fontWeight: 600, letterSpacing: '0.04em' }}
                required
              />
            </div>
          </div>

          <button
            type="submit"
            className="btn btn-primary"
            disabled={conferindo || !codigoInput.trim()}
            style={{ height: '48px', padding: '0 24px' }}
          >
            {conferindo ? (
              <>
                <LoaderCircle size={18} className="spin" /> Conferindo...
              </>
            ) : (
              'Conferir'
            )}
          </button>
        </form>

        {ultimoConferido && (
          <div
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '12px',
              marginTop: '16px',
              padding: '14px 16px',
              borderRadius: 'var(--radius)',
              background: '#ecfdf5',
              border: '1px solid #a7f3d0',
              color: '#065f46',
              fontSize: '0.9rem',
            }}
          >
            <CheckCircle size={20} color="#047857" style={{ flexShrink: 0 }} />
            <div>
              <strong>Cupom validado com sucesso!</strong>
              <div style={{ marginTop: '2px' }}>
                Estudante: <strong>{ultimoConferido.nomeAluno}</strong> · Vantagem:{' '}
                <strong>{ultimoConferido.tituloVantagem}</strong>
              </div>
            </div>
          </div>
        )}
      </article>

      {/* Resgates Recebidos (Wireframe 13) */}
      <article className="card reveal" style={{ marginTop: '12px' }}>
        <header className="card-title-row">
          <div>
            <h2 className="card-title">Resgates recebidos</h2>
            <p className="muted">Histórico de cupons resgatados para as suas vantagens.</p>
          </div>
        </header>

        {carregando ? (
          <div style={{ display: 'grid', placeItems: 'center', padding: '50px 0' }}>
            <LoaderCircle size={32} className="spin" color="var(--brand-600)" />
            <p style={{ marginTop: '10px', color: 'var(--text-muted)' }}>Carregando histórico de resgates...</p>
          </div>
        ) : cupons.length === 0 ? (
          <div className="empty-state">
            <div className="empty-illustration">
              <Inbox size={34} />
            </div>
            <h3>Nenhum resgate recebido ainda</h3>
            <p>Quando um aluno trocar moedas por uma das suas vantagens, o cupom aparecerá aqui.</p>
          </div>
        ) : (
          <div className="data-table-container">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Código</th>
                  <th>Aluno</th>
                  <th>Vantagem</th>
                  <th>Data</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {cupons.map((c) => {
                  const ehUtilizado = c.status === 'UTILIZADO';

                  return (
                    <tr key={c.id}>
                      <td>
                        <code
                          style={{
                            fontFamily: 'monospace',
                            fontWeight: 700,
                            fontSize: '0.88rem',
                            padding: '3px 8px',
                            borderRadius: '6px',
                            background: 'var(--surface-muted)',
                            border: '1px solid var(--border)',
                          }}
                        >
                          {c.codigo}
                        </code>
                      </td>
                      <td style={{ fontWeight: 600 }}>{c.nomeAluno}</td>
                      <td>{c.tituloVantagem}</td>
                      <td>{formatarData(c.dataResgate)}</td>
                      <td>
                        <span className={`table-badge ${ehUtilizado ? 'badge-utilizado' : 'badge-pendente'}`}>
                          {ehUtilizado ? (
                            <>
                              <CheckCircle size={13} /> Utilizado
                            </>
                          ) : (
                            'Pendente'
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

