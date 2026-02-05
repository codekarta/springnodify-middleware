# Troubleshooting 401 Error - Step by Step

## Your Current Status
✅ Maven settings.xml exists and is configured correctly  
✅ Server ID is "central"  
✅ Username is configured  
✅ Password field is configured  

❌ **Still getting 401 Unauthorized**

## Most Likely Cause

The `CENTRAL_PASSWORD` GitHub secret likely contains a **placeholder** instead of the actual Portal User Token password.

## How to Fix

### Step 1: Verify Your Portal User Token

1. Go to https://central.sonatype.com/usertoken
2. Find your token (e.g., "GITHUB_ACCESS" with value "2joR5w")
3. **Important**: When you generated this token, you should have seen TWO values:
   - **Username**: `2joR5w` (this is what you see in the token list)
   - **Password**: A separate password string (this is shown ONLY when you first generate the token)

### Step 2: Check Your GitHub Secrets

Go to your GitHub repository → Settings → Secrets and variables → Actions

Check `CENTRAL_PASSWORD`:
- ❌ If it says `my password string here` or similar → **This is wrong!**
- ❌ If it's your regular account password → **This is wrong!**
- ✅ It should be the **token password** shown when you generated the token

### Step 3: If You Don't Have the Token Password

If you don't have the token password (because it's only shown once), you need to regenerate the token:

1. Go to https://central.sonatype.com/usertoken
2. Click "Revoke Token" on your existing token
3. Click "Generate User Token"
4. **IMMEDIATELY copy BOTH values**:
   - Username (e.g., `2joR5w`)
   - Password (a long string)
5. Update GitHub secrets:
   - `CENTRAL_USERNAME`: The username from step 4
   - `CENTRAL_PASSWORD`: The password from step 4 (NOT a placeholder!)

### Step 4: Verify Token Format

Portal User Token passwords are typically:
- Long strings (20+ characters)
- May contain letters, numbers, and special characters
- NOT your account password
- NOT a placeholder like "my password string here"

### Step 5: Test Again

After updating the secrets, trigger the workflow again. The new verification step will test the token authentication before attempting to publish.

## Still Not Working?

If you've verified the password is correct and you're still getting 401:

1. Check the workflow logs for the "Test Portal Token Authentication" step
2. Verify the namespace `io.github.codekarta` is still verified at https://central.sonatype.com/publishing/namespaces
3. Ensure you're using Portal User Tokens, not old OSSRH credentials
4. Try regenerating the token and updating secrets again

## Quick Checklist

- [ ] `CENTRAL_USERNAME` = Token username (e.g., `2joR5w`)
- [ ] `CENTRAL_PASSWORD` = Actual token password (NOT "my password string here")
- [ ] Token was generated from https://central.sonatype.com/usertoken
- [ ] Namespace `io.github.codekarta` is verified
- [ ] GitHub secrets were updated after token generation
