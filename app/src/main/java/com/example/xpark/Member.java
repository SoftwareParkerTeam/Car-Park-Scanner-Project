package com.example.xpark;

public class Member
{
    private String email;
    private String pass;

    public Member(String email, String pass) {
        this.email = email;
        this.pass = pass;
    }

    public void setEmail(String x) { this.email = x; }
    public void setPass(String x) { this.pass = x; }
    public String getEmail() { return this.email; }
    public String getPass() {return this.pass; }

    @Override
    public String toString()
    {
        return  email + " " + pass;
    }
}
