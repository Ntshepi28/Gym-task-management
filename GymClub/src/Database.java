import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private List<Task> tasks;
    private Connection conn;
    private Statement stmt;

    public Database() {
        tasks = new ArrayList<>();
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:tasks.db");
            stmt = conn.createStatement();
            createTable();
            updateRegisteredUsersTable();
            createRegisteredUsersTable();
            loadTasksFromDatabase();
            System.out.println("Database connection established successfully.");
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found: " + e.getMessage());
            conn = null;
        } catch (SQLException e) {
            System.out.println("SQL Error connecting to database: " + e.getMessage());
            conn = null;
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
            conn = null;
        }
    }

    private void createTable() {
        if (conn == null) return;
        String query = "CREATE TABLE IF NOT EXISTS tasks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "task_name TEXT, " +
                "description TEXT, " +
                "category TEXT, " +
                "start_date TEXT, " +
                "due_date TEXT, " +
                "created_time TEXT, " +
                "is_completed INTEGER DEFAULT 0)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(query);
        } catch (SQLException e) {
            System.out.println("Error creating tasks table: " + e.getMessage());
        }
    }

    private void updateRegisteredUsersTable() {
        if (conn == null) return;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS registered_users");
            stmt.execute("CREATE TABLE IF NOT EXISTS registered_users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "surname TEXT NOT NULL, " +
                    "id_number TEXT NOT NULL, " +
                    "age INTEGER NOT NULL, " +
                    "cell_number TEXT NOT NULL, " +
                    "address TEXT NOT NULL, " +
                    "occupation TEXT)");
            System.out.println("registered_users table updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error updating registered_users table: " + e.getMessage());
        }
    }

    private void createRegisteredUsersTable() {
        if (conn == null) return;
        String query = "CREATE TABLE IF NOT EXISTS registered_users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "surname TEXT NOT NULL, " +
                "id_number TEXT NOT NULL, " +
                "age INTEGER NOT NULL, " +
                "cell_number TEXT NOT NULL, " +
                "address TEXT NOT NULL, " +
                "occupation TEXT)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(query);
            System.out.println("registered_users table created or already exists.");
        } catch (SQLException e) {
            System.out.println("Error creating registered_users table: " + e.getMessage());
        }
    }

    public void saveRegisteredUser(String name, String surname, String idNumber, int age, String cellNumber, String address, String occupation) {
        if (conn == null) return;
        try (PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO registered_users (name, surname, id_number, age, cell_number, address, occupation) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            pstmt.setString(1, name);
            pstmt.setString(2, surname);
            pstmt.setString(3, idNumber);
            pstmt.setInt(4, age);
            pstmt.setString(5, cellNumber);
            pstmt.setString(6, address);
            pstmt.setString(7, occupation);
            pstmt.executeUpdate();
            System.out.println("User saved successfully: " + name + " " + surname);
        } catch (SQLException e) {
            System.out.println("Error saving registered user: " + e.getMessage());
        }
    }

    private void displayRegisteredUsers() {
        if (conn == null) return;
        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM registered_users");
            if (!rs.next()) {
                System.out.println("No users found in the table.");
            } else {
                do {
                    System.out.println("Name: " + rs.getString("name"));
                    System.out.println("Surname: " + rs.getString("surname"));
                    System.out.println("ID Number: " + rs.getString("id_number"));
                    System.out.println("Age: " + rs.getInt("age"));
                    System.out.println("Cell Number: " + rs.getString("cell_number"));
                    System.out.println("Address: " + rs.getString("address"));
                    System.out.println("Occupation: " + rs.getString("occupation"));
                    System.out.println("----------------------");
                } while (rs.next());
            }
        } catch (SQLException e) {
            System.out.println("Error displaying registered users: " + e.getMessage());
        }
    }

    public boolean userExists(String username, String surname) {
        if (conn == null) return false;
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM registered_users WHERE name = ?")) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error checking if user exists: " + e.getMessage());
            return false;
        }
    }

    private void loadTasksFromDatabase() {
        if (conn == null) return;
        String query = "SELECT * FROM tasks";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                boolean isCompleted = rs.getInt("is_completed") == 1;
                Task task = new Task(
                        rs.getInt("id"),
                        rs.getString("task_name"),
                        rs.getString("description"),
                        rs.getString("category"),
                        isCompleted
                );
                task.setStartDate(LocalDate.parse(rs.getString("start_date")));
                task.setDueDate(LocalDate.parse(rs.getString("due_date")));
                task.setCreatedTime(LocalTime.parse(rs.getString("created_time")));
                tasks.add(task);
            }
        } catch (SQLException e) {
            System.out.println("Error loading tasks from database: " + e.getMessage());
        }
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void addTask(Task task) {
        tasks.add(task);
        if (conn == null) return;
        try (PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO tasks (task_name, description, category, start_date, due_date, created_time, is_completed) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            pstmt.setString(1, task.getTaskName());
            pstmt.setString(2, task.getDescription());
            pstmt.setString(3, task.getCategory());
            pstmt.setString(4, task.getStartDate().toString());
            pstmt.setString(5, task.getDueDate().toString());
            pstmt.setString(6, task.getCreatedTime().toString());
            pstmt.setInt(7, task.isCompleted() ? 1 : 0);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error adding task to database: " + e.getMessage());
        }
    }

    public void deleteTask(Task task) {
        tasks.remove(task);
        if (conn == null) return;
        try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM tasks WHERE task_name = ?")) {
            pstmt.setString(1, task.getTaskName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting task from database: " + e.getMessage());
        }
    }

    public Task getTaskByName(String taskName) {
        if (conn == null) return null;
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM tasks WHERE task_name = ?")) {
            pstmt.setString(1, taskName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                boolean isCompleted = rs.getInt("is_completed") == 1;
                Task task = new Task(
                        rs.getInt("id"),
                        rs.getString("task_name"),
                        rs.getString("description"),
                        rs.getString("category"),
                        isCompleted
                );
                task.setStartDate(LocalDate.parse(rs.getString("start_date")));
                task.setDueDate(LocalDate.parse(rs.getString("due_date")));
                task.setCreatedTime(LocalTime.parse(rs.getString("created_time")));
                return task;
            } else {
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving task by name: " + e.getMessage());
            return null;
        }
    }

    public boolean deleteTaskByName(String taskNameToDelete) {
        if (conn == null) {
            System.out.println("Database connection is null. Cannot delete task.");
            return false;
        }

        System.out.println("Task name to delete: " + taskNameToDelete);
        String query = "SELECT * FROM tasks WHERE task_name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, taskNameToDelete);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("Task found in the database.");
                query = "DELETE FROM tasks WHERE task_name = ?";
                try (PreparedStatement pstmt2 = conn.prepareStatement(query)) {
                    pstmt2.setString(1, taskNameToDelete);
                    pstmt2.executeUpdate();
                    return true;
                }
            } else {
                System.out.println("Task not found in the database.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error deleting task: " + e.getMessage());
            return false;
        }
    }

    public void closeConnection() {
        try {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
            conn = null;
            System.out.println("Database connection closed successfully.");
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }
}
