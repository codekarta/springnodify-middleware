# Maven Central Publishing Guide

## Troubleshooting 401 Unauthorized Error

If you're getting a `401 Unauthorized` error when publishing to Maven Central, it's likely an authentication issue. Here's how to fix it:

### 1. Verify You're Using Portal User Tokens

The `central-publishing-maven-plugin` requires **Portal User Tokens** from Maven Central, not regular username/password credentials.

#### How to Generate Portal User Tokens:

1. Log into [Maven Central Portal](https://central.sonatype.com/)
2. Navigate to **User Token** (or go to `/usertoken`)
3. Generate a new token
4. You'll receive:
   - **Username**: A token username (not your regular account username)
   - **Password**: A token password string

#### Update GitHub Secrets:

Make sure your GitHub repository secrets are set with the Portal User Token credentials:

- `CENTRAL_USERNAME`: The token username from the Portal User Token
- `CENTRAL_PASSWORD`: The token password from the Portal User Token
- `GPG_PRIVATE_KEY`: Your GPG private key (export with `gpg --armor --export-secret-keys <key-id>`)
- `GPG_PASSPHRASE`: The passphrase for your GPG key

### 2. Verify Namespace Ownership

Ensure your namespace (`io.github.codekarta`) is verified in the Maven Central Portal:
- Go to https://central.sonatype.com/publishing/namespaces
- Verify that `io.github.codekarta` shows as "Verified"

### 3. Check Maven Settings Configuration

The GitHub Actions workflow uses `actions/setup-java@v4` which automatically configures Maven `settings.xml` with the credentials. The plugin reads credentials from the server with id `central`.

### 4. Common Issues

- **Old OSSRH credentials**: If you're using credentials from the old Sonatype OSSRH system, you need to generate new Portal User Tokens
- **Incorrect token format**: Make sure you're using the exact username and password from the Portal User Token, not your regular account credentials
- **Expired tokens**: Portal User Tokens don't expire, but if you regenerated them, make sure GitHub secrets are updated

### 5. Testing Locally

To test publishing locally, configure Maven `settings.xml` at `~/.m2/settings.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.2.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.2.0
          https://maven.apache.org/xsd/settings-1.2.0.xsd">
  <servers>
    <server>
      <!-- CRITICAL: Must be exactly "central" (not ${server} or any variable) -->
      <id>central</id>
      <username>YOUR_PORTAL_TOKEN_USERNAME</username>
      <password>YOUR_PORTAL_TOKEN_PASSWORD</password>
    </server>
  </servers>
</settings>
```

**Common Mistakes:**
- ❌ `<id>${server}</id>` - Don't use variables, use the literal string `central`
- ❌ `<password>my password string here</password>` - Use the actual password from your Portal User Token
- ✅ `<id>central</id>` - Correct format

Then run:
```bash
mvn clean deploy -DskipTests -Dgpg.passphrase=YOUR_GPG_PASSPHRASE
```

**Note:** When you generate a Portal User Token, you get TWO values:
1. **Username**: The token identifier (e.g., `2joR5w`)
2. **Password**: A separate password string (not your account password)

Both values are shown when you generate the token. Make sure you're using the actual password string, not a placeholder.

### 6. Additional Resources

- [Maven Central Publishing Guide](https://central.sonatype.org/publish/publish-portal-maven)
- [Portal User Token Documentation](https://central.sonatype.org/publish/publish-portal-guide)
