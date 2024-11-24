package server;

import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import llamarag.LlamaRAG;

public class DataEvaluateServer {

    LlamaRAG llamaRAG;

    public static void main(String[] args) throws Exception {
        DataEvaluateServer main = new DataEvaluateServer();
        main.run();
    }

    public void run() throws Exception {

        System.out.println("Launching server...");
        llamaRAG = new LlamaRAG();
        Server server = new Server(8383);

        // Setting up servlet context
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContextHandler.setContextPath("/");

        // Add form handling servlet
        servletContextHandler.addServlet(new ServletHolder(new FormHandlerServlet(llamaRAG)), "/submit");

        // Add stop servlet
        servletContextHandler.addServlet(new ServletHolder(new StopServlet(server)), "/stop");

        // Add resource handler for static content
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase("./public");
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[] { "index.html" });

        // Combine handlers
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new org.eclipse.jetty.server.Handler[] { resourceHandler, servletContextHandler });

        server.setHandler(handlers);

        // Start the server
        server.start();
        server.join();
        System.out.println("Server is running...");

    }

    public class StopServlet extends HttpServlet {
        private static final long serialVersionUID = 1L;
        private final Server server;

        public StopServlet(Server server) {
            this.server = server;
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.getWriter().write("Server stopping...");
            new Thread(() -> {
                try {
                    server.stop();
                    System.out.println("Stopped.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

}