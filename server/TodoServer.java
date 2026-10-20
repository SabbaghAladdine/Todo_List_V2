import java.io.*;
import java.net.*;

public class TodoServer {
    static final int PORT = 5050;

    public static void main(String[] args) throws IOException {
        TodoList todo = TodoList.load();
        ServerSocket server = new ServerSocket(PORT);
        System.out.println("Server ready on port " + PORT);
        while (true) {
            try (Socket socket = server.accept();
                 BufferedReader in = new BufferedReader(
                         new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                handle(todo, in.readLine(), out);
            } catch (Exception e) {
                System.out.println("Request failed: " + e);
            }
        }
    }

    static void handle(TodoList todo, String line, PrintWriter out) {
        if (line == null) return;
        String[] part = line.split(" ", 2);
        String arg = part.length > 1 ? part[1] : "";
        switch (part[0]) {
            case "ADD" -> out.println(todo.add(arg.trim()));
            case "LIST" -> {
                for (Task t : todo.tasks)
                    out.println(t.id + "|" + t.done + "|" + t.title);
                out.println("END");
            }
            case "TOGGLE" -> {
                Task t = todo.find(Integer.parseInt(arg));
                out.println(t == null ? "Unknown task." : todo.toggle(t));
            }
            case "DELETE" -> {
                Task t = todo.find(Integer.parseInt(arg));
                out.println(t == null ? "Unknown task." : todo.delete(t));
            }
            case "CLEAR" -> out.println(todo.clearDone());
            default -> out.println("Unknown command.");
        }
    }
}