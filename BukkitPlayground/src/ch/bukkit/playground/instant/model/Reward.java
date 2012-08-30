package ch.bukkit.playground.instant.model;

public class Reward {

    private int id;
    private int quantity;

    public Reward() {
    }

    public Reward(int id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
