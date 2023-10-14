package admindashboard;


import javafx.beans.property.*;

public class ClientData {

    private final IntegerProperty id;
    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty gender;
    private final StringProperty dob;
    private final StringProperty email;
    private final StringProperty accType;
    private final LongProperty accNum;
    private final LongProperty balance;

    public ClientData(int id, String firstName, String lastName, String dob, String gender, String email, String accType, long accNum, long balance) {
        this.id = new SimpleIntegerProperty(id);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.gender = new SimpleStringProperty(gender);
        this.dob = new SimpleStringProperty(dob);
        this.email = new SimpleStringProperty(email);
        this.accType = new SimpleStringProperty(accType);
        this.accNum = new SimpleLongProperty(accNum);
        this.balance = new SimpleLongProperty(balance);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getFirstName() {
        return firstName.get();
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public String getLastName() {
        return lastName.get();
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public String getGender() {
        return gender.get();
    }

    public StringProperty genderProperty() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender.set(gender);
    }

    public String getDob() {
        return dob.get();
    }

    public StringProperty dobProperty() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob.set(dob);
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public String getAccType() {
        return accType.get();
    }

    public StringProperty accTypeProperty() {
        return accType;
    }

    public void setAccType(String accType) {
        this.accType.set(accType);
    }

    public long getAccNum() {
        return accNum.get();
    }

    public LongProperty accNumProperty() {
        return accNum;
    }

    public void setAccNum(long accNum) {
        this.accNum.set(accNum);
    }

    public long getBalance() {
        return balance.get();
    }

    public LongProperty balanceProperty() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance.set(balance);
    }
}
