import { RouterConfigurationBuilder } from '@vaadin/hilla-file-router/runtime.js';
import fileRoutes from 'Frontend/generated/file-routes.js';

export const { router, routes } = new RouterConfigurationBuilder()
  .withFileRoutes(fileRoutes)
  .protect('/login')
  .build();
