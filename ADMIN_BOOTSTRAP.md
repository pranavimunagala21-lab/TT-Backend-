# Local development administrator

The backend creates or promotes a local administrator only when both `ADMIN_BOOTSTRAP_EMAIL` and `ADMIN_BOOTSTRAP_PASSWORD` are explicitly configured. The password must contain at least 12 characters. Leave both values empty to disable bootstrap. The runner is active only in the `local` Spring profile; production administrator provisioning must use a controlled database or deployment process.

Public registration always creates `STUDENT` accounts and does not accept role assignment.
