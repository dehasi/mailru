package me.dehasi.highload;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Arrays;
import java.util.Objects;

/** Created by Ravil on 17/12/2018. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Group {
    public String sex;
    public String status;
    public String[] interests;
    public String country;
    public String city;
    public long count;

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Group))
            return false;
        Group group = (Group)o;
        return count == group.count &&
            Objects.equals(sex, group.sex) &&
            Objects.equals(status, group.status) &&
            Arrays.equals(interests, group.interests) &&
            Objects.equals(country, group.country) &&
            Objects.equals(city, group.city);
    }

    @Override public int hashCode() {
        int result = Objects.hash(sex, status, country, city, count);
        result = 31 * result + Arrays.hashCode(interests);
        return result;
    }

    @Override public String toString() {
        return "Group{" +
            "sex='" + sex + '\'' +
            ", status='" + status + '\'' +
            ", interests=" + Arrays.toString(interests) +
            ", country='" + country + '\'' +
            ", city='" + city + '\'' +
            ", count=" + count +
            '}';
    }
}
