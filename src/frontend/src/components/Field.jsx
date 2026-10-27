import { useId, useState } from 'react';
import { ChevronDown, CircleAlert, CircleCheck, Eye, EyeOff } from 'lucide-react';
import { forcaSenha } from '../utils/formatters';

export function Field({ label, icon: Icon, hint, className = '', children, ...props }) {
  const autoId = useId();
  const id = props.id || autoId;

  return (
    <div className={`field ${className}`}>
      <label htmlFor={id}>{label}</label>
      <div className="input-wrap">
        {Icon && <Icon className="input-icon" size={18} aria-hidden="true" />}
        <input {...props} id={id} className={Icon ? 'has-icon' : undefined} />
        {children}
      </div>
      {hint && <small className="field-hint">{hint}</small>}
    </div>
  );
}

export function SelectField({ label, icon: Icon, options, className = '', ...props }) {
  const autoId = useId();
  const id = props.id || autoId;

  return (
    <div className={`field ${className}`}>
      <label htmlFor={id}>{label}</label>
      <div className="input-wrap">
        {Icon && <Icon className="input-icon" size={18} aria-hidden="true" />}
        <select {...props} id={id} className={Icon ? 'has-icon' : undefined}>
          {options.length === 0 && <option value="">Carregando...</option>}
          {options.map((opcao) => (
            <option key={opcao.id} value={opcao.id}>
              {opcao.nome}
            </option>
          ))}
        </select>
        <ChevronDown className="select-chevron" size={18} aria-hidden="true" />
      </div>
    </div>
  );
}

export function PasswordField(props) {
  const [visivel, setVisivel] = useState(false);

  return (
    <Field {...props} type={visivel ? 'text' : 'password'}>
      <button
        type="button"
        className="input-action"
        onClick={() => setVisivel((atual) => !atual)}
        aria-label={visivel ? 'Ocultar senha' : 'Mostrar senha'}
      >
        {visivel ? <EyeOff size={18} /> : <Eye size={18} />}
      </button>
    </Field>
  );
}

const NIVEIS_SENHA = ['', 'fraca', 'média', 'boa', 'forte'];

export function PasswordStrength({ senha }) {
  if (!senha) return null;
  const nivel = forcaSenha(senha);

  return (
    <div className={`strength strength-${nivel}`}>
      <div className="strength-bars" aria-hidden="true">
        {[1, 2, 3, 4].map((n) => (
          <span key={n} className={n <= nivel ? 'is-on' : undefined} />
        ))}
      </div>
      <span>Senha {NIVEIS_SENHA[nivel]}</span>
    </div>
  );
}

export function PasswordMatch({ senha, confirmacao }) {
  if (!confirmacao) return null;
  const coincidem = senha === confirmacao;

  return (
    <p className={`match-hint ${coincidem ? 'is-ok' : 'is-pending'}`}>
      {coincidem ? <CircleCheck size={16} /> : <CircleAlert size={16} />}
      {coincidem ? 'As senhas coincidem' : 'As senhas ainda não coincidem'}
    </p>
  );
}
