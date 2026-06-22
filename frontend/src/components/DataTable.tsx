import type { ReactNode } from "react";

type DataTableProps<T> = {
  title: string;
  columns: string[];
  rows: T[];
  renderRow: (row: T) => ReactNode;
};

export function DataTable<T>({ title, columns, rows, renderRow }: DataTableProps<T>) {
  return (
    <section className="table-card">
      <div className="table-header">
        <h3>{title}</h3>
      </div>
      <table>
        <thead>
          <tr>
            {columns.map((column) => (
              <th key={column}>{column}</th>
            ))}
          </tr>
        </thead>
        <tbody>{rows.map(renderRow)}</tbody>
      </table>
    </section>
  );
}
