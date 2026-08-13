// Base URLs for the two backends.
//
// These have to be configurable because the browser resolves them, not the
// container: the address that reaches a backend depends on where the page is
// being viewed from. Running locally that is 127.0.0.1; served from EC2 it is
// the instance's public address.
//
// Vite replaces import.meta.env.VITE_* at transform time, so these come from
// the environment Vite was started with, not from the browser. The defaults
// keep `npm run dev` on a laptop working with no configuration.
export const EMPLOYEE_API_URL =
    import.meta.env.VITE_EMPLOYEE_API_URL ?? "http://127.0.0.1:5000";

export const MANAGER_API_URL =
    import.meta.env.VITE_MANAGER_API_URL ?? "http://127.0.0.1:7001";
