import { useCallback, useMemo, useRef, useState } from 'react';
import { CircleAlert, CircleCheck, X } from 'lucide-react';
import { ToastContext } from '../context/ToastContext';

const DURACAO = 4500;
const DURACAO_SAIDA = 250;

export default function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([]);
  const proximoId = useRef(0);

  const fechar = useCallback((id) => {
    setToasts((atuais) => atuais.map((t) => (t.id === id ? { ...t, saindo: true } : t)));
    setTimeout(() => {
      setToasts((atuais) => atuais.filter((t) => t.id !== id));
    }, DURACAO_SAIDA);
  }, []);

  const mostrar = useCallback(
    (tipo, texto) => {
      proximoId.current += 1;
      const id = proximoId.current;
      setToasts((atuais) => [...atuais, { id, tipo, texto }]);
      setTimeout(() => fechar(id), DURACAO);
    },
    [fechar]
  );

  const toast = useMemo(
    () => ({
      sucesso: (texto) => mostrar('sucesso', texto),
      erro: (texto) => mostrar('erro', texto),
    }),
    [mostrar]
  );

  return (
    <ToastContext.Provider value={toast}>
      {children}
      <div className="toast-region" aria-live="polite">
        {toasts.map((t) => (
          <div
            key={t.id}
            className={`toast toast-${t.tipo}${t.saindo ? ' is-leaving' : ''}`}
            role={t.tipo === 'erro' ? 'alert' : 'status'}
          >
            {t.tipo === 'sucesso' ? (
              <CircleCheck className="toast-icon" size={20} />
            ) : (
              <CircleAlert className="toast-icon" size={20} />
            )}
            <p>{t.texto}</p>
            <button
              type="button"
              className="toast-close"
              onClick={() => fechar(t.id)}
              aria-label="Fechar notificação"
            >
              <X size={16} />
            </button>
            <span className="toast-progress" style={{ animationDuration: `${DURACAO}ms` }} />
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
}
