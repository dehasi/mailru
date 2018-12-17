package me.dehasi.highload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Arrays;

/** Created by Ravil on 16/12/2018. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Account {
    public int id;
    public String email;
    public String fname;
    public String sname;
    public String phone;
    public String sex;
    public long birth;
    public String country;
    public String city;

    public long joined;
    public String status;
    public String[] interests;
    public Premium premium;
    public Like[] likes;

    public static class Premium {
        public long start;
        public long finish;

        @Override public String toString() {
            return "Premium{" +
                "start=" + start +
                ", finish=" + finish +
                '}';
        }
    }

    public static class Like {
        public int id;
        public long ts;

        @Override public String toString() {
            return "Like{" +
                "id=" + id +
                ", ts=" + ts +
                '}';
        }
    }

    @Override public String toString() {
        return "Account{" +
            "id=" + id +
            ", email='" + email + '\'' +
            ", fname='" + fname + '\'' +
            ", sname='" + sname + '\'' +
            ", phone='" + phone + '\'' +
            ", sex='" + sex + '\'' +
            ", birth=" + birth +
            ", country='" + country + '\'' +
            ", city='" + city + '\'' +
            ", joined=" + joined +
            ", status='" + status + '\'' +
            ", interests=" + Arrays.toString(interests) +
            ", premium=" + premium +
            ", likes=" + Arrays.toString(likes) +
            '}';
    }
}
