package ch.bukkit.playground.instant.model;

import java.util.logging.Logger;

public class Reward implements Validataeble {

    private final static Logger logger = Logger.getLogger("Reward");

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
        if (money < 1) {
            if (id < 1) {
                logger.warning("Reward ID must be bigger then 0 and a valid minecraft item id");
                return false;
            }
            if (quantity < 1 || quantity > 64) {
                logger.warning("Reward Quantity must be between 0 and 64");
                return false;
            }
            return true;
        } else if (money > 0) {
            return true;
        }

        logger.warning("Money must be bigger then 1, if no item reward is set.");
        return false;
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
