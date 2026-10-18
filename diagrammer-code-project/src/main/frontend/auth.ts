import { configureAuth } from '@vaadin/hilla-react-auth';
import { CurrentUserEndpoint } from 'Frontend/generated/endpoints';

const auth = configureAuth(CurrentUserEndpoint.getCurrentUser, {
  getRoles: (user) => user.roles.map((role) => role ?? ''),
});

export const useAuth = auth.useAuth;
export const AuthProvider = auth.AuthProvider;
