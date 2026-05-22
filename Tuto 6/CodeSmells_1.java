public class CodeSmells_1 {

    public static void main(String[] args) {
        Address address = new Address(
                "Paul-Henri Spaaklaan 1",
                "Maastricht",
                "NL",
                "6229 EN"
        );

        Customer customer = new Customer("John Doe", address);
        Order order = new Order(customer);

        order.printShippingLabel();
    }
}

class Address {

    private String street;
    private String city;
    private String state;
    private String zipCode;

    public Address(String street, String city, String state, String zipCode) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    // Address formats its own data, so other classes do not need many getters.
    public String getFormattedAddress() {
        return "Street: " + street + "\n"
                + "City: " + city + "\n"
                + "State: " + state + "\n"
                + "ZipCode: " + zipCode;
    }
}

class Customer {

    private String name;
    private Address address;

    public Customer(String name, Address address) {
        this.name = name;
        this.address = address;
    }

    // Customer creates the full label instead of Order reaching through its fields.
    public String getShippingLabel() {
        return "Name: " + name + "\n" + address.getFormattedAddress();
    }
}

class Order {

    private Customer customer;

    public Order(Customer customer) {
        this.customer = customer;
    }

    public void printShippingLabel() {
        System.out.println(customer.getShippingLabel());
    }
}
