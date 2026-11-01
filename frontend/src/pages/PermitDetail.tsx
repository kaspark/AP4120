import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { DatePicker, Form, Input } from 'antd';
import dayjs from 'dayjs';
import { CarOutlined } from '@ant-design/icons';
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
import { permitApi, type Permit, type PermitType, type Person } from '../api';

const DateField = ({ value, onChange }: { value?: unknown; onChange?: (next: string | null) => void }) => (
  <DatePicker
    style={{ width: '100%' }}
    value={value ? dayjs(String(value)) : null}
    onChange={(d) => onChange?.(d ? d.format('YYYY-MM-DD') : null)}
  />
);

const DescriptionField = ({ value, onChange }: { value?: unknown; onChange?: (next: string) => void }) => (
  <Input.TextArea
    rows={3}
    maxLength={200}
    value={value == null ? '' : String(value)}
    onChange={(e) => onChange?.(e.target.value)}
  />
);

export const PermitDetail = () => {
  const { id } = useParams();
  const permitId = Number(id);
  const navigate = useNavigate();
  const notify = useAppNotification();
  const [form] = Form.useForm<Permit>();
  const dc = useDataController<Permit>({} as Permit);
  const [mode, setMode] = useState<'view' | 'edit'>('view');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const [types, setTypes] = useState<PermitType[]>([]);
  const [persons, setPersons] = useState<Person[]>([]);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    Promise.all([permitApi.get(permitId), permitApi.permitTypes(), permitApi.persons()])
      .then(([permit, typeList, personList]) => {
        if (cancelled) return;
        dc.load(permit);
        form.setFieldsValue(permit);
        setTypes(typeList);
        setPersons(personList);
      })
      .catch((e) => {
        notify.error('Could not load the permit', e.message);
        navigate('/permits');
      })
      .finally(() => !cancelled && setLoading(false));
    return () => {
      cancelled = true;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [permitId, notify]);

  const save = async (values: Permit) => {
    setSaving(true);
    try {
      const updated = await permitApi.update(permitId, {
        permitTypeId: values.permitTypeId,
        holderId: values.holderId,
        grantedById: values.grantedById,
        startDate: values.startDate,
        endDate: values.endDate,
        description: values.description,
      });
      dc.load(updated);
      form.setFieldsValue(updated);
      setMode('view');
      notify.success('Saved', updated.holderName ?? 'Permit');
    } catch (e) {
      notify.error('Save failed', (e as Error).message);
    } finally {
      setSaving(false);
    }
  };

  const retire = async () => {
    setDeleting(true);
    try {
      await permitApi.retire(permitId);
      notify.success('Retired', dc.current.holderName ?? 'Permit');
      navigate('/permits');
    } catch (e) {
      notify.error('Retire failed', (e as Error).message);
      setDeleting(false);
    }
  };

  const personOptions = persons.map((p) => ({ value: p.id, label: p.name }));

  const sections: ResourceFormSection<Permit>[] = [
    {
      key: 'who',
      title: 'Permit',
      fields: [
        {
          name: 'holderId',
          label: 'Holder',
          type: 'select',
          required: true,
          options: personOptions,
          viewRender: (holderId) => persons.find((p) => p.id === holderId)?.name ?? dc.current.holderName ?? '—',
        },
        {
          name: 'permitTypeId',
          label: 'Permit type',
          type: 'select',
          required: true,
          options: types.map((t) => ({ value: t.id, label: t.name })),
          viewRender: (permitTypeId) => <AppTag>{types.find((t) => t.id === permitTypeId)?.name ?? ''}</AppTag>,
        },
        {
          name: 'grantedById',
          label: 'Granted by',
          type: 'select',
          required: true,
          options: personOptions,
          viewRender: (grantedById) => persons.find((p) => p.id === grantedById)?.name ?? dc.current.grantedByName ?? '—',
        },
      ],
    },
    {
      key: 'access',
      title: 'Access',
      fields: [
        {
          name: 'startDate',
          label: 'Start date',
          type: 'custom',
          required: true,
          render: ({ value, onChange }) => <DateField value={value} onChange={onChange} />,
          viewRender: (value) => (value ? <AppDate value={String(value)} /> : '—'),
        },
        {
          name: 'endDate',
          label: 'End date',
          type: 'custom',
          required: true,
          tooltip: 'Must not be before the start date',
          render: ({ value, onChange }) => <DateField value={value} onChange={onChange} />,
          viewRender: (value) => (value ? <AppDate value={String(value)} /> : '—'),
        },
        {
          name: 'description',
          label: 'Why',
          type: 'custom',
          required: true,
          rules: [{ required: true, max: 200 }],
          render: ({ value, onChange }) => <DescriptionField value={value} onChange={onChange} />,
          viewRender: (value) => (value ? String(value) : '—'),
        },
      ],
    },
  ];

  return (
    <ResourceForm<Permit>
      mode={mode}
      entityLabel="Permit"
      icon={<CarOutlined />}
      name={dc.current.holderName}
      subtitle={dc.current.permitTypeName}
      chips={dc.current.permitTypeName ? <AppTag>{dc.current.permitTypeName}</AppTag> : undefined}
      loading={loading}
      saving={saving}
      deleting={deleting}
      form={form}
      dataController={dc}
      initialValues={dc.current}
      sections={sections}
      onBack={() => navigate('/permits')}
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
      deleteConfirmText="Retire this permit? It stays in the database with status retired."
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
