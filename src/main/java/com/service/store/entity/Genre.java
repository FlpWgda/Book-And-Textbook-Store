package com.service.store.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;


public class Genre   {
    public static List<Genre> genres = new ArrayList<Genre>();

    public Genre() {
    }

    @JsonProperty("name")
    @Id
    @Column(length = 50)
    private String genreName = null;

    public Genre(String genreName) {
        this.genreName = genreName;
    }

    public Genre name(String name) {
        this.genreName = name;
        return this;
    }

    /**@ManyToMany(mappedBy = "genre")
    private List<Publication> publications;

    public List<Publication> getPublications() {
    return publications;
    }

    public void setPublications(List<Publication> publications) {
    this.publications = publications;
    }**/

    /**
     * Get name
     * @return name
     **/

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Genre genre = (Genre) o;
        return Objects.equals(this.genreName, genre.genreName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(genreName);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Genre {\n");

        sb.append("    name: ").append(toIndentedString(genreName)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
