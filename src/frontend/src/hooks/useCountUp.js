import { useEffect, useState } from 'react';

// Anima um número de 0 até o valor informado (respeita "reduzir movimento")
export default function useCountUp(valor, duracao = 1200) {
  const [atual, setAtual] = useState(0);

  useEffect(() => {
    const alvo = Number(valor) || 0;
    const reduzirMovimento = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    const total = reduzirMovimento ? 0 : duracao;
    const inicio = performance.now();
    let frame;

    const tick = (agora) => {
      const progresso = total === 0 ? 1 : Math.min((agora - inicio) / total, 1);
      const suavizado = 1 - Math.pow(1 - progresso, 3);
      setAtual(progresso < 1 ? Math.round(alvo * suavizado) : alvo);
      if (progresso < 1) frame = requestAnimationFrame(tick);
    };

    frame = requestAnimationFrame(tick);
    return () => cancelAnimationFrame(frame);
  }, [valor, duracao]);

  return atual;
}
