package com.example.ezrodriguez.bibliotecaaltice.entity;

/**
 * Created by Ez. Rodriguez on 11/13/2017.
 */

public class Book {

    private String autor;
    private String body;
    private int quantity;
    private int quantitySales;
    private long rental;
    private long price;
    private String resumen;
    private String title;
    private String url;

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantitySales() {
        return quantitySales;
    }

    public void setQuantitySales(int quantitySales) {
        this.quantitySales = quantitySales;
    }

    public long getRental() {
        return rental;
    }

    public void setRental(long rental) {
        this.rental = rental;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
