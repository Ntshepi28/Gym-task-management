import java.time.LocalDate;
import java.time.LocalTime;

public class Task {
    private int id;
    private String taskName;
    private String description;
    private String category;
    private LocalDate startDate;
    private LocalDate dueDate;
    private LocalTime createdTime;
    private boolean isCompleted; // Added task completion status
    private Registration registration;

    public Task(int id, String taskName, String description, String category, boolean isCompleted) {
        this.id = id;
        this.taskName = taskName;
        this.description = description;
        this.category = category;
        this.startDate = LocalDate.now();  // Set the start date to the current date
        this.dueDate = startDate.plusMonths(3);  // Set the due date to 3 months from now
        this.createdTime = LocalTime.now();  // Set the created time to the current time
        this.isCompleted = false; // By default, the task is not completed
    }

    // Updated constructor (no need for unnecessary overloaded constructors)
    public Task(int id, String taskName, String description, String category, LocalDate startDate, LocalDate dueDate, LocalTime createdTime, boolean isCompleted) {
        this.id = id;
        this.taskName = taskName;
        this.description = description;
        this.category = category;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.createdTime = createdTime;
        this.isCompleted = isCompleted;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public String getTaskName() {

        return taskName;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {

        return category;
    }

    public LocalDate getStartDate() {

        return startDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalTime getCreatedTime() {

        return createdTime;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setCreatedTime(LocalTime createdTime) {
        this.createdTime = createdTime;
    }

    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", startDate=" + startDate +
                ", dueDate=" + dueDate +
                ", createdTime=" + createdTime +
                ", isCompleted=" + isCompleted +
                '}';
    }

    public Registration getRegistration() {
        return registration;
    }

    public void setRegistration(Registration registration) {
        this.registration = registration;
    }
}

class Registration {
    private String name;
    private String surname;
    private String idNumber;
    private int age;
    private String cellNumber;
    private String address;
    private String occupation;

    public Registration(String name, String surname, String idNumber, int age, String cellNumber, String address, String occupation) {
        this.name = name;
        this.surname = surname;
        this.idNumber = idNumber;
        this.age = age;
        this.cellNumber = cellNumber;
        this.address = address;
        this.occupation = occupation;
    }

    // Getters and setters for registration fields...
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCellNumber() {
        return cellNumber;
    }

    public void setCellNumber(String cellNumber) {
        this.cellNumber = cellNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public int getID() {
        return 0;
    }

    public String getTasks() {
        return "";
    }
}

