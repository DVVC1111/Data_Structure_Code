import java.io.Serializable;

public class Patient implements Serializable {
    private int id;
    private String name;
    private String condition;
    private String ticketType;
    private int order;
    private int seriousness;

    public Patient(int id, String name, String condition, String ticketType, int seriousness) {
        this.id = id;
        this.name = name;
        this.condition = condition;
        this.ticketType = ticketType;
        this.seriousness = seriousness;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public int getSeriousness() {
        return seriousness;
    }

    public void setSeriousness(int seriousness) {
        this.seriousness = seriousness;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", condition='" + condition + '\'' +
                ", ticketType='" + ticketType + '\'' +
                ", seriousness=" + seriousness +
                '}';
    }
}
