package models;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Store extends User {
    @OneToMany
    private List<PickupTime> pickupTimes;

    public List<PickupTime> getPickupTimes() {
        return pickupTimes;
    }

    public void setPickupTimes(List<PickupTime> pickupTimes) {
        this.pickupTimes = pickupTimes;
    }

    public void addPickupTime(PickupTime pickupTime) {
        pickupTimes.add(pickupTime);
    }
}
