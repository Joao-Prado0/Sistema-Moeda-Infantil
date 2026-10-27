export default function DashboardShell({ titulo, itens, ativo, onSelecionar, children }) {
  return (
    <div className="dashboard">
      <aside className="sidebar reveal">
        <span className="sidebar-title">{titulo}</span>
        <nav className="sidebar-nav" aria-label={titulo}>
          {itens.map((item) => {
            const Icone = item.icone;
            const selecionado = ativo === item.id;
            return (
              <button
                key={item.id}
                type="button"
                className={`nav-item${selecionado ? ' active' : ''}`}
                onClick={() => onSelecionar?.(item.id)}
                disabled={item.emBreve}
                aria-current={selecionado ? 'page' : undefined}
              >
                <Icone size={18} />
                <span>{item.rotulo}</span>
                {item.emBreve && <span className="soon-badge">Em breve</span>}
              </button>
            );
          })}
        </nav>
      </aside>

      <section className="dashboard-content">{children}</section>
    </div>
  );
}
