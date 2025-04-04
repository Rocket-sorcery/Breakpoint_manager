package org.example.server

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress

object Server {
    private var server: HttpServer? = null
    private var breakpointList: MutableList<String> = mutableListOf()
    private var pageUpdateFlag = true
    private var html: String = ""

    private var counter = 0

    fun start(port: Int) {
        server = HttpServer.create(InetSocketAddress("127.0.0.1", port), 0)
        server!!.createContext("/getPage", PageSender())
        server!!.createContext("/addBreakpoint", BreakpointInserter())
        server!!.createContext("/removeBreakpoint", BreakpointRemover())
        server!!.createContext("/replaceList", BreakpointListUpdate())
        server!!.createContext("/shutdown", ServerSwitch())
        server!!.executor = null
        server!!.start()
    }

    fun stop() {
        server?.stop(0)
    }

    private class PageSender: HttpHandler {
        override fun handle(exchange: HttpExchange?) {
            if (pageUpdateFlag) {
                updatePage()
            }

            exchange!!.sendResponseHeaders(200, html.length.toLong())
            exchange.responseBody.use { os -> os.write(html.toByteArray()) }
        }
    }

    private fun updatePage() {
        html = """
            <html>
            <body style="color:white">
                <div style="width: 90%; border-radius: 5px; align-items: center;">
                    <h2>Breakpoints: ${breakpointList.size}</h2>
                    <ul style="list-style:none">${breakpointList.joinToString("") { "<li>$it</li>" }}</ul>
                </div>
            </body>
            </html>
        """.trimIndent()
        pageUpdateFlag = false
    }

    private class BreakpointInserter: HttpHandler {
        override fun handle(exchange: HttpExchange?) {
            val requestBody = exchange!!.requestBody.bufferedReader().readText()
            breakpointList.add(requestBody)
            pageUpdateFlag = true
            counter++
            exchange.sendResponseHeaders(200, 0)
            exchange.responseBody.close()
        }
    }
    private class BreakpointRemover: HttpHandler {
        override fun handle(exchange: HttpExchange?) {
            val requestBody = exchange!!.requestBody.bufferedReader().readText()
            breakpointList.remove(requestBody)
            pageUpdateFlag = true
            counter++
            exchange.sendResponseHeaders(200, 0)
            exchange.responseBody.close()
        }
    }
    private class BreakpointListUpdate: HttpHandler {
        override fun handle(exchange: HttpExchange?) {
            val requestBody = exchange!!.requestBody.bufferedReader().readText()
            breakpointList.clear()
            breakpointList.addAll(requestBody.split('\n'))
            pageUpdateFlag = true
            exchange.sendResponseHeaders(200, 0)
            exchange.responseBody.close()
        }
    }

    private class ServerSwitch: HttpHandler {
        override fun handle(exchange: HttpExchange?) {
            exchange!!.sendResponseHeaders(200, 0)
            stop()
        }
    }
}