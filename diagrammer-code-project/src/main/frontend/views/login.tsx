import type { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { useSignal } from '@vaadin/hilla-react-signals';
import { LoginOverlay } from '@vaadin/react-components/LoginOverlay.js';
import { useAuth } from 'Frontend/auth';

export const config: ViewConfig = {
  skipLayouts: true,
  menu: { exclude: true },
  title: 'Log in',
};

export default function LoginView() {
  const { login } = useAuth();
  const hasError = useSignal(false);

  return (
    <LoginOverlay
      opened
      error={hasError.value}
      noForgotPassword
      title="Diagrammer"
      description="Log in to your diagrams"
      onErrorChanged={(event) => {
        hasError.value = event.detail.value;
      }}
      onLogin={async ({ detail: { username, password } }) => {
        const { error } = await login(username, password);
        hasError.value = Boolean(error);
      }}
    />
  );
}
