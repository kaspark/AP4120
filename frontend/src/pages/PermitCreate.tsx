import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { DatePicker, Form, Input, Select } from 'antd';
import { AppButtonPrimary, AppButtonSecondary, AppCard, useAppNotification } from '@helex/ui';
import type { Dayjs } from 'dayjs';
import { permitApi, type PermitType, type Person } from '../api';

export const PermitCreate = () => {
  const navigate = useNavigate();
  const notify = useAppNotification();
  const [types, setTypes] = useState<PermitType[]>([]);
  const [persons, setPersons] = useState<Person[]>([]);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    Promise.all([permitApi.permitTypes(), permitApi.persons()])
      .then(([typeList, personList]) => {
        setTypes(typeList);
        setPersons(personList);
      })
      .catch((e) => notify.error('Could not load lists', e.message));
  }, [notify]);

  const submit = (values: {
    permitTypeId: number;
    holderId: number;
    grantedById: number;
    startDate: Dayjs;
    endDate: Dayjs;
    description: string;
  }) => {
    setSaving(true);
    permitApi
      .create({
        permitTypeId: values.permitTypeId,
        holderId: values.holderId,
        grantedById: values.grantedById,
        startDate: values.startDate.format('YYYY-MM-DD'),
        endDate: values.endDate.format('YYYY-MM-DD'),
        description: values.description,
      })
      .then((created) => {
        notify.success('Added', created.holderName ?? 'Permit');
        navigate('/permits');
      })
      .catch((e) => notify.error('Could not add permit', e.message))
      .finally(() => setSaving(false));
  };

  const personOptions = persons.map((p) => ({ value: p.id, label: `${p.name} · ${p.email}` }));

  return (
    <AppCard title="Add permit" style={{ maxWidth: 640, margin: '24px auto' }}>
      <Form layout="vertical" onFinish={submit} requiredMark>
        <Form.Item name="holderId" label="Holder" rules={[{ required: true }]}>
          <Select options={personOptions} placeholder="Who the permit is for" />
        </Form.Item>
        <Form.Item name="permitTypeId" label="Permit type" rules={[{ required: true }]}>
          <Select options={types.map((t) => ({ value: t.id, label: t.name }))} placeholder="Choose a type" />
        </Form.Item>
        <Form.Item name="grantedById" label="Granted by" rules={[{ required: true }]}>
          <Select options={personOptions} placeholder="Who granted the permit" />
        </Form.Item>
        <Form.Item name="startDate" label="Start date" rules={[{ required: true }]}>
          <DatePicker style={{ width: '100%' }} />
        </Form.Item>
        <Form.Item name="endDate" label="End date" rules={[{ required: true }]} extra="Must not be before the start date">
          <DatePicker style={{ width: '100%' }} />
        </Form.Item>
        <Form.Item name="description" label="Why" rules={[{ required: true, max: 200 }]}>
          <Input.TextArea rows={3} maxLength={200} placeholder="Why this parking access was granted" />
        </Form.Item>
        <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
          <AppButtonSecondary onClick={() => navigate('/permits')}>Cancel</AppButtonSecondary>
          <AppButtonPrimary htmlType="submit" loading={saving}>
            Add
          </AppButtonPrimary>
        </div>
      </Form>
    </AppCard>
  );
};
