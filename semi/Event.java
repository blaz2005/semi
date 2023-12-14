public class Event {
    private String eventName;
    private String eventTime;

    public Event(String eventName, String eventTime) {
        this.eventName = eventName;
        this.eventTime = eventTime;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventTime() {
        return eventTime;
    }

    @Override
    public String toString() {
        return eventName;
    }
}
