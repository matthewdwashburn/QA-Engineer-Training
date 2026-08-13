import { Table } from 'react-bootstrap'

// columns: [{ field, label }]
// rows:    [{ id, ... }]
// actions: optional (row) => JSX  — renders buttons in a trailing column
function DataTable({ columns, rows, actions, rowKey = 'id'}) {

  return (
    <Table striped bordered hover responsive variant="light">
      <thead>
        <tr>
          {columns.map((col) => (
            <th key={col.field}>{col.label}</th>
          ))}
          {actions && <th>Actions</th>}
        </tr>
      </thead>
      <tbody>
        {rows.map((row) => (
          <tr key={row[rowKey]}>
            {columns.map((col) => (
              <td key={col.field}>{row[col.field]}</td>
            ))}
            {actions && <td>{actions(row)}</td>}
          </tr>
        ))}
      </tbody>
    </Table>
  )
}

export default DataTable
