package org.example.breakpoint_manager

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectCloseListener

@Suppress("UnstableApiUsage")
class ProjectShutdownListener: ProjectCloseListener {
    override fun projectClosing(project: Project) {
        super.projectClosing(project)
        Client.stop()
    }
}