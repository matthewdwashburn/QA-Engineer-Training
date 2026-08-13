## Story AUTH-77
### Title
As a registered, I want to reset my password,
so that I can regain access to my account

### Business Requirement (BR)
Users who forget their password must be able to securely reset it without contacting customer support.

### Functional Requirment (FR)

FR-1: User can request a password reset using their registered email.
FR-2: System generates a unique reset token.
FR-3: Reset link expires after 30 minutes.
FR-4: Reset link may only be used once.
FR-5: User can create a new password meeting password policy.

## Acceptance Criteria
AUTH-77-AC-1
Given a registered email
When the user requests a password reset
Then a reset email is sent within 2 minutes.

AUTH-77-AC-2
Given an invalid email
When the reset is requested
Then the application displays a generic success message without revealing whether the account exists.

AUTH-77-AC-3
Given a reset link that has already been used
When the user attempts to use it again
Then the system rejects the request and requires a new reset.

AUTH-77-AC-4
Given a password shorter than 12 characters
When the user submits the new password
Then validation prevents the reset.

## NON-Functional Requirements

AUTH-77-NFR-1
Password reset API respons in under 500ms (95th percentile)

AUTH-77-NFR-2
System Supports 500 concurrent reset requests

