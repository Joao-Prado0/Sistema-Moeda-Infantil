import { useEffect, useId } from 'react';
import { createPortal } from 'react-dom';
import { LoaderCircle, TriangleAlert } from 'lucide-react';

export default function ConfirmDialog({
  aberto,
  titulo,
  descricao,
  textoConfirmar,
  textoCarregando = 'Aguarde...',
  carregando = false,
  onConfirmar,
  onCancelar,
}) {
  const tituloId = useId();
  const descricaoId = useId();

  useEffect(() => {
    if (!aberto) return undefined;

    const handleKeyDown = (e) => {
      if (e.key === 'Escape' && !carregando) onCancelar();
    };

    document.addEventListener('keydown', handleKeyDown);
    document.body.style.overflow = 'hidden';
    return () => {
      document.removeEventListener('keydown', handleKeyDown);
      document.body.style.overflow = '';
    };
  }, [aberto, carregando, onCancelar]);

  if (!aberto) return null;

  return createPortal(
    <div className="modal-backdrop" onClick={() => !carregando && onCancelar()}>
      <div
        className="modal"
        role="alertdialog"
        aria-modal="true"
        aria-labelledby={tituloId}
        aria-describedby={descricaoId}
        onClick={(e) => e.stopPropagation()}
      >
        <div className="modal-icon">
          <TriangleAlert size={26} />
        </div>
        <h3 id={tituloId}>{titulo}</h3>
        <p id={descricaoId}>{descricao}</p>

        <div className="modal-actions">
          <button type="button" className="btn btn-ghost" onClick={onCancelar} disabled={carregando} autoFocus>
            Cancelar
          </button>
          <button type="button" className="btn btn-danger" onClick={onConfirmar} disabled={carregando}>
            {carregando ? (
              <>
                <LoaderCircle size={18} className="spin" /> {textoCarregando}
              </>
            ) : (
              textoConfirmar
            )}
          </button>
        </div>
      </div>
    </div>,
    document.body
  );
}
