import dayjs from 'dayjs';
import type { Rule } from 'antd/es/form';

/** Shown on the end date the same way a missing license plate is shown on its field. */
export const END_BEFORE_START = 'Must not be before the start date';

export function endNotBeforeStart(start: unknown, end: unknown): boolean {
  if (start == null || start === '' || end == null || end === '') return true;
  const startDay = dayjs.isDayjs(start) ? start : dayjs(String(start));
  const endDay = dayjs.isDayjs(end) ? end : dayjs(String(end));
  if (!startDay.isValid() || !endDay.isValid()) return true;
  return !endDay.isBefore(startDay, 'day');
}

export const endDateRule: Rule = ({ getFieldValue }) => ({
  validator(_, value) {
    if (endNotBeforeStart(getFieldValue('startDate'), value)) return Promise.resolve();
    return Promise.reject(new Error(END_BEFORE_START));
  },
});
