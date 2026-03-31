# DevOps Layout

This folder groups DevOps-facing assets in one place while preserving backward compatibility.

Included now:
- `devops/scripts/` (copied from `scripts/`)
- `devops/docs/` (copied from `docs/`)

Compatibility note:
- Existing pipelines and commands still use root paths (`scripts/`, `docs/`) to avoid breaking current workflows.
- Treat root folders as active paths until a full reference migration is completed and validated.

Suggested next phase:
1. Update all CI/CD, Makefile, and docs references.
2. Run full verification pipeline.
3. Switch source-of-truth paths to `devops/*`.
