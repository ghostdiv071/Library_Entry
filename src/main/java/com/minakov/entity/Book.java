package com.minakov.entity;

public class Book extends Entity {

    private String name;
    private String author;
    private String genre;
    private String publisher;
    private int year;
    private String city;

    public Book() {
    }

    public Book(String name, String author, String genre, String publisher,
                int year, String city)
    {
        this.name = name;
        this.author = author;
        this.genre = genre;
        this.publisher = publisher;
        this.year = year;
        this.city = city;
    }

    public Book(long id, String name, String author, String genre, String publisher,
                int year, String city)
    {
        setId(id);
        this.name = name;
        this.author = author;
        this.genre = genre;
        this.publisher = publisher;
        this.year = year;
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", genre='" + genre + '\'' +
                ", publisher='" + publisher + '\'' +
                ", year=" + year +
                ", city='" + city + '\'' +
                '}';
    }
}
