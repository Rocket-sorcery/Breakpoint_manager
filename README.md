# Breakpoint manager

Tracks addition and removal of breakpoints with an XBreakpointListener, sending the added or removed ones as strings to the HttpServer running as a separate plugin. The server in turn stores the strings and serves an html page with the list when requested to. The page is requested and displayed using a JCEF browser built into the plugin's ToolWindow. The information displayed includes the total number of breakpoints, and a set of file path, line number and type for each one.

Upon startup, the listener, the server and the client are started, the latter sending the list of breakpoints already present to the server.
