import { useEffect, useState } from 'react';
import { Coins, Gift, Image, LoaderCircle, Sparkles, Type } from 'lucide-react';
import { empresaApi } from '../../services/api';
import { useToast } from '../../context/ToastContext';

export default function ModalVantagemForm({ empresaId, vantagemEmEdicao, onClose, onSucesso }) {
  const toast = useToast();
  const [salvando, setSalvando] = useState(false);

  const [formData, setFormData] = useState({
    titulo: '',
    descricao: '',
    custoMoedas: '',
    fotoUrl: '',
  });

  useEffect(() => {
    if (vantagemEmEdicao) {
      setFormData({
        titulo: vantagemEmEdicao.titulo || '',
        descricao: vantagemEmEdicao.descricao || '',
        custoMoedas: vantagemEmEdicao.custoMoedas || '',
        fotoUrl: vantagemEmEdicao.fotoUrl || '',
      });
    } else {
      setFormData({
        titulo: '',
        descricao: '',
        custoMoedas: '',
        fotoUrl: '',
      });
    }
  }, [vantagemEmEdicao]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!formData.titulo.trim()) {
      toast.erro('O título da vantagem é obrigatório.');
      return;
    }
    if (!formData.descricao.trim()) {
      toast.erro('A descrição da vantagem é obrigatória.');
      return;
    }
    const custo = parseFloat(formData.custoMoedas);
    if (isNaN(custo) || custo <= 0) {
      toast.erro('O custo em moedas deve ser maior que zero.');
      return;
    }

    setSalvando(true);
    try {
      const payload = {
        titulo: formData.titulo.trim(),
        descricao: formData.descricao.trim(),
        custoMoedas: custo,
        fotoUrl: formData.fotoUrl.trim() || undefined,
      };

      if (vantagemEmEdicao?.id) {
        await empresaApi.editarVantagem(empresaId, vantagemEmEdicao.id, payload);
        toast.sucesso('Vantagem atualizada com sucesso!');
      } else {
        await empresaApi.criarVantagem(empresaId, payload);
        toast.sucesso('Vantagem cadastrada com sucesso!');
      }

      onSucesso?.();
      onClose();
    } catch (err) {
      toast.erro(err.message || 'Erro ao salvar vantagem.');
    } finally {
      setSalvando(false);
    }
  };

  const ehEdicao = Boolean(vantagemEmEdicao?.id);

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
          maxWidth: '560px',
          padding: '28px',
          borderRadius: 'var(--radius-xl)',
          background: 'var(--surface)',
          boxShadow: 'var(--shadow-lg)',
          animation: 'modal-pop 0.3s var(--ease-spring)',
        }}
      >
        <header style={{ marginBottom: '20px' }}>
          <h2 style={{ fontSize: '1.35rem', display: 'flex', alignItems: 'center', gap: '8px' }}>
            <Gift size={22} color="var(--brand-600)" />
            {ehEdicao ? 'Editar vantagem' : 'Cadastrar nova vantagem'}
          </h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.88rem', marginTop: '4px' }}>
            Ofereça descontos ou produtos que os alunos poderão resgatar com moedas.
          </p>
        </header>

        <form onSubmit={handleSubmit} style={{ display: 'grid', gap: '16px' }}>
          {/* Título */}
          <div className="field">
            <label className="field-label" htmlFor="vantagem-titulo">
              <Type size={16} /> Título da vantagem
            </label>
            <div className="input-wrap">
              <input
                id="vantagem-titulo"
                name="titulo"
                className="input"
                placeholder="Ex.: Desconto de 20% no restaurante"
                value={formData.titulo}
                onChange={handleChange}
                disabled={salvando}
                required
              />
            </div>
          </div>

          {/* Descrição */}
          <div className="field">
            <label className="field-label" htmlFor="vantagem-descricao">
              Descrição detalhada
            </label>
            <textarea
              id="vantagem-descricao"
              name="descricao"
              className="input"
              rows={3}
              placeholder="Descreva as condições, regras de uso ou detalhes do produto..."
              value={formData.descricao}
              onChange={handleChange}
              disabled={salvando}
              required
              style={{ resize: 'vertical', minHeight: '80px', padding: '10px 14px' }}
            />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
            {/* Custo em Moedas */}
            <div className="field">
              <label className="field-label" htmlFor="vantagem-custo">
                <Coins size={16} /> Custo (moedas)
              </label>
              <div className="input-wrap">
                <input
                  id="vantagem-custo"
                  name="custoMoedas"
                  type="number"
                  min="1"
                  step="1"
                  className="input"
                  placeholder="Ex.: 200"
                  value={formData.custoMoedas}
                  onChange={handleChange}
                  disabled={salvando}
                  required
                />
              </div>
            </div>

            {/* Foto URL */}
            <div className="field">
              <label className="field-label" htmlFor="vantagem-foto">
                <Image size={16} /> URL da foto
              </label>
              <div className="input-wrap">
                <input
                  id="vantagem-foto"
                  name="fotoUrl"
                  type="url"
                  className="input"
                  placeholder="https://..."
                  value={formData.fotoUrl}
                  onChange={handleChange}
                  disabled={salvando}
                />
              </div>
            </div>
          </div>

          {/* Pré-visualização da Imagem se houver */}
          {formData.fotoUrl && (
            <div
              style={{
                height: '110px',
                borderRadius: 'var(--radius)',
                overflow: 'hidden',
                border: '1px solid var(--border)',
                background: 'var(--surface-muted)',
              }}
            >
              <img
                src={formData.fotoUrl}
                alt="Prévia"
                style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                onError={(e) => {
                  e.target.style.display = 'none';
                }}
              />
            </div>
          )}

          {/* Botões */}
          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '8px' }}>
            <button
              type="button"
              className="btn btn-secondary"
              onClick={onClose}
              disabled={salvando}
            >
              Cancelar
            </button>
            <button
              type="submit"
              className="btn btn-primary"
              disabled={salvando || !formData.titulo.trim() || !formData.descricao.trim() || !formData.custoMoedas}
            >
              {salvando ? (
                <>
                  <LoaderCircle size={18} className="spin" /> Salvando...
                </>
              ) : (
                ehEdicao ? 'Salvar alterações' : 'Cadastrar vantagem'
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

