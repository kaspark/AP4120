export interface Person {
  id: number;
  name: string;
  email: string;
}

export interface PermitType {
  id: number;
  name: string;
}

export interface Permit {
  id: number;
  permitTypeId: number;
  holderId: number;
  grantedById: number;
  startDate: string;
  endDate: string;
  description: string;
  permitTypeName?: string;
  holderName?: string;
  grantedByName?: string;
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

export type PermitInput = Omit<Permit, 'id' | 'permitTypeName' | 'holderName' | 'grantedByName'>;

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

export const permitApi = {
  list: (textContains?: string): Promise<QueryResult<Permit>> =>
    fetch(
      '/api/permits?limit=50' + (textContains ? `&textContains=${encodeURIComponent(textContains)}` : ''),
      { headers: authHeaders() },
    ).then((r) => handle<QueryResult<Permit>>(r)),

  get: (id: number): Promise<Permit> =>
    fetch(`/api/permits/${id}`, { headers: authHeaders() }).then((r) => handle<Permit>(r)),

  create: (body: PermitInput): Promise<Permit> =>
    fetch('/api/permits', {
      method: 'POST',
      headers: { ...authHeaders(), 'Content-Type': 'application/json' },
      body: JSON.stringify(body),
    }).then((r) => handle<Permit>(r)),

  update: (id: number, body: PermitInput): Promise<Permit> =>
    fetch(`/api/permits/${id}`, {
      method: 'PUT',
      headers: { ...authHeaders(), 'Content-Type': 'application/json' },
      body: JSON.stringify(body),
    }).then((r) => handle<Permit>(r)),

  retire: (id: number): Promise<void> =>
    fetch(`/api/permits/${id}`, { method: 'DELETE', headers: authHeaders() }).then((r) => handle<void>(r)),

  permitTypes: (): Promise<PermitType[]> =>
    fetch('/api/permit-types', { headers: authHeaders() }).then((r) => handle<PermitType[]>(r)),

  persons: (): Promise<Person[]> =>
    fetch('/api/persons', { headers: authHeaders() }).then((r) => handle<Person[]>(r)),
};
