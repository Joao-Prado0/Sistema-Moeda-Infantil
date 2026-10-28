import { useEffect, useState } from 'react';
import { Gift, LoaderCircle, Sparkles, Store, Wallet } from 'lucide-react';
import { vantagemApi } from '../../services/api';
import { useToast } from '../../context/ToastContext';
import { formatarMoedas } from '../../utils/formatters';
import ModalConfirmarResgate from './ModalConfirmarResgate';

export default function AlunoCatalogoVantagens({ alunoId, saldoAtual, onVantagemResgatada }) {
  const toast = useToast();
  const [vantagens, setVantagens] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [vantagemSelecionada, setVantagemSelecionada] = useState(null);

  async function carregarVantagens() {
    try {
      const lista = await vantagemApi.listar();
      setVantagens(lista || []);
    } catch (err) {
      toast.erro('Não foi possível carregar o catálogo de vantagens: ' + err.message);
    } finally {
      setCarregando(false);
    }
  }

  useEffect(() => {
    carregarVantagens();
  }, []);

  return (
    <div className="tab-panel">
      <header className="page-heading reveal" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '16px' }}>
        <div>
          <h2>Vantagens disponíveis</h2>
          <p>Troque suas moedas por produtos e benefícios exclusivos em parceiros.</p>
        </div>
        <div className="chip-glass" style={{ color: 'var(--text)', background: 'var(--brand-50)' }}>
          <Wallet size={14} color="var(--brand-600)" />
          <span>
            Seu saldo: <strong>{formatarMoedas(saldoAtual ?? 0)}</strong> moedas
          </span>
        </div>
      </header>

      {carregando ? (
        <div style={{ display: 'grid', placeItems: 'center', padding: '60px 0' }}>
          <LoaderCircle size={36} className="spin" color="var(--brand-600)" />
          <p style={{ marginTop: '12px', color: 'var(--text-muted)' }}>Carregando catálogo...</p>
        </div>
      ) : vantagens.length === 0 ? (
        <div className="card empty-state">
          <div className="empty-illustration">
            <Gift size={34} />
          </div>
          <h3>Nenhuma vantagem cadastrada no momento</h3>
          <p>Novas oportunidades de troca por empresas parceiras estarão disponíveis em breve.</p>
        </div>
      ) : (
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))',
            gap: '24px',
          }}
        >
          {vantagens.map((v, index) => {
            const custo = v.custoEmMoedas ?? 0;
            const podeResgatar = (saldoAtual ?? 0) >= custo;

            return (
              <article
                key={v.id}
                className="card reveal"
                style={{
                  '--i': index + 1,
                  display: 'flex',
                  flexDirection: 'column',
                  padding: '20px',
                  borderRadius: 'var(--radius-lg)',
                  transition: 'transform 0.25s var(--ease-spring), box-shadow 0.25s',
                }}
              >
                {/* Imagem / Foto do Produto */}
                <div
                  style={{
                    height: '160px',
                    borderRadius: 'var(--radius)',
                    overflow: 'hidden',
                    background: 'var(--surface-muted)',
                    display: 'grid',
                    placeItems: 'center',
                    marginBottom: '14px',
                  }}
                >
                  {v.urlFoto ? (
                    <img
                      src={v.urlFoto}
                      alt={v.nome}
                      style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                      loading="lazy"
                    />
                  ) : (
                    <Gift size={40} color="var(--brand-500)" />
                  )}
                </div>

                <div style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
                  <h3 style={{ fontSize: '1.05rem', fontWeight: 700, lineHeight: 1.3 }}>{v.nome}</h3>
                  <span
                    style={{
                      display: 'inline-flex',
                      alignItems: 'center',
                      gap: '5px',
                      color: 'var(--text-muted)',
                      fontSize: '0.82rem',
                      marginTop: '4px',
                    }}
                  >
                    <Store size={13} /> {v.nomeEmpresa}
                  </span>

                  {v.descricao && (
                    <p
                      style={{
                        color: 'var(--text-muted)',
                        fontSize: '0.85rem',
                        marginTop: '8px',
                        lineHeight: 1.4,
                        display: '-webkit-box',
                        WebkitLineClamp: 2,
                        WebkitBoxOrient: 'vertical',
                        overflow: 'hidden',
                      }}
                    >
                      {v.descricao}
                    </p>
                  )}
                </div>

                <div
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    marginTop: '16px',
                    paddingTop: '12px',
                    borderTop: '1px solid var(--border)',
                  }}
                >
                  <div>
                    <strong style={{ fontSize: '1.15rem', color: 'var(--gold-700)' }}>
                      {formatarMoedas(custo)}
                    </strong>
                    <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginLeft: '4px' }}>
                      moedas
                    </span>
                  </div>

                  <button
                    type="button"
                    className="btn btn-primary btn-sm"
                    onClick={() => setVantagemSelecionada(v)}
                    disabled={!podeResgatar}
                    title={!podeResgatar ? 'Saldo insuficiente para resgatar esta vantagem' : 'Resgatar vantagem'}
                  >
                    {podeResgatar ? 'Resgatar' : 'Saldo insuficiente'}
                  </button>
                </div>
              </article>
            );
          })}
        </div>
      )}

      {vantagemSelecionada && (
        <ModalConfirmarResgate
          vantagem={vantagemSelecionada}
          alunoId={alunoId}
          saldoAtual={saldoAtual}
          onClose={() => setVantagemSelecionada(null)}
          onResgatado={(cupom) => {
            onVantagemResgatada?.(cupom);
            carregarVantagens();
          }}
        />
      )}
    </div>
  );
}

