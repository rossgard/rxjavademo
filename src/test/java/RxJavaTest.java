import domain.Request;
import domain.Response;
import domain.Response2;
import endpoint.Service1;
import endpoint.Service2;
import endpoint.Service3;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import rx.util.functions.Action1;
import rx.util.functions.Func1;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;

public class RxJavaTest {

    private static final Logger LOG = LoggerFactory.getLogger(RxJavaTest.class);
    private final Executor executor = new ThreadPoolExecutor(10, 10, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());

    private final Service1 service1 = new Service1();
    private final Service2 service2 = new Service2();
    private final Service3 service3 = new Service3();

    private static final boolean RETURN_RESPONSE = false;
    private static final boolean THROW_EXCEPTION = true;

    @Test
    public void testMerge() {
        final Observable<Response> observable = Observable.from(
                new Response("Response from service 1"),
                new Response("Response from service 2"),
                new Response("Response from service 3"));

        final List<Response> responses = observable.toList().toBlockingObservable().single();
        assertEquals(3, responses.size());
    }

    @Test
    public void synchronousObservable() {
        final Observable<Response> o1 = Observable.create(new Observable.OnSubscribeFunc<Response>() {
            @Override
            public Subscription onSubscribe(Observer<? super Response> o) {
                o.onNext(dummyResponse("Response from service 1"));
                o.onNext(dummyResponse("Response from service 2"));

                o.onCompleted();
                return Subscriptions.empty();
            }
        });
        assertEquals(2, o1.toList().toBlockingObservable().single().size());
    }

    @Test
    public void asynchronousObservable() {
        final Observable<Response> o1 = Observable.create(new Observable.OnSubscribeFunc<Response>() {
            @Override
            public Subscription onSubscribe(final Observer<? super Response> o) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        o.onNext(dummyResponse("Response from service 1"));
                        o.onNext(dummyResponse("Response from service 2"));
                        o.onCompleted();
                    }
                });
                return Subscriptions.empty();
            }
        });
        assertEquals(2, o1.toList().toBlockingObservable().single().size());
    }

    @Test
    public void asynchronousServiceCalls() {
        final Observable<Response> o = Observable.merge(
                callService1(new Request("Request 1 to service 1"), RETURN_RESPONSE),
                callService1(new Request("Request 2 to service 1"), THROW_EXCEPTION),
                callService1(new Request("Request 3 to service 1"), RETURN_RESPONSE),
                callService2(new Request("Request 1 to service 2"), RETURN_RESPONSE),
                callService2(new Request("Request 2 to service 2"), THROW_EXCEPTION),
                callService2(new Request("Request 3 to service 2"), RETURN_RESPONSE),
                callService3(new Request("Request 1 to service 3"), RETURN_RESPONSE),
                callService3(new Request("Request 2 to service 3"), THROW_EXCEPTION),
                callService3(new Request("Request 3 to service 3"), RETURN_RESPONSE));

        o.toBlockingObservable().forEach(new Action1<Response>() {
            @Override
            public void call(Response response) {
                LOG.debug(response.toString());
            }
        });
        assertEquals(9, o.toList().toBlockingObservable().single().size());
    }

    @Test
    public void map() throws Exception {
        final Observable<Response2> o2 = Observable.create(new Observable.OnSubscribeFunc<Response>() {
            @Override
            public Subscription onSubscribe(final Observer<? super Response> o) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        o.onNext(dummyResponse("Response from service 1"));
                        o.onNext(dummyResponse("Response from service 2"));
                        o.onCompleted();
                    }
                });
                return Subscriptions.empty();
            }
        }).map(new Func1<Response, Response2>() {
            @Override
            public Response2 call(Response response) {
                return new Response2(response.responseText);
            }
        });

        o2.toBlockingObservable().forEach(new Action1<Response2>() {
            @Override
            public void call(Response2 response) {
                LOG.debug(response.toString());
            }
        });

    }

    private Response dummyResponse(String responseText) {
        LOG.debug("Dummy response from {}", responseText);
        return new Response(responseText);
    }


    private Observable<Response> callService1(final Request request, final boolean simulateError) {
        return Observable.create(new Observable.OnSubscribeFunc<Response>() {
            @Override
            public Subscription onSubscribe(final Observer<? super Response> observer) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (simulateError) {
                                service1.throwExceptionOnRequest(request);
                            } else {
                                observer.onNext(service1.call(request));
                            }
                        } catch (RuntimeException e) {  // package as response to handle later
                            observer.onNext(new Response(e.getMessage()));
                        }
                        observer.onCompleted();
                    }
                });
                return Subscriptions.empty();
            }
        });
    }

    private Observable<Response> callService2(final Request request, final boolean simulateError) {
        return Observable.create(new Observable.OnSubscribeFunc<Response>() {
            @Override
            public Subscription onSubscribe(final Observer<? super Response> observer) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (simulateError) {
                                service2.throwExceptionOnRequest(request);
                            } else {
                                observer.onNext(service2.call(request));
                            }
                        } catch (RuntimeException e) {   // package as response to handle later
                            observer.onNext(new Response(e.getMessage()));
                        }
                        observer.onCompleted();
                    }
                });
                return Subscriptions.empty();
            }
        });
    }

    private Observable<Response> callService3(final Request request, final boolean simulateError) {
        return Observable.create(new Observable.OnSubscribeFunc<Response>() {
            @Override
            public Subscription onSubscribe(final Observer<? super Response> observer) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (simulateError) {
                                service3.throwExceptionOnRequest(request);
                            } else {
                                observer.onNext(service3.call(request));
                            }
                        } catch (RuntimeException e) { // package as response to handle later
                            observer.onNext(new Response(e.getMessage()));
                        }
                        observer.onCompleted();
                    }
                });
                return Subscriptions.empty();
            }
        });
    }
}
