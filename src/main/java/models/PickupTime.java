package models;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class PickupTime {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id, storeId;

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;
    private LocalTime startTime, endTime;

    private Weekdays weekday;

    public PickupTime() {

    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public PickupTime(LocalTime startTime, LocalTime endTime, Weekdays weekday) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.weekday = weekday;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Weekdays getWeekday() {
        return weekday;
    }

    public void setWeekday(Weekdays weekday) {
        this.weekday = weekday;
    }
}

