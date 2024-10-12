package pl.kwojcik.project_lab.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

class CacheResultServiceTest {
    private AtomicInteger executionCount = new AtomicInteger(0);

    @BeforeEach
    void beforeEach() {
        executionCount = new AtomicInteger(0);
    }

    @Test
    void should_return_new_result() {
        //given
        var service = new CacheResultService<String, Integer>();

        //when
        service.executeOrGetCached("bob", this::countLength);
        
        //then
        assert executionCount.get() == 1;
    }


    @Test
    void should_return_cached_result() {
        //given
        var service = new CacheResultService<String, Integer>();

        //when
        service.executeOrGetCached("bob", this::countLength);
        service.executeOrGetCached("bob", this::countLength);

        //then
        assert executionCount.get() == 1;
    }

    @Test
    void should_return_new_result_while_having_cached_different_key() {
        //given
        var service = new CacheResultService<String, Integer>();

        //when
        service.executeOrGetCached("bob", this::countLength);
        service.executeOrGetCached("alice", this::countLength);

        //then
        assert executionCount.get() == 2;
    }

    @Test
    void should_return_cached_value_while_having_multiple_keys() {
        //given
        var service = new CacheResultService<String, Integer>();

        //when
        service.executeOrGetCached("bob", this::countLength);
        service.executeOrGetCached("alice", this::countLength);
        service.executeOrGetCached("george", this::countLength);
        service.executeOrGetCached("alice", this::countLength);

        //then
        assert executionCount.get() == 3;
    }

    @Test
    void should_return_new_value_while_having_multiple_keys() {
        //given
        var service = new CacheResultService<String, Integer>();

        //when
        service.executeOrGetCached("bob", this::countLength);
        service.executeOrGetCached("alice", this::countLength);
        service.executeOrGetCached("george", this::countLength);
        service.executeOrGetCached("alice", this::countLength);
        service.executeOrGetCached("bradley", this::countLength);

        //then
        assert executionCount.get() == 4;
    }

    private Integer countLength(String key) {
        executionCount.incrementAndGet();
        return key.length();
    }

}