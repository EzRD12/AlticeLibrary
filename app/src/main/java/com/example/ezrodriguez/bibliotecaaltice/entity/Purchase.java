package com.example.ezrodriguez.bibliotecaaltice.entity;

/**
 * Created by Ez. Rodriguez on 11/18/2017.
 */

public class Purchase {

    private String key;
    private String book_title;
    private String book_autor;
    private String user_key;

    public String getBook_title() {
        return book_title;
    }

    public void setBook_title(String book_title) {
        this.book_title = book_title;
    }

    public String getBook_autor() {
        return book_autor;
    }

    public void setBook_autor(String book_autor) {
        this.book_autor = book_autor;
    }

    public String getUser_key() {
        return user_key;
    }

    public void setUser_key(String user_key) {
        this.user_key = user_key;
    }



}
