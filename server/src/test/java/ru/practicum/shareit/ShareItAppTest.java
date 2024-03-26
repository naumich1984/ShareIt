package ru.practicum.shareit;

import org.junit.jupiter.api.Test;

// Test class added ONLY to cover main() invocation not covered by application tests.
class ShareItAppTest {
    @Test
    public void main() {
        ShareItServer.main(new String[] {});
    }
}