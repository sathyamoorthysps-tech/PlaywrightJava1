# MCP Server Configuration for Cursor

## Overview
This document explains how to configure the Playwright MCP server (`@executeautomation/playwright-mcp-server`) for use with Cursor IDE.

## Installation Status

✅ **Package Installed:** `@executeautomation/playwright-mcp-server@1.0.12`  
✅ **Location:** `C:\Users\sathy\AppData\Roaming\npm\playwright-mcp-server.cmd`  
✅ **Configuration:** Added to Cursor settings.json

## Configuration Details

### Cursor Settings Location
**File:** `C:\Users\sathy\AppData\Roaming\Cursor\User\settings.json`

### MCP Server Configuration
```json
{
    "mcpServers": {
        "playwright": {
            "command": "playwright-mcp-server",
            "args": [],
            "env": {}
        }
    }
}
```

## Configuration Options

### Basic Configuration
```json
"playwright": {
    "command": "playwright-mcp-server",
    "args": [],
    "env": {}
}
```

### Advanced Configuration (Optional)
```json
"playwright": {
    "command": "playwright-mcp-server",
    "args": [
        "--headless",
        "--browser=chromium"
    ],
    "env": {
        "PLAYWRIGHT_BROWSERS_PATH": "0"
    }
}
```

### Full Path Configuration (If needed)
```json
"playwright": {
    "command": "C:\\Users\\sathy\\AppData\\Roaming\\npm\\playwright-mcp-server.cmd",
    "args": [],
    "env": {}
}
```

## Features Available

Once configured, the Playwright MCP server provides:

1. **Browser Automation**
   - Navigate to URLs
   - Click elements
   - Fill forms
   - Extract data

2. **Screenshot Capabilities**
   - Capture full page screenshots
   - Capture element screenshots
   - Screenshot comparison

3. **Device Emulation**
   - 143 real device presets
   - iPhone, iPad, Pixel, Galaxy, Desktop
   - Custom viewport sizes

4. **JavaScript Execution**
   - Execute JavaScript in browser context
   - Extract data using JS
   - Manipulate DOM

5. **Test Code Generation**
   - Generate Playwright test code
   - Export test scripts
   - Code snippets

## Usage in Cursor

After configuration, you can:

1. **Ask Cursor to automate browsers:**
   ```
   "Take a screenshot of https://www.amazon.in"
   "Click the search button on Amazon"
   "Extract product titles from the search results"
   ```

2. **Generate Playwright code:**
   ```
   "Generate Playwright test code for adding a product to cart"
   "Create a test that searches for iPhone on Amazon"
   ```

3. **Debug web pages:**
   ```
   "What elements are visible on this page?"
   "Check if the add to cart button exists"
   ```

## Verification

To verify the MCP server is working:

1. **Restart Cursor** after configuration changes
2. **Check MCP Status:**
   - Open Cursor settings
   - Look for MCP server status
   - Verify "playwright" server is connected

3. **Test Commands:**
   - Try asking Cursor to take a screenshot
   - Request browser automation tasks
   - Generate Playwright code

## Troubleshooting

### Issue: MCP Server Not Found
**Solution:**
```json
"playwright": {
    "command": "C:\\Users\\sathy\\AppData\\Roaming\\npm\\playwright-mcp-server.cmd",
    "args": [],
    "env": {}
}
```

### Issue: Permission Denied
**Solution:**
- Ensure npm global bin is in PATH
- Check file permissions on the executable
- Run Cursor as administrator (if needed)

### Issue: Browser Not Found
**Solution:**
Install Playwright browsers:
```bash
npx playwright install chromium
```

### Issue: MCP Server Not Responding
**Solution:**
1. Check Cursor logs for errors
2. Verify the command path is correct
3. Test the command manually:
   ```bash
   playwright-mcp-server --help
   ```

## Environment Variables

Optional environment variables you can set:

```json
"env": {
    "PLAYWRIGHT_BROWSERS_PATH": "0",
    "PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD": "0",
    "DEBUG": "mcp:*"
}
```

## Alternative: Using Node.js Directly

If the command doesn't work, use Node.js directly:

```json
"playwright": {
    "command": "node",
    "args": [
        "C:\\Users\\sathy\\AppData\\Roaming\\npm\\node_modules\\@executeautomation\\playwright-mcp-server\\dist\\index.js"
    ],
    "env": {}
}
```

## Related Packages

You also have installed:
- `@playwright/mcp` - Official Playwright MCP tools

## Next Steps

1. **Restart Cursor** to load the new configuration
2. **Test the MCP server** by asking Cursor to perform browser automation
3. **Explore features** like screenshot capture and code generation
4. **Integrate** with your Java Playwright automation framework

## Resources

- [Playwright MCP Server Documentation](https://www.npmjs.com/package/@executeautomation/playwright-mcp-server)
- [Model Context Protocol](https://modelcontextprotocol.io/)
- [Cursor Documentation](https://cursor.sh/docs)

## Support

If you encounter issues:
1. Check Cursor's MCP server logs
2. Verify npm global packages: `npm list -g`
3. Test the command manually in terminal
4. Check Cursor's documentation for MCP configuration

---

**Last Updated:** February 12, 2026  
**Configuration Status:** ✅ Configured
