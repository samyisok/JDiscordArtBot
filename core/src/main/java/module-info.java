module ru.sarahbot.sarah {
    requires transitive utils;

    // Spring Boot dependencies
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.web;
    requires spring.webflux;
    requires spring.data.jpa;
    requires spring.beans;
    requires spring.core;
    requires spring.aop;
    requires spring.tx;

    // Reactor modules for Spring WebFlux
    requires reactor.core;
    requires io.netty.common;
    requires io.netty.buffer;
    requires io.netty.transport;
    requires io.netty.handler;
    requires io.netty.codec;
    requires io.netty.codec.http;

    // JDA dependencies
    requires net.dv8tion.jda;
    requires org.slf4j;

    // Java standard modules
    requires java.sql;
    requires java.desktop;
    requires java.net.http;

    // Jakarta EE modules
    requires jakarta.persistence;
    requires jakarta.annotation;

    // Internal module dependencies
    // requires ru.utils.name;

    // For annotations support
    requires static lombok;

    // Export all packages for Spring component scanning
    exports ru.sarahbot.sarah;
    exports ru.sarahbot.sarah.config;
    exports ru.sarahbot.sarah.exception;
    exports ru.sarahbot.sarah.file.dto;
    exports ru.sarahbot.sarah.file.repository;
    exports ru.sarahbot.sarah.file.service;
    exports ru.sarahbot.sarah.service;
    exports ru.sarahbot.sarah.service.generator;

    // Open packages for Spring to perform reflection
    opens ru.sarahbot.sarah;
    opens ru.sarahbot.sarah.config;
    opens ru.sarahbot.sarah.exception;
    opens ru.sarahbot.sarah.file.dto;
    opens ru.sarahbot.sarah.file.repository;
    opens ru.sarahbot.sarah.file.service;
    opens ru.sarahbot.sarah.service;
    opens ru.sarahbot.sarah.service.generator;
}
