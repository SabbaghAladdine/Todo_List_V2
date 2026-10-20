import java.io.Serializable;

public class TaskC implements Serializable {
    int id;
    String title;

    boolean done = false;

    TaskC(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public String toString() {
        return (done ? "[x]  " : "[  ]  ") + title;
    }
}
