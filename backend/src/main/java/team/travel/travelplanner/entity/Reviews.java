package team.travel.travelplanner.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class Reviews {

    @Id
    @GeneratedValue
    private Long id;
    private int rating;
    private String name;

    @JsonIgnore
    @ManyToOne
    private GasStation gasStation;
    @Embedded
    private Text text;

    public Reviews(int rating, String name, GasStation gasStation, Text text) {
        this.rating = rating;
        this.name = name;
        this.gasStation = gasStation;
        this.text = text;
    }

    public Reviews() {

    }

    @Override
    public String toString() {
        return "Reviews{" +
                "text=" + //text.toString() +
                ", rating=" + rating +
                ", name='" + name + '\'' +
                '}';
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName(){
        return name;
    }

    public int getRating(){
        return rating;
    }

    public void setRating(int rating){
        this.rating = rating;
    }

    public void setName(String name){
        this.name = name;
    }

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }

    public GasStation getGasStation() {
        return gasStation;
    }

    public void setGasStation(GasStation gasStation) {
        this.gasStation = gasStation;
    }

    public static class Text{

        // Google's review character limit is 4096
        @Column(length = 4100)
        private String text;

        @Override
        public String toString(){
            return text;
        }

        public String getText(){
            return text;
        }

        public void setText(String text){
            this.text = text;
        }
    }
}
