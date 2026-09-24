import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { DatePicker, Form, Input, InputNumber, Select } from 'antd';
import { AppButtonPrimary, AppButtonSecondary, AppCard, useAppNotification } from '@helex/ui';
import type { Dayjs } from 'dayjs';
import { teaApi, type Category, type TeaUser } from '../api';

export const TeaCreate = () => {
  const navigate = useNavigate();
  const notify = useAppNotification();
  const [categories, setCategories] = useState<Category[]>([]);
  const [users, setUsers] = useState<TeaUser[]>([]);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    Promise.all([teaApi.categories(), teaApi.users()])
      .then(([categoryList, userList]) => {
        setCategories(categoryList);
        setUsers(userList);
      })
      .catch((e) => notify.error('Could not load lists', e.message));
  }, [notify]);

  const submit = (values: {
    name: string;
    brand: string;
    categoryId: number;
    ownerId: number;
    purchaseDate: Dayjs;
    expiryDate: Dayjs;
    quantity: number;
    unit: string;
  }) => {
    setSaving(true);
    teaApi
      .create({
        name: values.name,
        brand: values.brand,
        categoryId: values.categoryId,
        ownerId: values.ownerId,
        purchaseDate: values.purchaseDate.format('YYYY-MM-DD'),
        expiryDate: values.expiryDate.format('YYYY-MM-DD'),
        quantity: values.quantity,
        unit: values.unit,
      })
      .then((created) => {
        notify.success('Added', `${created.name} · ${created.brand}`);
        navigate('/teas');
      })
      .catch((e) => notify.error('Could not add tea', e.message))
      .finally(() => setSaving(false));
  };

  return (
    <AppCard title="Add tea" style={{ maxWidth: 640, margin: '24px auto' }}>
      <Form layout="vertical" onFinish={submit} requiredMark initialValues={{ quantity: 1, unit: 'bags' }}>
        <Form.Item name="name" label="Name" rules={[{ required: true, max: 255 }]}>
          <Input placeholder="Earl Grey" />
        </Form.Item>
        <Form.Item name="brand" label="Brand" rules={[{ required: true, max: 255 }]}>
          <Input placeholder="Twinings" />
        </Form.Item>
        <Form.Item name="categoryId" label="Classification" rules={[{ required: true }]}>
          <Select options={categories.map((c) => ({ value: c.id, label: c.name }))} placeholder="Choose a classification" />
        </Form.Item>
        <Form.Item name="ownerId" label="Owner" rules={[{ required: true }]}>
          <Select options={users.map((u) => ({ value: u.id, label: `${u.name} · ${u.email}` }))} placeholder="Choose an owner" />
        </Form.Item>
        <Form.Item name="purchaseDate" label="Purchase date" rules={[{ required: true }]}>
          <DatePicker style={{ width: '100%' }} />
        </Form.Item>
        <Form.Item name="expiryDate" label="Expiry date" rules={[{ required: true }]} extra="Must not be before the purchase date">
          <DatePicker style={{ width: '100%' }} />
        </Form.Item>
        <Form.Item name="quantity" label="Quantity" rules={[{ required: true, type: 'number', min: 0 }]}>
          <InputNumber min={0} style={{ width: '100%' }} />
        </Form.Item>
        <Form.Item name="unit" label="Unit" rules={[{ required: true, max: 30 }]}>
          <Input placeholder="bags" />
        </Form.Item>
        <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
          <AppButtonSecondary onClick={() => navigate('/teas')}>Cancel</AppButtonSecondary>
          <AppButtonPrimary htmlType="submit" loading={saving}>
            Add
          </AppButtonPrimary>
        </div>
      </Form>
    </AppCard>
  );
};
