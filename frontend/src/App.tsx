import type { ReactElement } from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';
import { PermitList } from './pages/PermitList';
import { PermitCreate } from './pages/PermitCreate';
import { PermitDetail } from './pages/PermitDetail';

const componentRoutes = Object.values(
  import.meta.glob<{ routes: ReactElement[] }>('./pages/*/routes.tsx', { eager: true }),
).flatMap((m) => m.routes);

export const App = () => (
  <Routes>
    <Route path="/" element={<Navigate to="/permits" replace />} />
    <Route path="/permits" element={<PermitList />} />
    <Route path="/permits/new" element={<PermitCreate />} />
    <Route path="/permits/:id" element={<PermitDetail />} />
    {componentRoutes}
  </Routes>
);
