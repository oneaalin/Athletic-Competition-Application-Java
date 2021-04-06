package chat.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Entry extends Entity<Tuple<Long,Long>>{
    private LocalDateTime date ;
    private Challenge challenge;
    private Child child;

    /**
     *
     * @param date the date when the entry was made
     * @param challenge the challenge chosen by the child
     * @param child the child that participates at the challenge
     */
    public Entry(LocalDateTime date, Child child ,Challenge challenge) {

        this.date = date;
        this.child = child;
        this.challenge = challenge;
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

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public Child getChild() {
        return child;
    }

    public void setChild(Child child) {
        this.child = child;
    }

    @Override
    public String toString() {
        return getId().getLeft() + " - " + getId().getRight() + " = " + child.getName() + " " + child.getAge() + " - "
                + challenge.getMinimumAge() + " " + challenge.getMaximumAge() + " " + date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
