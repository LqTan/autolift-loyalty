# OpenCode Documentation Template

Place your markdown documentation files here.

Files in this directory can be referenced via `opencode.json` instructions.

## Example structure

```
docs/
├── architecture.md      # Kiến trúc tổng thể
├── design.md           # Design decisions
├── uplift-modeling.md   # Uplift modeling details
└── README.md           # Index/overview
```

## Referencing from opencode.json

```json
{
  "instructions": ["docs/*.md"]
}
```