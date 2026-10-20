import javax.swing.*;
import java.awt.*;


public class Main extends JFrame {
    TodoClient todo = new TodoClient("localhost", 5050);

    DefaultListModel<Task> model = new DefaultListModel<>();
    JList<Task> list = new JList<>(model);
    JTextField titleField = new JTextField();
    JLabel status = new JLabel(" ");
    JComboBox<String> filter = new JComboBox<>(new String[]{"All", "Pending", "Done"});

    Main() {
        super("Todo App");

        JButton addButton = new JButton("Add");
        JPanel top = new JPanel(new BorderLayout(5, 5));
        top.add(titleField, BorderLayout.CENTER);
        top.add(addButton, BorderLayout.EAST);
        top.add(filter, BorderLayout.WEST);

        list.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        list.setCellRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> jlist, Object value,
                                                          int index, boolean selected, boolean focus) {
                super.getListCellRendererComponent(jlist, value, index, selected, focus);
                if (((Task) value).done && !selected) setForeground(Color.GRAY);
                return this;
            }
        });

        JButton toggleButton = new JButton("Done / Undo");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear completed");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(toggleButton);
        buttons.add(deleteButton);
        buttons.add(clearButton);
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(buttons, BorderLayout.CENTER);
        bottom.add(status, BorderLayout.SOUTH);

        JPanel content = new JPanel(new BorderLayout(5, 5));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.add(top, BorderLayout.NORTH);
        content.add(new JScrollPane(list), BorderLayout.CENTER);
        content.add(bottom, BorderLayout.SOUTH);
        setContentPane(content);

        addButton.addActionListener(e -> add());
        titleField.addActionListener(e -> add());
        toggleButton.addActionListener(e -> toggle());
        deleteButton.addActionListener(e -> delete());
        clearButton.addActionListener(e -> show(todo.clearDone()));
        filter.addActionListener(e -> refresh());

        refresh();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(450, 400);
        setLocationRelativeTo(null);
    }

    void add() {
        String message = todo.add(titleField.getText().trim());
        if (message.equals("Task added.")) titleField.setText("");
        show(message);
    }

    void toggle() {
        Task t = list.getSelectedValue();
        if (t == null) { status.setText("Select a task first."); return; }
        show(todo.toggle(t));
    }

    void delete() {
        Task t = list.getSelectedValue();
        if (t == null) { status.setText("Select a task first."); return; }
        show(todo.delete(t));
    }

    void show(String message) {
        refresh();
        status.setText(message);
    }

    void refresh() {
        int selected = list.getSelectedIndex();
        model.clear();
        String choice = (String) filter.getSelectedItem();
        for (Task t : todo.list()) {
            if (choice.equals("Pending") && t.done) continue;
            if (choice.equals("Done") && !t.done) continue;
            model.addElement(t);
        }        if (selected >= 0 && selected < model.size()) list.setSelectedIndex(selected);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
