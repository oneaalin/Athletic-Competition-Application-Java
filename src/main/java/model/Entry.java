package model;

import java.time.LocalDateTime;

public class Entry extends Entity<Tuple<Long,Long>>{
    private LocalDateTime date ;

    /**
     * constructor
     * @param date the date when the entry was made
     */
    public Entry(LocalDateTime date) {
        this.date = date;
    }

    /**
     * return the date when the entry was made
     * @return date LocalDateTime
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * set the date when the entry was made
     * @param date the date when the entry was made
     */
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
