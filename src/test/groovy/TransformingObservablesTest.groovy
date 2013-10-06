import domain.Response
import domain.Response2
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rx.Observable

import java.util.concurrent.Executor
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class TransformingObservablesTest {

    private Logger LOG = LoggerFactory.getLogger(RxJavaTest.class);
    private Executor executor = new ThreadPoolExecutor(10, 10, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());

    @Test
    void "map"() {
        Observable.create({ observer ->
            executor.execute({
                Thread.sleep(80);
                observer.onNext(callService("Call service"))
                observer.onNext(callService("Call service"))
                observer.onCompleted();
            })
        }).map({ new Response2(it.responseText) }).toBlockingObservable().forEach({ println(it) })
    }

    @Test
    void "mapWithIndex"() {
        Observable.create({ observer ->
            executor.execute({
                Thread.sleep(80);
                observer.onNext(callService("Call service"))
                observer.onNext(callService("Call service"))
                observer.onCompleted();
            })
        }).mapWithIndex({ response, index ->
            new Response2(response.responseText, index) }).toBlockingObservable().forEach({ response2 -> println(response2) })
    }

    def Response callService(String request) {
        LOG.debug("Calling service with request {}", request)
        new Response(request)
    }


}
