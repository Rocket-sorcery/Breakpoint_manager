# Breakpoint manager

Tracks addition and removal of breakpoints with an XBreakpointListener, sending the added or removed ones as strings to the HttpServer running as a separate plugin. The server in turn stores the strings and serves an html page with the list when requested to. The page is requested and displayed using a JCEF browser built into the plugin's ToolWindow. 
The information displayed includes the total number of breakpoints, and a set of file path, line number and type for each one.

## Parts of the code
- **Server**
  - the object Server, as said, is a basic HttpServer from com.sun, has several contexts like /getPage and /addBreakpoint, and stores a list of breakpoints strings
- **Main plugin code**
  - PluginStartup is the postStartupActivity that starts the listener, the server and the client, causing the latter to send the list of breakpoints already present in the project to the server
  - Client is another object handling the communications with the server and the creation of UI content for the toolWindow, as well holding the JCEF browser object
  - ManagerWindow is the factory class for the plugin ToolWindow, the content for which it takes from the Client object
  - BreakpointListener is responsible for triggering the appropriate functions upon the addition/removal of a breakpoint
  - ProjectShutdownListener is virtually a failed attempt at stopping the jcef_helper.exe from continuing to run after the plugin shuts down by dispose()-ing of the JCEF browser in the client
 
## Known issues:
- When the plugin is run from the "run plugin" task, four warnings are shown in the terminal:
  1. ```"VersionControl.Log.Commit.rowHeight = null in LookAndFeelThemeAdapter; it may lead to performance degradation```
  2. ```Bundled shared index is not found at: \<path to jdk-shared-indexes\>```
  3. ```JBCefApp$Holder <clinit> requests JBCefAppCache instance. Class initialization must not depend on services```
  4. ```File not found: api23.txt```
- After a bit of time, another warning appears:
    ```Failed to run C:\WINDOWS\system32\wsl.exe``` ... ```stderr=The Windows Subsystem for Linux is not installed```
  Together with a java.io.IOException saying the same thing about wsl.exe not being present
- Finally, after some more time, three more warnings follow about there not being accessors for:
  1. ```org.jetbrains.kotlin.cli.common.arguments.InternalArgument```
  2. ```com.intellij.platform.feedback.impl.state.DontShowAgainFeedbackState```
  3. ```com.intellij.platform.feedback.impl.state.CommonFeedbackSurveysState```

None of those seem to impact the plugin, but what do I know, really. I could get rid of some of them with enough google searches, and time, more importantly, some could probably just have been suppressed, but I had limited time and a lot of learning to do for this plugin.
