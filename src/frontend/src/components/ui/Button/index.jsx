import './Button.css';

const Button = ({
  type = 'button',
  onClick,
  variant = 'primary',
  size = 'md',
  fullWidth = false,
  full = false,
  disabled = false,
  children,
  className = '',
  ...props
}) => {
  const isFull = fullWidth || full;
  const buttonClasses = [
    'custom-btn',
    `btn-${variant}`,
    size !== 'md' && `btn-${size}`,
    isFull && 'btn-full',
    className,
  ]
    .filter(Boolean)
    .join(' ');

  return (
    <button
      type={type}
      onClick={onClick}
      disabled={disabled}
      className={buttonClasses}
      {...props}
    >
      {children}
    </button>
  );
};

export default Button;
