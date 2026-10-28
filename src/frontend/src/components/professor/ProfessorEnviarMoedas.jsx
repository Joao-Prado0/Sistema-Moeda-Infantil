import { useEffect, useState } from 'react';
import {
  Coins,
  GraduationCap,
  Info,
  LoaderCircle,
  MessageSquare,
  Send,
  UserCheck,
  Wallet,
} from 'lucide-react';
import { alunoApi, professorApi } from '../../services/api';
import { useToast } from '../../context/ToastContext';
import { formatarMoedas } from '../../utils/formatters';

export default function ProfessorEnviarMoedas({ professorId, saldoDisponivel, onSucesso, onCancelar }) {
  const toast = useToast();
  const [alunos, setAlunos] = useState([]);
  const [carregandoAlunos, setCarregandoAlunos] = useState(true);
  const [enviando, setEnviando] = useState(false);

  const [alunoId, setAlunoId] = useState('');
  const [quantidade, setQuantidade] = useState('');
  const [motivo, setMotivo] = useState('');

  useEffect(() => {
    async function carregarListaAlunos() {
      try {
        const lista = await alunoApi.listar();
        setAlunos(lista || []);
      } catch (err) {
        toast.erro('Não foi possível carregar a lista de alunos: ' + err.message);
      } finally {
        setCarregandoAlunos(false);
      }
    }
    carregarListaAlunos();
  }, [toast]);

  const valorNumerico = parseFloat(quantidade) || 0;
  const saldoInsuficiente = valorNumerico > (saldoDisponivel ?? 0);

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!alunoId) {
      toast.erro('Selecione o aluno destinatário.');
      return;
    }
    if (valorNumerico <= 0) {
      toast.erro('A quantidade de moedas deve ser maior que zero.');
      return;
    }
    if (saldoInsuficiente) {
      toast.erro(`Saldo insuficiente! Seu saldo é de ${formatarMoedas(saldoDisponivel)} moedas.`);
      return;
    }
    if (!motivo.trim()) {
      toast.erro('O motivo do reconhecimento é obrigatório.');
      return;
    }

    setEnviando(true);
    try {
      const resp = await professorApi.enviarMoedas(professorId, {
        alunoId: Number(alunoId),
        quantidade: valorNumerico,
        motivo: motivo.trim(),
      });

      toast.sucesso(
        `Envio de ${formatarMoedas(valorNumerico)} moedas para ${resp.nomeAluno || 'o aluno'} realizado com sucesso!`
      );
      setQuantidade('');
      setMotivo('');
      setAlunoId('');
      onSucesso?.();
    } catch (err) {
      toast.erro(err.message || 'Erro ao enviar moedas.');
    } finally {
      setEnviando(false);
    }
  };

  return (
    <div className="tab-panel">
      <article className="card reveal" style={{ maxWidth: '680px', margin: '0 auto' }}>
        <header className="card-title-row" style={{ marginBottom: '24px' }}>
          <div>
            <h2 className="card-title">
              <Send size={18} /> Enviar moedas
            </h2>
            <p className="muted">Reconheça a dedicação e bom comportamento dos alunos.</p>
          </div>
          <div className="chip-glass" style={{ color: 'var(--text)', background: 'var(--brand-50)' }}>
            <Wallet size={14} color="var(--brand-600)" />
            <span>
              Saldo: <strong>{formatarMoedas(saldoDisponivel ?? 0)}</strong> moedas
            </span>
          </div>
        </header>

        <form onSubmit={handleSubmit} style={{ display: 'grid', gap: '20px' }}>
          {/* Campo Aluno */}
          <div className="field">
            <label className="field-label" htmlFor="select-aluno">
              <GraduationCap size={16} /> Aluno destinatário
            </label>
            <div className="input-wrap">
              <select
                id="select-aluno"
                className="input select-custom"
                value={alunoId}
                onChange={(e) => setAlunoId(e.target.value)}
                disabled={carregandoAlunos || enviando}
                required
              >
                <option value="">
                  {carregandoAlunos ? 'Carregando lista de alunos...' : 'Selecione o aluno ▾'}
                </option>
                {alunos.map((a) => (
                  <option key={a.id} value={a.id}>
                    {a.nome} — {a.cursoNome || 'Curso não informado'} ({a.instituicaoNome || 'PUC Minas'})
                  </option>
                ))}
              </select>
            </div>
          </div>

          {/* Campo Quantidade */}
          <div className="field">
            <label className="field-label" htmlFor="input-quantidade">
              <Coins size={16} /> Quantidade de moedas
            </label>
            <div className="input-wrap">
              <input
                id="input-quantidade"
                className={`input ${saldoInsuficiente ? 'input-error' : ''}`}
                type="number"
                min="1"
                step="1"
                placeholder="Ex.: 100"
                value={quantidade}
                onChange={(e) => setQuantidade(e.target.value)}
                disabled={enviando}
                required
              />
            </div>
            {saldoInsuficiente && (
              <span className="field-error" style={{ color: 'var(--danger-600)', fontSize: '0.8rem', marginTop: '4px' }}>
                A quantidade excede seu saldo disponível ({formatarMoedas(saldoDisponivel ?? 0)} moedas).
              </span>
            )}
          </div>

          {/* Campo Motivo */}
          <div className="field">
            <label className="field-label" htmlFor="textarea-motivo">
              <MessageSquare size={16} /> Motivo do reconhecimento (obrigatório)
            </label>
            <textarea
              id="textarea-motivo"
              className="input"
              rows={4}
              placeholder="Descreva por que o aluno está sendo reconhecido (ex.: Excelente participação nas discussões em sala)..."
              value={motivo}
              onChange={(e) => setMotivo(e.target.value)}
              disabled={enviando}
              required
              style={{ resize: 'vertical', minHeight: '96px', padding: '12px 14px' }}
            />
          </div>

          {/* Aviso informativo */}
          <div
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '10px',
              padding: '12px 16px',
              borderRadius: 'var(--radius)',
              background: 'var(--brand-50)',
              color: 'var(--brand-700)',
              fontSize: '0.85rem',
            }}
          >
            <Info size={16} />
            <span>O aluno será notificado por e-mail automaticamente ao receber as moedas.</span>
          </div>

          {/* Botões de Ação */}
          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '10px' }}>
            {onCancelar && (
              <button
                type="button"
                className="btn btn-secondary"
                onClick={onCancelar}
                disabled={enviando}
              >
                Cancelar
              </button>
            )}
            <button
              type="submit"
              className="btn btn-primary"
              disabled={enviando || saldoInsuficiente || !alunoId || !quantidade || !motivo.trim()}
            >
              {enviando ? (
                <>
                  <LoaderCircle size={18} className="spin" /> Enviando...
                </>
              ) : (
                <>
                  <Send size={16} /> Enviar moedas
                </>
              )}
            </button>
          </div>
        </form>
      </article>
    </div>
  );
}

