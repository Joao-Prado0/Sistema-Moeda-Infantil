import './Select.css';

export default function Select({
  label,
  icon: Icon,
  options = [],
  value,
  onChange,
  required,
  error,
  id,
  name,
  disabled = false,
  placeholder = 'Selecione uma opção',
  className = '',
  ...props
}) {
  const selectId = id || name;

  return (
    <div className={`custom-select-field ${className}`}>
      {label && (
        <label htmlFor={selectId} className="select-label">
          {Icon && <Icon size={16} />}
          <span>{label}</span>
          {required && <span className="req">*</span>}
        </label>
      )}
      <div className="select-wrapper">
        <select
          id={selectId}
          name={name}
          value={value}
          onChange={onChange}
          required={required}
          disabled={disabled}
          className={`select-input ${error ? 'select-error' : ''}`}
          {...props}
        >
          <option value="">{placeholder}</option>
          {options.map((opt) => {
            const val = typeof opt === 'object' ? opt.id : opt;
            const text = typeof opt === 'object' ? opt.nome || opt.label : opt;
            return (
              <option key={val} value={val}>
                {text}
              </option>
            );
          })}
        </select>
      </div>
      {error && <span className="select-error-msg">{error}</span>}
    </div>
  );
}

