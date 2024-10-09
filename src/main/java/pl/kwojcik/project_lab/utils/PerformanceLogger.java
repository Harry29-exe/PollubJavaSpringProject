package pl.kwojcik.project_lab.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PerformanceLogger {
    //start L4 functional interface
    public interface FN<T> {
        T execute();
    }

    public static <T> T logExecutionTime(FN<T> fn) {
        var time0 = System.nanoTime();
        try {
            return fn.execute();
        } finally {
            log.info("Execution time: {} ms", (System.nanoTime() - time0) / 1_000_000);
        }
    }

}
