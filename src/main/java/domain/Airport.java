package domain;

public class Airport {
    private String code;
    private String name;
    private Address address;
    private Boolean isWorked;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isWorked() {
        return isWorked;
    }

    public void setWorked(Boolean worked) {
        isWorked = worked;
    }
}
