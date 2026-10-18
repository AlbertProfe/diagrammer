import type { ViewConfig } from '@vaadin/hilla-file-router/types.js';

export const config: ViewConfig = {
  loginRequired: true,
  title: 'Diagram Orchestrator',
  menu: { title: 'Diagram Orchestrator' },
};

export default function DiagramOrchestratorView() {
  return (
    <main className="orchestrator">
      <h1>Diagram Orchestrator</h1>
      <p>No diagrams yet.</p>
    </main>
  );
}
