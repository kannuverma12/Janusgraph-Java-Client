package com.paytm.digital.education.utility.testmodel;

public class Person {
    private String name;
    private int age;
    private float height;
    private int amount;
    private long phone;

    public Person(String name, int age, float height, int amount, long phone) {
        this.name = name;
        this.age = age;
        this.height = height;
        this.amount = amount;
        this.phone = phone;
    }


    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public float getHeight() {
        return height;
    }

    public int getAmount() {
        return amount;
    }

    public long getPhone() {
        return phone;
    }
}
