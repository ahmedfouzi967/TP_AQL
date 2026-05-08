package exercice2;

public class Order {
    private int id;
    private String product;
    private int quantity;

    public Order(int id, String product, int quantity) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
    }

    public int getId() { return id; }
    public String getProduct() { return product; }
    public int getQuantity() { return quantity; }

    @Override
    public String toString() {
        return "Order{id=" + id + ", product='" + product + "', quantity=" + quantity + "}";
    }
}
