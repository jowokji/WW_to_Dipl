# Database Requirements Checklist

## Category

The project uses a relational transactional database (OLTP): PostgreSQL. OLAP and NoSQL are not required for the current WeatherWear backend scope.

## Minimum Requirements for OLTP

| Point | Done | Evidence |
| --- | --- | --- |
| Data dictionary | Yes | `Documentation/database_documentation.md` |
| Describe tables, columns, data types, keys, and quality expectations | Yes | Data dictionary section |
| Describe integrity and transactions | Yes | Documentation section 7, migrations, Spring services |
| Logical schema | Yes | `Documentation/database_schema.mmd` and DOCX figure |
| DDL script | Yes | `src/main/resources/db/migration/V1__init_schema.sql`, `V2__database_integrity_and_reporting.sql`, `V3__align_legacy_schema_with_documentation.sql` |
| Modern relational DBMS | Yes | PostgreSQL 16 |
| 3NF design | Yes | Documentation normalization section |
| Enough tables and relationships | Yes | 7 tables, 1:1 and 1:N relationships |
| PK/FK/constraints | Yes | Flyway migrations V1 and V2 |
| Meaningful data types | Yes | BIGSERIAL, VARCHAR, TEXT, BOOLEAN, TIMESTAMP, DOUBLE PRECISION |
| Structure created by scripts/migrations | Yes | Flyway |
| Scripts in Git | Yes | `src/main/resources/db` |
| Test records | Yes | `src/main/resources/db/testdata/weatherwear_test_data.sql` |
| Reference/fixed data approach | Yes | Enum-like values are constrained in V2 and documented |
| Roles/access rights | Yes | `src/main/resources/db/security/roles_and_grants.sql` |
| Application not superuser | Yes | Docker uses `APP_DB_USER`; admin user is separate |
| Passwords encrypted | Yes | BCrypt encoder and BCrypt test-data hashes |
| Data integrity through constraints/transactions | Yes | FK, UNIQUE, CHECK, triggers, `@Transactional` |

## Maximum / Extra Requirements Covered

| Point | Status | Evidence |
| --- | --- | --- |
| Database versioning | Covered | Flyway migrations and Git |
| Indexes with reasoning | Covered | Database documentation index section |
| Triggers/UDF | Covered | `weatherwear_set_updated_at`, `weatherwear_mask_email` |
| Views | Covered | `v_anonymized_users`, `v_user_recommendation_summary` |
| Data masking/anonymization | Covered | `v_anonymized_users` and masking function |

## Not Applicable

| Requirement | Reason |
| --- | --- |
| OLAP analytical DBMS | The project has no analytical warehouse/reporting pipeline requirement |
| Raw/staging/data mart layers | Not needed for OLTP application data |
| NoSQL DBMS | Project data is structured relational data; NoSQL would be unjustified |
| Multiple DBMS types | Current scope does not require a second DBMS |
| SCD / analytical dimensions | OLAP-only requirement, not part of this backend |
