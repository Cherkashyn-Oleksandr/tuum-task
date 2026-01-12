package com.tuum.tuumtask;

import org.springframework.boot.SpringApplication;

public class TestTuumTaskApplication {

    public static void main(String[] args) {
        SpringApplication.from(TuumTaskApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
