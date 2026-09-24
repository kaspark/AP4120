import { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { PlusOutlined } from '@ant-design/icons';
import { AppButtonPrimary, AppLink, AppTag, ResourceList, useAppNotification, type ResourceListColumn } from '@helex/ui';
import { teaApi, type Tea } from '../api';

export const TeaList = () => {
  const navigate = useNavigate();
  const notify = useAppNotification();
  const [teas, setTeas] = useState<Tea[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [selected, setSelected] = useState<Tea | null>(null);

  const load = useCallback((text: string) => {
    setLoading(true);
    teaApi
      .list(text || undefined)
      .then((result) => setTeas(result.data))
      .catch((e) => notify.error('Could not load teas', e.message))
      .finally(() => setLoading(false));
  }, [notify]);

  useEffect(() => {
    const t = setTimeout(() => load(search), 250);
    return () => clearTimeout(t);
  }, [search, load]);

  const columns: ResourceListColumn<Tea>[] = [
    {
      key: 'name',
      title: 'Name',
      dataIndex: 'name',
      locked: true,
      render: (name: string, tea: Tea) => (
        <AppLink
          href={`/teas/${tea.id}`}
          onClick={(e) => {
            e.preventDefault();
            e.stopPropagation();
            navigate(`/teas/${tea.id}`);
          }}
        >
          {name}
        </AppLink>
      ),
    },
    { key: 'brand', title: 'Brand', dataIndex: 'brand' },
    {
      key: 'categoryName',
      title: 'Classification',
      dataIndex: 'categoryName',
      render: (name: string) => <AppTag>{name}</AppTag>,
    },
    { key: 'quantity', title: 'Quantity', dataIndex: 'quantity' },
    { key: 'unit', title: 'Unit', dataIndex: 'unit' },
    { key: 'expiryDate', title: 'Expiry', dataIndex: 'expiryDate' },
  ];

  return (
    <ResourceList<Tea>
      title="Tea"
      columns={columns}
      dataSource={teas}
      rowKey="id"
      loading={loading}
      search={{ value: search, onChange: setSearch, placeholder: 'Name or brand…' }}
      actions={
        <AppButtonPrimary icon={<PlusOutlined />} onClick={() => navigate('/teas/new')}>
          Add tea
        </AppButtonPrimary>
      }
      detailView={{
        selectedRecord: selected,
        onSelectedRecordChange: setSelected,
        title: 'Tea',
        render: (tea) => (
          <div style={{ display: 'grid', gap: 8 }}>
            <div><b>{tea.name}</b> · {tea.brand}</div>
            <div>Classification: {tea.categoryName}</div>
            <div>Owner: {tea.ownerName}</div>
            <div>{tea.quantity} {tea.unit}</div>
            <div>Bought {tea.purchaseDate}, expires {tea.expiryDate}</div>
          </div>
        ),
      }}
    />
  );
};
