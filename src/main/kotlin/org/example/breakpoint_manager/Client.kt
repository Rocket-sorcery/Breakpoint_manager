package org.example.breakpoint_manager

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.toNioPathOrNull
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.xdebugger.XDebuggerManager
import com.intellij.xdebugger.breakpoints.XBreakpoint
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.awt.BorderLayout
import javax.swing.JPanel

object Client {
    private val browser = JBCefBrowser()
    private val panel = JPanel(BorderLayout(0, 20))
    private lateinit var project: Project
    private var toolWindow: ToolWindow? = null
    private var port: Int = -1
    private var url = ""

    fun setPort(port: Int) {
        if (port == -1) {
            error("Port has already been set")
        } else {
            this.port = port
            url = "http://localhost:$port"
        }
    }

    fun start(project: Project) {
        panel.add(browser.component, BorderLayout.WEST)
        browser.loadURL("$url/getPage")
        this.project = project
        toolWindow = ToolWindowManager.getInstance(Client.project).getToolWindow("Breakpoints")
        CoroutineScope(Dispatchers.IO).launch {
            updateBreakpoints()
        }
    }

    private fun update() {
        browser.loadURL("$url/getPage")
        panel.validate()
        panel.repaint()
    }

    fun stop() {
        browser.dispose()
    }

    fun getPanel(): JPanel {
        if (toolWindow != null) {
            panel.setSize(toolWindow!!.component.size.width, toolWindow!!.component.size.height)
        }
        return panel
    }

    private val httpClient: HttpClient  = HttpClient(CIO) {
            expectSuccess = false
    }

    private fun breakpointToString(breakpoint: XBreakpoint<*>): String {
        return "" + (breakpoint.sourcePosition?.file?.toNioPathOrNull() ?: "File path not found").toString() +
               ": " + (breakpoint.sourcePosition?.line ?: "Line not found").toString()  +
               "; Type: " + breakpoint.type.title
    }

    suspend fun addBreakpoint(breakpoint: XBreakpoint<*>) {
        val breakpointString = breakpointToString(breakpoint)
        val response: HttpResponse = httpClient.post("$url/addBreakpoint") {
            headers.append("Content-Type", "text/plain")
            setBody(breakpointString.toByteArray())
        }
        update()
    }
    suspend fun removeBreakpoint(breakpoint: XBreakpoint<*>) {
        val breakpointString = breakpointToString(breakpoint)
        val response: HttpResponse = httpClient.post("$url/removeBreakpoint") {
            headers.append("Content-Type", "text/plain")
            setBody(breakpointString.toByteArray())
        }
        update()
    }
    private suspend fun updateBreakpoints() {
        val breakpointList = XDebuggerManager.getInstance(project).breakpointManager.allBreakpoints.toList()
        val breakpointsString = breakpointList.drop(1).joinToString("\n") {breakpointToString(it)} // drop(1) is for the default breakpoint
        val response: HttpResponse = httpClient.post("$url/replaceList") {
            headers.append("Content-Type", "text/plain")
            setBody(breakpointsString.toByteArray())
        }
        update()
    }
}
