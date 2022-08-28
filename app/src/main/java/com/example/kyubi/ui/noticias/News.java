package com.example.kyubi.ui.noticias;

public class News {
    String URL,TITLE,DESCRIPTION;

    public News() {
    }

    public News(String TITLE, String DESCRIPTION, String URL) {
        this.URL = URL;
        this.DESCRIPTION = DESCRIPTION;
        this.TITLE = TITLE;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getTITLE() {
        return TITLE;
    }

    public void setTITLE(String TITLE) {
        this.TITLE = TITLE;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }
}
