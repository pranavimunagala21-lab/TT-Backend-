# Local development administrator

When the `local` Spring profile is active, startup creates this development account if it does not already exist:

- Email: `admin@campussphere.local`
- Password: `CampusAdmin@2026!`

The password is BCrypt-hashed before it is saved. If the email already exists, startup promotes that account to `ADMIN` but does not change its password. Configure `ADMIN_BOOTSTRAP_EMAIL` and `ADMIN_BOOTSTRAP_PASSWORD` to use different local credentials; the password must contain at least 12 characters. Leave both variables empty to disable bootstrap.

The runner is restricted to the `local` profile. Set `SPRING_PROFILES_ACTIVE=prod` in production; do not use the local Compose defaults or local development credentials on a public deployment. Provision production administrators through a controlled deployment or database process.

Public registration always creates `STUDENT` accounts and does not accept role assignment.
