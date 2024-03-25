package ru.practicum.shareit;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class CommonPageRequest extends PageRequest {

    private final int from;

    public CommonPageRequest(int from, int size) {
        super(from / size, size, Sort.unsorted());
        this.from = from;
    }

    @Override
    public long getOffset() {
        return from;
    }
}
