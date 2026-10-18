import { Button } from '@vaadin/react-components/Button.js';
import { Suspense } from 'react';
import { Outlet } from 'react-router';
import { useAuth } from 'Frontend/auth';

export default function MainLayout() {
  const { state, logout } = useAuth();
  const displayName = state.user?.displayName ?? '';

  return (
    <div className="app-shell">
      <header className="app-header">
        <span className="brand">Diagrammer</span>
        <span className="spacer"></span>
        <span className="display-name">{displayName}</span>
        <Button onClick={() => void logout()}>Log out</Button>
      </header>
      <Suspense>
        <div className="view">
          <Outlet />
        </div>
      </Suspense>
    </div>
  );
}
