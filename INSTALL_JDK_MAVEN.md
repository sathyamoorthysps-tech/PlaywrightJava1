# Install JDK and Maven on Windows (C:\Program Files)

## 1. Install JDK

### Option A: Eclipse Temurin (Adoptium) – recommended

1. **Download:** https://adoptium.net/temurin/releases/
   - Version: **17** (LTS)
   - Operating System: **Windows**
   - Architecture: **x64**
   - Package: **JDK**
   - Download the **.msi** installer.

2. **Run the installer** (as Administrator if you want system-wide install).
   - Install to: **`C:\Program Files\Eclipse Adoptium\jdk-17.x.x.x-hotspot`** (default).
   - Check **"Set JAVA_HOME variable"** and **"Add to PATH"** if the installer offers it.

### Option B: Oracle JDK 17

1. **Download:** https://www.oracle.com/java/technologies/downloads/#jdk17-windows
   - Choose **Windows x64 Installer**.
2. Run the installer and use the default path: **`C:\Program Files\Java\jdk-17`**.

---

## 2. Install Maven

1. **Download** the binary ZIP:
   - https://dlcdn.apache.org/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.zip  
   - Or latest: https://maven.apache.org/download.cgi

2. **Extract** the ZIP (e.g. with **Extract All**).
   - Target folder: **`C:\Program Files\Apache\Maven`**  
   - Result should be: **`C:\Program Files\Apache\Maven\apache-maven-3.9.9`**  
   - So the **Maven home** is: **`C:\Program Files\Apache\Maven\apache-maven-3.9.9`**

---

## 3. Set environment variables

### Option 1: Run the PowerShell script (easiest)

1. **Edit** `set-java-maven-env.ps1` if your install paths differ.
2. Open **PowerShell as Administrator** (for system-wide variables):
   - Right-click Start → **Windows Terminal (Admin)** or **PowerShell (Admin)**.
3. Run:
   ```powershell
   cd "C:\Users\sathy\OneDrive\Desktop\Playwright"
   Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
   .\set-java-maven-env.ps1
   ```
4. Close and reopen any terminal/IDE, then run `java -version` and `mvn -version`.

### Option 2: Set variables manually

1. Press **Win + R**, type **`sysdm.cpl`**, Enter.
2. **Advanced** tab → **Environment Variables**.
3. Under **System variables** (or **User variables**):

   | Variable    | Value |
   |------------|--------|
   | **JAVA_HOME** | `C:\Program Files\Eclipse Adoptium\jdk-17.0.xx-hotspot` (or `C:\Program Files\Java\jdk-17` for Oracle) |
   | **MAVEN_HOME** | `C:\Program Files\Apache\Maven\apache-maven-3.9.9` |

4. Edit **Path** (System or User):
   - Add: **`%JAVA_HOME%\bin`**
   - Add: **`%MAVEN_HOME%\bin`**
5. Click **OK** on all dialogs.
6. Restart any open Command Prompt, PowerShell, or IDE.

---

## 4. Verify

Open a **new** Command Prompt or PowerShell:

```cmd
java -version
javac -version
mvn -version
```

You should see the JDK and Maven versions. Then from your project:

```cmd
cd C:\Users\sathy\OneDrive\Desktop\Playwright
mvn test -Dtest=TestRunner
```
