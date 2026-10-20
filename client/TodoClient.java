import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class TodoClient {
    String host;
    int port;

    TodoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    List<String> ask(String command) {
        List<String> lines = new ArrayList<>();
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()))) {
            out.println(command);
            String line;
            while ((line = in.readLine()) != null && !line.equals("END"))
                lines.add(line);
        } catch (IOException e) {
            return null;
        }
        return lines;
    }

    String message(String command) {
        List<String> reply = ask(command);
        if (reply == null) return "Server unreachable.";
        return reply.isEmpty() ? "Empty reply." : reply.getFirst();
    }

    String add(String title) { return message("ADD " + title); }
    String toggle(Task t)    { return message("TOGGLE " + t.id); }
    String delete(Task t)    { return message("DELETE " + t.id); }
    String clearDone()       { return message("CLEAR"); }

    List<Task> list() {
        List<Task> tasks = new ArrayList<>();
        List<String> reply = ask("LIST");
        if (reply == null) return tasks;
        for (String line : reply) {
            String[] part = line.split("\\|", 3);
            Task t = new Task(Integer.parseInt(part[0]), part[2]);
            t.done = part[1].equals("true");
            tasks.add(t);
        }
        return tasks;
    }
}
