import { useEffect, useState } from 'react';
import { Checkbox, Input } from 'antd';

/** Reasons the clerk can tick. Anything else is stored as the "Other" text. */
export const REASON_OPTIONS = [
  'District 1 resident',
  'District 2 resident',
  'TalTech staff',
  'TalTech student',
  'Contractor',
  'Maintenance staff',
] as const;

const KNOWN = new Set<string>(REASON_OPTIONS);

export function parseReason(description: string): { selected: string[]; other: string } {
  const selected: string[] = [];
  const other: string[] = [];
  for (const part of description.split('; ').map((piece) => piece.trim()).filter(Boolean)) {
    if (KNOWN.has(part)) {
      selected.push(part);
    } else {
      other.push(part);
    }
  }
  return { selected, other: other.join('; ') };
}

export function composeReason(selected: string[], otherOn: boolean, other: string): string {
  const parts = REASON_OPTIONS.filter((option) => selected.includes(option));
  if (otherOn && other.trim()) {
    parts.push(other.trim());
  }
  return parts.join('; ');
}

/** Checkbox list plus the free-text "Other" line. The form value is the stored reason. */
export const ReasonPicker = ({
  value,
  onChange,
}: {
  value?: unknown;
  onChange?: (next: string) => void;
}) => {
  const text = value == null ? '' : String(value);
  const parsed = parseReason(text);
  const [otherOn, setOtherOn] = useState(parsed.other.length > 0);
  const [other, setOther] = useState(parsed.other);

  useEffect(() => {
    const next = parseReason(text);
    setOther(next.other);
    if (next.other.length > 0) {
      setOtherOn(true);
    }
  }, [text]);

  const emit = (selected: string[], nextOn: boolean, nextOther: string) => {
    onChange?.(composeReason(selected, nextOn, nextOther));
  };

  return (
    <div style={{ display: 'grid', gap: 8 }}>
      <Checkbox.Group
        style={{ display: 'grid', gap: 8 }}
        value={parsed.selected}
        onChange={(checked) => emit(checked.map(String), otherOn, other)}
      >
        {REASON_OPTIONS.map((option) => (
          <Checkbox key={option} value={option}>
            {option}
          </Checkbox>
        ))}
      </Checkbox.Group>
      <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
        <Checkbox
          checked={otherOn}
          onChange={(event) => {
            const nextOn = event.target.checked;
            setOtherOn(nextOn);
            emit(parsed.selected, nextOn, other);
          }}
        >
          Other:
        </Checkbox>
        <Input
          value={other}
          maxLength={200}
          onChange={(event) => {
            const nextOther = event.target.value;
            setOther(nextOther);
            const nextOn = otherOn || nextOther.trim().length > 0;
            setOtherOn(nextOn);
            emit(parsed.selected, nextOn, nextOther);
          }}
        />
      </div>
    </div>
  );
};
