package models;

/**
 * Адрес организации (почтовый индекс).
 */
public class Address {

    private String zipCode;

    /** Создаёт адрес. */
    public Address(String zipCode) {

        if (zipCode != null && zipCode.length() > 19) {
            throw new IllegalArgumentException("zipCode cannot be longer than 19 characters");
        }
        this.zipCode = zipCode;

    }
    @Override
    public String toString() {
        return "Address {zipCode='" + zipCode + "'}";
    }

}
