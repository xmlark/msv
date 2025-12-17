package com.sun.msv.datatype.regexp;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * tests RangeToken.
 *
 * @author <a href="mailto:kirill.subbotin@tutanota.com">Kirill Subbotin</a>
 */
public class RangeTokenTest extends TestCase {

    public RangeTokenTest( String name ) { super(name); }

    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(RangeTokenTest.class);
    }

    public void testMatchUnderConcurrency() throws InterruptedException {
        RangeToken rangeToken = new RangeToken(Token.RANGE);

        rangeToken.addRange('<', '<');
        rangeToken.addRange('=', '=');
        rangeToken.addRange('>', '>');
        rangeToken.addRange('?', '?');
        rangeToken.addRange('@', '@');
        rangeToken.addRange('A', 'A'); // our match
        rangeToken.addRange('B', 'B');
        rangeToken.addRange('C', 'C');
        rangeToken.addRange('D', 'D');
        rangeToken.addRange('E', 'E');
        rangeToken.addRange('F', 'F');

        int threads = Runtime.getRuntime().availableProcessors() * 4;
        int runs =  threads * 1_000;

        ExecutorService executor = Executors.newFixedThreadPool(threads);

        try {
            List<CompletableFuture<Boolean>> futures = new ArrayList<>();

            for (int i = 0; i < runs; i++) {
                futures.add(CompletableFuture.supplyAsync(
                    () -> rangeToken.match('A'),
                    executor
                ));
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            boolean allMatched = futures.stream()
                .allMatch(CompletableFuture::join);

            assertTrue(allMatched);
        } finally {
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);
        }
    }

}
