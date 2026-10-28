import { useState } from 'react';
import { Gift, Info, LoaderCircle, Store, Wallet } from 'lucide-react';
import { vantagemApi } from '../../services/api';
import { useToast } from '../../context/ToastContext';
import { formatarMoedas } from '../../utils/formatters';

export default function ModalConfirmarResgate({ vantagem, alunoId, saldoAtual, onClose, onResgatado }) {
  const toast = useToast();
  const [resgatando, setResgatando] = useState(false);

  if (!vantagem) return null;

  const custo = vantagem.custoEmMoedas ?? 0;
  const saldoApos = (saldoAtual ?? 0) - custo;

  const handleConfirmar = async () => {
    setResgatando(true);
    try {
      const cupom = await vantagemApi.resgatar(vantagem.id, alunoId);
      toast.sucesso(
        `Vantagem "${vantagem.nome}" resgatada com sucesso! Código do cupom: ${cupom.codigo}`
      );
      onResgatado?.(cupom);
      onClose();
    } catch (err) {
      toast.erro(err.message || 'Erro ao resgatar vantagem.');
    } finally {
      setResgatando(false);
    }
  };

  return (
    <div
      role="dialog"
      aria-modal="true"
      style={{
        position: 'fixed',
        inset: 0,
        zIndex: 1000,
        display: 'grid',
        placeItems: 'center',
        padding: '16px',
        background: 'rgba(18, 14, 58, 0.65)',
        backdropFilter: 'blur(6px)',
        animation: 'modal-fade 0.25s ease-out',
      }}
    >
      <div
        className="card"
        style={{
          width: '100%',
          maxWidth: '520px',
          padding: '28px',
          borderRadius: 'var(--radius-xl)',
          background: 'var(--surface)',
          boxShadow: 'var(--shadow-lg)',
          animation: 'modal-pop 0.3s var(--ease-spring)',
        }}
      >
        <h2 style={{ fontSize: '1.4rem', marginBottom: '16px' }}>Confirmar resgate</h2>

        {/* Thumbnail / Foto */}
        <div
          style={{
            height: '180px',
            borderRadius: 'var(--radius)',
            overflow: 'hidden',
            background: 'var(--surface-muted)',
            display: 'grid',
            placeItems: 'center',
            marginBottom: '16px',
          }}
        >
          {vantagem.urlFoto ? (
            <img
              src={vantagem.urlFoto}
              alt={vantagem.nome}
              style={{ width: '100%', height: '100%', objectFit: 'cover' }}
            />
          ) : (
            <Gift size={48} color="var(--brand-500)" />
          )}
        </div>

        {/* Detalhes da Vantagem */}
        <div style={{ marginBottom: '16px' }}>
          <h3 style={{ fontSize: '1.15rem' }}>{vantagem.nome}</h3>
          <p style={{ display: 'flex', alignItems: 'center', gap: '6px', color: 'var(--text-muted)', fontSize: '0.88rem', marginTop: '2px' }}>
            <Store size={14} /> {vantagem.nomeEmpresa}
          </p>
        </div>

        {/* Box Informativo de Saldos (Wireframe 06) */}
        <div
          style={{
            display: 'grid',
            gap: '10px',
            padding: '16px',
            borderRadius: 'var(--radius)',
            background: 'var(--surface-muted)',
            border: '1px solid var(--border)',
            marginBottom: '16px',
            fontSize: '0.92rem',
          }}
        >
          <div style={{ display: 'flex', justifyContent: 'space-between' }}>
            <span style={{ color: 'var(--text-muted)' }}>Custo</span>
            <strong>{formatarMoedas(custo)} moedas</strong>
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between' }}>
            <span style={{ color: 'var(--text-muted)' }}>Saldo atual</span>
            <span>{formatarMoedas(saldoAtual ?? 0)} moedas</span>
          </div>
          <div
            style={{
              display: 'flex',
              justifyContent: 'space-between',
              paddingTop: '8px',
              borderTop: '1px dashed var(--border)',
              fontWeight: 700,
            }}
          >
            <span>Saldo após o resgate</span>
            <span style={{ color: saldoApos < 0 ? 'var(--danger-600)' : 'var(--brand-700)' }}>
              {formatarMoedas(saldoApos)} moedas
            </span>
          </div>
        </div>

        <p
          style={{
            display: 'flex',
            alignItems: 'flex-start',
            gap: '8px',
            fontSize: '0.82rem',
            color: 'var(--text-muted)',
            marginBottom: '20px',
          }}
        >
          <Info size={16} style={{ flexShrink: 0, marginTop: '2px' }} />
          Um e-mail com o cupom e o código de conferência será enviado a você e à empresa parceira.
        </p>

        {/* Botões */}
        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px' }}>
          <button
            type="button"
            className="btn btn-secondary"
            onClick={onClose}
            disabled={resgatando}
          >
            Cancelar
          </button>
          <button
            type="button"
            className="btn btn-primary"
            onClick={handleConfirmar}
            disabled={resgatando || saldoApos < 0}
          >
            {resgatando ? (
              <>
                <LoaderCircle size={18} className="spin" /> Confirmando...
              </>
            ) : (
              'Confirmar resgate'
            )}
          </button>
        </div>
      </div>
    </div>
  );
}

