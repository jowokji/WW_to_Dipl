# API Specification Validation

Canonical specification file:

```text
Documentation/api/openapi/weatherwear-api.openapi.json
```

## Local Validation Performed

The OpenAPI file was checked for valid JSON syntax with PowerShell:

```powershell
Get-Content Documentation\api\openapi\weatherwear-api.openapi.json -Raw | ConvertFrom-Json | Out-Null
```

Result:

```text
Passed
```

Internal `$ref` pointers were checked with a local Node.js script:

```text
OpenAPI JSON parsed. 104 internal references resolved.
```

Request and success-response example coverage was checked with a local Node.js script:

```text
All JSON request/2xx response examples covered
```

Runtime Swagger annotations and Java DTO schema annotations were verified by Maven tests:

```powershell
mvn -B test
```

Result:

```text
Tests run: 112, Failures: 0, Errors: 0, Skipped: 0
```

## Recommended OpenAPI Validation Tools

For final submission or CI, validate the file with one of the following tools.

### Redocly CLI

```bash
npx @redocly/cli lint Documentation/api/openapi/weatherwear-api.openapi.json
```

### Spectral

```bash
npx @stoplight/spectral-cli lint Documentation/api/openapi/weatherwear-api.openapi.json
```

### Swagger Editor

1. Open Swagger Editor.
2. Import `Documentation/api/openapi/weatherwear-api.openapi.json`.
3. Confirm there are no schema or reference errors.

## Validation Checklist

| Check | Status |
| --- | --- |
| JSON syntax is valid | Passed |
| Internal `$ref` pointers resolve | Passed |
| OpenAPI version is declared | Passed |
| API title and version are declared | Passed |
| Servers are declared | Passed |
| Security scheme is declared | Passed |
| Protected endpoints declare bearer authentication | Passed |
| Request DTOs are represented as schemas | Passed |
| Response DTOs are represented as schemas | Passed |
| Error model is documented | Passed |
| JSON request examples are included for body-based endpoints | Passed |
| JSON success-response examples are included | Passed |
| Runtime Swagger annotations compile | Passed |

## CI Recommendation

Add an API lint job to GitHub Actions:

```yaml
- name: Lint OpenAPI
  run: npx @redocly/cli lint Documentation/api/openapi/weatherwear-api.openapi.json
```

This requires Node.js and network access or a pinned package cache.
