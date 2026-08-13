import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import 'bootstrap/dist/css/bootstrap.min.css'
import './index.css'
import { createBrowserRouter, RouterProvider, Navigate } from 'react-router-dom'
import ProtectedRoute from './components/ProtectedRoute.jsx'
import WelcomePage from './universal pages/WelcomePage.jsx'
import EmployeeLoginPage from './employee pages/EmployeeLoginPage.jsx'
import ManagerLoginPage from './manager pages/ManagerLoginPage.jsx'
import EmployeeDashboardPage from './employee pages/EmployeeDashboardPage.jsx'
import ManagerDashboardPage from './manager pages/ManagerDashboardPage.jsx'
import NotFoundPage from './universal pages/NotFoundPage.jsx'
import EmployeePendingExpensePage from './employee pages/EmployeePendingExpensePage.jsx'
import EmployeeSubmitExpensePage from './employee pages/EmployeeSubmitExpensePage.jsx'
import EmployeeExpenseHistoryPage from './employee pages/EmployeeExpenseHistoryPage.jsx'
import ManagerExpenseReviewPage from './manager pages/ManagerExpenseReviewPage.jsx'
import ManagerExpenseReportPage from './manager pages/ManagerExpenseReportPage.jsx'

const router = createBrowserRouter([
  {
    path: '/',
    element: <WelcomePage/>,
    errorElement: <NotFoundPage/>
  },
  {
    path: '/employee/login',
    element: <EmployeeLoginPage/>,
    errorElement: <NotFoundPage/>

  },
  {
    path: '/manager/login',
    element: <ManagerLoginPage/>,
    errorElement: <NotFoundPage/>

  },
  {
    path: '/employee/dashboard',
    element: <ProtectedRoute><EmployeeDashboardPage/></ProtectedRoute>,
    errorElement: <NotFoundPage/>
  },
  {
    path: '/employee/pending-expenses',
    element: <ProtectedRoute><EmployeePendingExpensePage/></ProtectedRoute>,
    errorElement: <NotFoundPage/>
  },

  {
    path: '/employee/expense-history',
    element: <ProtectedRoute><EmployeeExpenseHistoryPage/></ProtectedRoute>,
    errorElement: <NotFoundPage/>
  },

  {
    path: '/employee/submit-expense',
    element: <ProtectedRoute><EmployeeSubmitExpensePage/></ProtectedRoute>,
    errorElement: <NotFoundPage/>
  },
  {
    path: '/manager/dashboard',
    element: <ProtectedRoute authType="bearer" apiURL="http://127.0.0.1:7001" meRoute="/me"><ManagerDashboardPage/></ProtectedRoute>,
    errorElement: <NotFoundPage/>
  },
  {
    path: '/manager/expense-review',
    element: <ProtectedRoute authType="bearer" apiURL="http://127.0.0.1:7001" meRoute="/me"><ManagerExpenseReviewPage/></ProtectedRoute>,
    errorElement: <NotFoundPage/>
  },
  {
    path: '/manager/expense-report',
    element: <ProtectedRoute authType="bearer" apiURL="http://127.0.0.1:7001" meRoute="/me"><ManagerExpenseReportPage/></ProtectedRoute>,
    errorElement: <NotFoundPage/>
  }
])

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <RouterProvider router={router} />
  </StrictMode>
)
