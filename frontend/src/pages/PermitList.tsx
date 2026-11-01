import { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { PlusOutlined } from '@ant-design/icons';
import { AppButtonPrimary, AppLink, AppTag, ResourceList, useAppNotification, type ResourceListColumn } from '@helex/ui';
import { permitApi, type Permit } from '../api';

export const PermitList = () => {
  const navigate = useNavigate();
  const notify = useAppNotification();
  const [permits, setPermits] = useState<Permit[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [selected, setSelected] = useState<Permit | null>(null);

  const load = useCallback((text: string) => {
    setLoading(true);
    permitApi
      .list(text || undefined)
      .then((result) => setPermits(result.data))
      .catch((e) => notify.error('Could not load permits', e.message))
      .finally(() => setLoading(false));
  }, [notify]);

  useEffect(() => {
    const t = setTimeout(() => load(search), 250);
    return () => clearTimeout(t);
  }, [search, load]);

  const columns: ResourceListColumn<Permit>[] = [
    {
      key: 'number',
      title: 'No.',
      locked: true,
      render: (_value: unknown, permit: Permit) => `${permits.findIndex((row) => row.id === permit.id) + 1}.`,
    },
    {
      key: 'holderName',
      title: 'Holder',
      dataIndex: 'holderName',
      locked: true,
      render: (name: string, permit: Permit) => (
        <AppLink
          href={`/permits/${permit.id}`}
          onClick={(e) => {
            e.preventDefault();
            e.stopPropagation();
            navigate(`/permits/${permit.id}`);
          }}
        >
          {name}
        </AppLink>
      ),
    },
    {
      key: 'permitTypeName',
      title: 'Type',
      dataIndex: 'permitTypeName',
      render: (name: string) => <AppTag>{name}</AppTag>,
    },
    { key: 'startDate', title: 'Start', dataIndex: 'startDate' },
    { key: 'endDate', title: 'End', dataIndex: 'endDate' },
    { key: 'grantedByName', title: 'Granted by', dataIndex: 'grantedByName' },
    { key: 'description', title: 'Why', dataIndex: 'description' },
  ];

  return (
    <ResourceList<Permit>
      title="Permits"
      columns={columns}
      dataSource={permits}
      rowKey="id"
      loading={loading}
      search={{ value: search, onChange: setSearch, placeholder: 'Holder, granter, or reason…' }}
      actions={
        <AppButtonPrimary icon={<PlusOutlined />} onClick={() => navigate('/permits/new')}>
          Add permit
        </AppButtonPrimary>
      }
      detailView={{
        selectedRecord: selected,
        onSelectedRecordChange: setSelected,
        title: 'Permit',
        render: (permit) => (
          <div style={{ display: 'grid', gap: 8 }}>
            <div><b>{permit.holderName}</b> · {permit.permitTypeName}</div>
            <div>Granted by {permit.grantedByName}</div>
            <div>{permit.startDate} – {permit.endDate}</div>
            <div>{permit.description}</div>
          </div>
        ),
      }}
    />
  );
};
