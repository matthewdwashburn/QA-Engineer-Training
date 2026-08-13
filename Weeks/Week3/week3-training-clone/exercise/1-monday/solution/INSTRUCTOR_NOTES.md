# Instructor notes — Monday

- **Array sort:** Accept bubble/selection; watch for off-by-one in reverse loops.
- **Student equals:** If trainees use only `id` in `equals`/`hashCode`, `Objects.hash(id)` is enough; warn against including mutable `name` in `hashCode` if they change the spec.
- **Enrollment count:** Should increment only in constructor; decrement not required unless you add a destroy story.
