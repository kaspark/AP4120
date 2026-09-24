export interface TeaUser {
  id: number;
  name: string;
  email: string;
}

export interface Category {
  id: number;
  name: string;
}

export interface Tea {
  id: number;
  name: string;
  brand: string;
  categoryId: number;
  ownerId: number;
  purchaseDate: string;
  expiryDate: string;
  quantity: number;
  unit: string;
  categoryName?: string;
  ownerName?: string;
  sysStatus?: string;
  sysVersion?: number;
  sysCreatedAt?: string;
  sysCreatedBy?: string;
  sysModifiedAt?: string;
  sysModifiedBy?: string;
}

export interface QueryResult<T> {
  data: T[];
  meta?: { total?: number };
}

export type TeaInput = Omit<Tea, 'id' | 'categoryName' | 'ownerName'>;

function authHeaders(): Record<string, string> {
  const user = localStorage.getItem('helex_mock_user') || 'superadmin';
  return { Authorization: `Bearer ${user}` };
}

async function handle<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const problem = await response.json().catch(() => null);
    throw new Error(problem?.detail || `HTTP ${response.status}`);
  }
  if (response.status === 204) {
    return undefined as T;
  }
  return response.json();
}

export const teaApi = {
  list: (textContains?: string): Promise<QueryResult<Tea>> =>
    fetch(
      '/api/teas?limit=50' + (textContains ? `&textContains=${encodeURIComponent(textContains)}` : ''),
      { headers: authHeaders() },
    ).then((r) => handle<QueryResult<Tea>>(r)),

  get: (id: number): Promise<Tea> =>
    fetch(`/api/teas/${id}`, { headers: authHeaders() }).then((r) => handle<Tea>(r)),

  create: (body: TeaInput): Promise<Tea> =>
    fetch('/api/teas', {
      method: 'POST',
      headers: { ...authHeaders(), 'Content-Type': 'application/json' },
      body: JSON.stringify(body),
    }).then((r) => handle<Tea>(r)),

  update: (id: number, body: TeaInput): Promise<Tea> =>
    fetch(`/api/teas/${id}`, {
      method: 'PUT',
      headers: { ...authHeaders(), 'Content-Type': 'application/json' },
      body: JSON.stringify(body),
    }).then((r) => handle<Tea>(r)),

  retire: (id: number): Promise<void> =>
    fetch(`/api/teas/${id}`, { method: 'DELETE', headers: authHeaders() }).then((r) => handle<void>(r)),

  categories: (): Promise<Category[]> =>
    fetch('/api/categories', { headers: authHeaders() }).then((r) => handle<Category[]>(r)),

  users: (): Promise<TeaUser[]> =>
    fetch('/api/users', { headers: authHeaders() }).then((r) => handle<TeaUser[]>(r)),
};
