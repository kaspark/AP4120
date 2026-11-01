import type { ReactElement } from 'react';
import { Navigate, Route, Routes, useParams } from 'react-router-dom';
import { PermitList } from './pages/PermitList';
import { PermitCreate } from './pages/PermitCreate';
import { PermitDetail } from './pages/PermitDetail';

const LegacyTeaRedirect = () => {
  const { id } = useParams();
  return <Navigate to={id ? `/permits/${id}` : '/permits'} replace />;
};

const componentRoutes = Object.values(
  import.meta.glob<{ routes: ReactElement[] }>('./pages/*/routes.tsx', { eager: true }),
).flatMap((m) => m.routes);

export const App = () => (
  <Routes>
    <Route path="/" element={<Navigate to="/permits" replace />} />
    <Route path="/teas" element={<Navigate to="/permits" replace />} />
    <Route path="/teas/new" element={<Navigate to="/permits/new" replace />} />
    <Route path="/teas/:id" element={<LegacyTeaRedirect />} />
    <Route path="/permits" element={<PermitList />} />
    <Route path="/permits/new" element={<PermitCreate />} />
    <Route path="/permits/:id" element={<PermitDetail />} />
    {componentRoutes}
  </Routes>
);
