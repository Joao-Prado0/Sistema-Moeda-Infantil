export default function FormSkeleton({ campos = 4 }) {
  return (
    <div className="card form-card" aria-busy="true" aria-label="Carregando dados">
      <div className="skeleton skeleton-title" />
      <div className="form-grid">
        {Array.from({ length: campos }, (_, i) => (
          <div key={i}>
            <div className="skeleton skeleton-label" />
            <div className="skeleton skeleton-input" />
          </div>
        ))}
      </div>
    </div>
  );
}
