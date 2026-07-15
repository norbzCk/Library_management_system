package Model;

public class Author {
    private String authorId;
    private String authorName;
    private String biography;
    private String nationality;

    public Author(String authorId, String authorName, String biography, String nationality) {
        this.authorId = authorId;
        this.authorName = authorName;
        this.biography = biography;
        this.nationality = nationality;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getBiography() {
        return biography;
    }

    public String getNationality() {
        return nationality;
    }

    public String getAuthorInfo() {
        return "Author ID:" + authorId +
                "\nName: " + authorName +
                "\nNationality: " + nationality;
    }

    public void updateBiography(String bio) {
        biography = bio;
    }

    @Override
    public String toString() {
        return authorName;
    }
}
