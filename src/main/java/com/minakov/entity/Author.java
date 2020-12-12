package com.minakov.entity;

public class Author extends Entity {

    private String name;
    private String surname;
    private String secondName;

    public Author() {
    }

    public Author(String name, String surname, String secondName) {
        this.name = name;
        this.surname = surname;
        this.secondName = secondName;
    }

    public Author(long id, String name, String surname, String secondName) {
        setId(id);
        this.name = name;
        this.surname = surname;
        this.secondName = secondName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    @Override
    public String toString() {
        return name + " " + surname;
    }
}
