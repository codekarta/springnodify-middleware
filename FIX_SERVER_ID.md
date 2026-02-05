# Fix: Server ID Must Be Literal "central"

## The Problem

Your `settings.xml` has:
```xml
<id>${server}</id>  ❌ WRONG - This is a variable
```

But it **MUST** be:
```xml
<id>central</id>  ✅ CORRECT - This is a literal string
```

## Why This Causes 401 Errors

The `central-publishing-maven-plugin` looks for a server configuration with the **exact id "central"**. When you use `${server}`, Maven tries to resolve it as a variable, which fails, so the plugin can't find the credentials.

## Fix for Local Testing

If you're testing locally, update your `~/.m2/settings.xml`:

**Before (WRONG):**
```xml
<server>
  <id>${server}</id>
  <username>2joR5w</username>
  <password>Zy38LSKvqXBZzAogQpSOKGwQcMOzndLTt</password>
</server>
```

**After (CORRECT):**
```xml
<server>
  <id>central</id>
  <username>2joR5w</username>
  <password>Zy38LSKvqXBZzAogQpSOKGwQcMOzndLTt</password>
</server>
```

## For GitHub Actions

The workflow will automatically fix this issue if detected, but make sure:
1. Your GitHub secrets are set correctly:
   - `CENTRAL_USERNAME`: `2joR5w`
   - `CENTRAL_PASSWORD`: `Zy38LSKvqXBZzAogQpSOKGwQcMOzndLTt`

2. The `actions/setup-java@v4` action creates the settings.xml correctly with `<id>central</id>`

## Verification

After fixing, verify your settings.xml:
```bash
grep "<id>" ~/.m2/settings.xml
```

Should show: `<id>central</id>` (NOT `<id>${server}</id>`)
