# How GitHub Actions Publishes to Maven Central

## You Don't Need settings.xml!

The `actions/setup-java@v4` action **automatically creates** the Maven `settings.xml` file from your GitHub secrets. You don't need to create or manage it manually.

## How It Works

1. **GitHub Secrets** (set in your repository settings):
   - `CENTRAL_USERNAME` = Your Portal User Token username (e.g., `2joR5w`)
   - `CENTRAL_PASSWORD` = Your Portal User Token password
   - `GPG_PRIVATE_KEY` = Your GPG private key
   - `GPG_PASSPHRASE` = Your GPG passphrase

2. **The Workflow** passes these secrets to `setup-java`:
   ```yaml
   - uses: actions/setup-java@v4
     with:
       server-id: central
       server-username: ${{ secrets.CENTRAL_USERNAME }}
       server-password: ${{ secrets.CENTRAL_PASSWORD }}
   ```

3. **setup-java automatically creates** `~/.m2/settings.xml`:
   ```xml
   <settings>
     <servers>
       <server>
         <id>central</id>
         <username>2joR5w</username>
         <password>Zy38LSKvqXBZzAogQpSOKGwQcMOzndLTt</password>
       </server>
     </servers>
   </settings>
   ```

4. **Maven plugin** reads from this automatically created `settings.xml`

## What You Need to Do

**Only set the GitHub secrets correctly:**
1. Go to your repo → Settings → Secrets and variables → Actions
2. Set `CENTRAL_USERNAME` = Portal User Token username
3. Set `CENTRAL_PASSWORD` = Portal User Token password (the actual password, not a placeholder)

That's it! The workflow handles everything else.

## If You Get 401 Errors

The most common cause is **incorrect GitHub secrets**:
- Make sure `CENTRAL_PASSWORD` is the actual token password
- Not a placeholder like "my password string here"
- Not your account password
- The password shown when you generate/view the token at https://central.sonatype.com/usertoken

## Testing Locally (Optional)

If you want to test locally, you CAN create `~/.m2/settings.xml` manually, but it's not required for GitHub Actions. The workflow handles it automatically.
