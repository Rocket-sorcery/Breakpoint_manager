package org.example.breakpoint_manager

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.xdebugger.breakpoints.XBreakpointListener
import org.example.server.Server

internal class PluginStartup: ProjectActivity, DumbAware {
    private val port = 8080

    override suspend fun execute(project: Project) {
        project.messageBus.connect().subscribe(XBreakpointListener.TOPIC, BreakpointListener(port))
        Server.start(port)
        Client.setPort(port)
        Client.start(project)
    }
}