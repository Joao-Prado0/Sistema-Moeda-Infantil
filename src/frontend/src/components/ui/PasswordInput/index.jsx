import React, { useState } from 'react';
import './PasswordInput.css';
import { Eye, EyeOff, Lock } from 'lucide-react';

export default function PasswordInput({
  label = 'Senha',
  value,
  onChange,
  placeholder = '••••••••',
  required = false,
  error,
  id = 'password-input',
  name = 'senha',
  ...props
}) {
  const [visivel, setVisivel] = useState(false);

  return (
    <div className="custom-password-field">
      {label && (
        <label htmlFor={id} className="password-label">
          <Lock size={16} />
          <span>{label}</span>
          {required && <span className="req">*</span>}
        </label>
      )}
      <div className="password-input-wrapper">
        <input
          id={id}
          name={name}
          type={visivel ? 'text' : 'password'}
          value={value}
          onChange={onChange}
          placeholder={placeholder}
          required={required}
          className={`password-input ${error ? 'input-error' : ''}`}
          {...props}
        />
        <button
          type="button"
          className="password-toggle-btn"
          onClick={() => setVisivel((prev) => !prev)}
          tabIndex={-1}
          aria-label={visivel ? 'Ocultar senha' : 'Exibir senha'}
        >
          {visivel ? <EyeOff size={18} /> : <Eye size={18} />}
        </button>
      </div>
      {error && <span className="password-error-msg">{error}</span>}
    </div>
  );
}

