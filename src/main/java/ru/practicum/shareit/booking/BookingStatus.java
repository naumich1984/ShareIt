package ru.practicum.shareit.booking;

public enum BookingStatus {

    ALL,
    APPROVED,
    CURRENT,
    FUTURE,
    PAST,
    WAITING,
    REJECTED;

    static BookingStatus from(String state) {
        if (state == null) {

            return BookingStatus.ALL;
        }
        for (BookingStatus value : BookingStatus.values()) {
            if (value.name().equals(state)) {

                return value;
            }
        }

        return null;
    }
}
