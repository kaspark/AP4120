import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { DatePicker, Form, InputNumber } from 'antd';
import dayjs from 'dayjs';
import { CoffeeOutlined } from '@ant-design/icons';
import { useDataController } from '@helex/core';
import {
  AppCard,
  AppDate,
  AppTag,
  AppTimestamp,
  FieldItem,
  ResourceForm,
  useAppNotification,
  type ResourceFormSection,
} from '@helex/ui';
import { teaApi, type Category, type Tea, type TeaUser } from '../api';

const DateField = ({ value, onChange }: { value?: unknown; onChange?: (next: string | null) => void }) => (
  <DatePicker
    style={{ width: '100%' }}
    value={value ? dayjs(String(value)) : null}
    onChange={(d) => onChange?.(d ? d.format('YYYY-MM-DD') : null)}
  />
);

const QuantityField = ({ value, onChange }: { value?: unknown; onChange?: (next: number | null) => void }) => (
  <InputNumber
    min={0}
    style={{ width: '100%' }}
    value={value == null || value === '' ? null : Number(value)}
    onChange={(n) => onChange?.(n == null ? null : Number(n))}
  />
);

export const TeaDetail = () => {
  const { id } = useParams();
  const teaId = Number(id);
  const navigate = useNavigate();
  const notify = useAppNotification();
  const [form] = Form.useForm<Tea>();
  const dc = useDataController<Tea>({} as Tea);
  const [mode, setMode] = useState<'view' | 'edit'>('view');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const [categories, setCategories] = useState<Category[]>([]);
  const [users, setUsers] = useState<TeaUser[]>([]);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    Promise.all([teaApi.get(teaId), teaApi.categories(), teaApi.users()])
      .then(([tea, categoryList, userList]) => {
        if (cancelled) return;
        dc.load(tea);
        form.setFieldsValue(tea);
        setCategories(categoryList);
        setUsers(userList);
      })
      .catch((e) => {
        notify.error('Could not load the tea', e.message);
        navigate('/teas');
      })
      .finally(() => !cancelled && setLoading(false));
    return () => {
      cancelled = true;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [teaId, notify]);

  const save = async (values: Tea) => {
    setSaving(true);
    try {
      const updated = await teaApi.update(teaId, {
        name: values.name,
        brand: values.brand,
        categoryId: values.categoryId,
        ownerId: values.ownerId,
        purchaseDate: values.purchaseDate,
        expiryDate: values.expiryDate,
        quantity: Number(values.quantity),
        unit: values.unit,
      });
      dc.load(updated);
      form.setFieldsValue(updated);
      setMode('view');
      notify.success('Saved', `${updated.name} · ${updated.brand}`);
    } catch (e) {
      notify.error('Save failed', (e as Error).message);
    } finally {
      setSaving(false);
    }
  };

  const retire = async () => {
    setDeleting(true);
    try {
      await teaApi.retire(teaId);
      notify.success('Retired', dc.current.name);
      navigate('/teas');
    } catch (e) {
      notify.error('Retire failed', (e as Error).message);
      setDeleting(false);
    }
  };

  const sections: ResourceFormSection<Tea>[] = [
    {
      key: 'identity',
      title: 'Tea',
      fields: [
        { name: 'name', label: 'Name', type: 'text', required: true, rules: [{ required: true, max: 255 }] },
        { name: 'brand', label: 'Brand', type: 'text', required: true, rules: [{ required: true, max: 255 }] },
        {
          name: 'categoryId',
          label: 'Classification',
          type: 'select',
          required: true,
          options: categories.map((c) => ({ value: c.id, label: c.name })),
          viewRender: (categoryId) => <AppTag>{categories.find((c) => c.id === categoryId)?.name ?? ''}</AppTag>,
        },
        {
          name: 'ownerId',
          label: 'Owner',
          type: 'select',
          required: true,
          options: users.map((u) => ({ value: u.id, label: u.name })),
          viewRender: (ownerId) => users.find((u) => u.id === ownerId)?.name ?? dc.current.ownerName ?? '—',
        },
      ],
    },
    {
      key: 'stock',
      title: 'Stock',
      fields: [
        {
          name: 'purchaseDate',
          label: 'Purchase date',
          type: 'custom',
          required: true,
          render: ({ value, onChange }) => <DateField value={value} onChange={onChange} />,
          viewRender: (value) => (value ? <AppDate value={String(value)} /> : '—'),
        },
        {
          name: 'expiryDate',
          label: 'Expiry date',
          type: 'custom',
          required: true,
          tooltip: 'Must not be before the purchase date',
          render: ({ value, onChange }) => <DateField value={value} onChange={onChange} />,
          viewRender: (value) => (value ? <AppDate value={String(value)} /> : '—'),
        },
        {
          name: 'quantity',
          label: 'Quantity',
          type: 'custom',
          required: true,
          render: ({ value, onChange }) => <QuantityField value={value} onChange={onChange} />,
        },
        { name: 'unit', label: 'Unit', type: 'text', required: true, rules: [{ required: true, max: 30 }] },
      ],
    },
  ];

  return (
    <ResourceForm<Tea>
      mode={mode}
      entityLabel="Tea"
      icon={<CoffeeOutlined />}
      name={dc.current.name}
      subtitle={dc.current.brand}
      chips={dc.current.categoryName ? <AppTag>{dc.current.categoryName}</AppTag> : undefined}
      loading={loading}
      saving={saving}
      deleting={deleting}
      form={form}
      dataController={dc}
      initialValues={dc.current}
      sections={sections}
      onBack={() => navigate('/teas')}
      backTooltip="Back to the list"
      onEdit={() => setMode('edit')}
      onCancel={() => {
        dc.reset();
        form.setFieldsValue(dc.original ?? dc.current);
        setMode('view');
      }}
      onSave={save}
      onDelete={retire}
      deleteLabel="Retire"
      deleteConfirmText="Retire this tea? It stays in the database with status retired."
      sidebar={
        <AppCard title="Metadata" size="small">
          <FieldItem label="Created at">
            <AppTimestamp value={dc.current.sysCreatedAt} fallback="—" />
          </FieldItem>
          <FieldItem label="Created by">{dc.current.sysCreatedBy ?? '—'}</FieldItem>
          <FieldItem label="Modified at">
            <AppTimestamp value={dc.current.sysModifiedAt} fallback="—" />
          </FieldItem>
          <FieldItem label="Modified by">{dc.current.sysModifiedBy ?? '—'}</FieldItem>
          <FieldItem label="Version">{dc.current.sysVersion ?? '—'}</FieldItem>
        </AppCard>
      }
    />
  );
};
