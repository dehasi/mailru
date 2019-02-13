package me.dehasi.highload.controller;

/** Created by Ravil on 13/02/2019. */
public class FluentComparable<T extends Comparable<T>> {
    private final T value;

    public FluentComparable(T value) {
        this.value = value;
    }

    public static <T extends Comparable<T>> FluentComparable assertThatThis(T value) {
        return new FluentComparable(value);
    }

    public boolean isEqualTo(T value) {
        return this.value.compareTo(value) == 0;
    }

    public boolean isNotEqualTo(T value) {
        return !isEqualTo(value);
    }

    public boolean isLessThat(T value) {
        return this.value.compareTo(value) < 0;
    }

    public boolean isGreatherThat(T value) {
        return this.value.compareTo(value) > 0;
    }

    public boolean isLessOrEqualTo(T value) {
        return this.value.compareTo(value) <= 0;
    }

    public boolean isGreatherOrEqualTo(T value) {
        return this.value.compareTo(value) >= 0;
    }
}
