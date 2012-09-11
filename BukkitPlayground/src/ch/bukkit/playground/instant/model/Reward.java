package ch.bukkit.playground.instant.model;

public class Reward implements Validataeble {

    private int id;
    private int quantity;
    private int money;

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

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    @Override
    public boolean checkValidity() {
        return (id > 0 && quantity > 0) || money > 0;
    }

    @Override
    public String toString() {
        return "Reward{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", money=" + money +
                '}';
    }
}
