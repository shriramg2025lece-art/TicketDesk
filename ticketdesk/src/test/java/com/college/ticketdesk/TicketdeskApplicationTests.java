package com.college.ticketdesk;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("h2demo")   // tests use the in-memory database, so MySQL is not required
class TicketdeskApplicationTests {

    @Test
    void contextLoads() {
    }
}
