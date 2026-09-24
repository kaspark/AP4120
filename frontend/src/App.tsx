import type { ReactElement } from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';
import { TeaList } from './pages/TeaList';
import { TeaCreate } from './pages/TeaCreate';
import { TeaDetail } from './pages/TeaDetail';

const componentRoutes = Object.values(
  import.meta.glob<{ routes: ReactElement[] }>('./pages/*/routes.tsx', { eager: true }),
).flatMap((m) => m.routes);

export const App = () => (
  <Routes>
    <Route path="/" element={<Navigate to="/teas" replace />} />
    <Route path="/teas" element={<TeaList />} />
    <Route path="/teas/new" element={<TeaCreate />} />
    <Route path="/teas/:id" element={<TeaDetail />} />
    {componentRoutes}
  </Routes>
);
