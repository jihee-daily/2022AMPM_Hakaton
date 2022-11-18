package kr.ac.jbnu.se.sentiment.diary;

public class Person {
    int num;
    String dates;
    int mood;
    String content;
    String pictureUri;
    int weather;


    public Person(int num, String dates, int mood, String content, String pictureUri, int weather) {
        this.num = num;
        this.dates = dates;
        this.mood = mood;
        this.content = content;
        this.weather = weather;

        if(pictureUri == null) {
        } else {
            this.pictureUri = pictureUri;
        }

    }

    public int getWeather() {
        return weather;
    }

    public void setWeather(int weather) {
        this.weather = weather;
    }

    public String getPictureUri() {
        return pictureUri;
    }

    public void setPictureUri(String pictureUri) {
        this.pictureUri = pictureUri;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }

    public int getMood() {
        return mood;
    }

    public void setMood(int mood) {
        this.mood = mood;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

