package team.travel.travelplanner.entity;

import jakarta.persistence.*;

@Entity
public class Reviews {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private int rating;
    private String name;

    @Embedded
    private Text text;

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


    public static class Text{

        // Google's review character limit is 4096
        @Column(length = 4100)
        private String text;

        @Override
        public String toString(){
            return text;
        }
    }
}
