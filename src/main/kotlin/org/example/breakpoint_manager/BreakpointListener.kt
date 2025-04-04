package org.example.breakpoint_manager

import com.intellij.xdebugger.breakpoints.XBreakpoint
import com.intellij.xdebugger.breakpoints.XBreakpointListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.*

class BreakpointListener: XBreakpointListener<XBreakpoint<*>> {
    override fun breakpointAdded(breakpoint: XBreakpoint<*>) {
        super.breakpointAdded(breakpoint)
        launchAddBreakpoint(breakpoint)
    }

    override fun breakpointRemoved(breakpoint: XBreakpoint<*>) {
        super.breakpointRemoved(breakpoint)
        launchRemoveBreakpoint(breakpoint)
    }

    private fun launchAddBreakpoint(breakpoint: XBreakpoint<*>){
        CoroutineScope(Dispatchers.IO).launch {Client.addBreakpoint(breakpoint)}
    }
    private fun launchRemoveBreakpoint(breakpoint: XBreakpoint<*>) {
        CoroutineScope(Dispatchers.IO).launch {Client.removeBreakpoint(breakpoint)}
    }
}