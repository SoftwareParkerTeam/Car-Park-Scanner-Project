public class User {
    /** User's Name */
    private String name;
    /** User's Surname */
    private String surName;
    /** User's Nick Name */
    private String nick_name;
    /** User's Password */
    private String password;
    /** User's Phone  */
    private String phone;
    /** User's Email */
    private String eMail;
    /** User's Credit Balance */
    private double credit_balance;

    /**
     * Builds a user object
     */
    public User(){
        this.name = null;
        this.surName = null;
        this.nick_name = null;
        this.password = null;
        this.phone = null;
        this.eMail = null;
        this.credit_balance = 0.0;
    }

    /**
     * Builds a user object with given information.
     * @param name_ User's name
     * @param surName_ User's surname
     * @param nick_name_ User's nick name
     * @param password_ User's password
     * @param phone_ User's phone
     * @param eMail_ User's email
     * @param credit_balance_ User's credit balance
     */
    public User(String name_, String surName_,String nick_name_,String password_, String phone_, String eMail_, double credit_balance_){
        this.name = name_;
        this.surName = surName_;
        this.nick_name = nick_name_;
        this.password = password_;
        this.phone = phone_;
        this.eMail = eMail_;
        this.credit_balance = credit_balance_;
    }

    /**
     * Builds a user object with given information.
     * @param nick_name_ User's nick name
     * @param password_ User's password
     */
    public User(String nick_name_, String password_){
        this.name = null;
        this.surName = null;
        this.nick_name = nick_name_;
        this.password = password_;
        this.phone = null;
        this.eMail = null;
        this.credit_balance = 0.0;
    }

    /**
     * Setter name
     * @param name given User's name
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * Setter nick_name
     * @param nick_name given User's nick name
     */
    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }
    /**
     * Setter password
     * @param password given User's password
     */
    public void setPassword(String password) {
        this.password = password;
    }
    /**
     * Setter surname
     * @param surName given User's surname
     */
    public void setSurName(String surName) {
        this.surName = surName;
    }
    /**
     * Setter phone
     * @param phone given User's phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }
    /**
     * Setter email
     * @param eMail given User's email
     */
    public void seteMail(String eMail) {
        this.eMail = eMail;
    }
    /**
     * Setter credit balance
     * @param credit_balance given User's credit balance
     */
    public void setCredit_balance(double credit_balance) {
        this.credit_balance = credit_balance;
    }
    /**
     * Getter name
     * @return string is user's name
     */
    public String getName() {
        return name;
    }
    /**
     * Getter nick_name
     * @return string is user's nick name
     */
    public String getNick_name() {
        return nick_name;
    }
    /**
     * Getter password
     * @return string is user's password
     */
    public String getPassword() {
        return password;
    }
    /**
     * Getter surname
     * @return string is user's surname
     */
    public String getSurName() {
        return surName;
    }
    /**
     * Getter email
     * @return string is user's email
     */
    public String geteMail() {
        return eMail;
    }
    /**
     * Getter phone
     * @return string is user's phone
     */
    public String getPhone() {
        return phone;
    }
    /**
     * Getter credit_balance
     * @return double is user's credit balance
     */
    public double getCredit_balance() {
        return credit_balance;
    }

    /**
     * Overridden toString method to show user's data information
     * @return string user's data information
     */
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", surName='" + surName + '\'' +
                ", nick_name='" + nick_name + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", eMail='" + eMail + '\'' +
                ", credit_balance=" + credit_balance +
                '}';
    }
}
