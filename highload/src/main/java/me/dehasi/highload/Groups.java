package me.dehasi.highload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** Created by Ravil on 17/12/2018. */
public class Groups {
   public List<Group> groups;

    @JsonCreator
    public Groups(@JsonProperty("groups")List<Group> groups) {
        this.groups = groups;
    }
}
