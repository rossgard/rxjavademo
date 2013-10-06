package jersey;

import domain.Request;
import domain.Response;
import endpoint.Service1;
import endpoint.Service2;
import endpoint.Service3;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WrapperService {

    private static final boolean RETURN_RESPONSE = false;
    private static final boolean THROW_EXCEPTION = true;

    private final Service1 service1 = new Service1();
    private final Service2 service2 = new Service2();
    private final Service3 service3 = new Service3();

    private final Executor executor = new ThreadPoolExecutor(10, 10, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());

    public List<Response> service1() {
        try {
            Thread.sleep(2000); // simulate latency
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return Observable.merge(
                callService1(new Request("Request 1 to service 1"), RETURN_RESPONSE),
                callService1(new Request("Request 2 to service 1"), THROW_EXCEPTION),
                callService1(new Request("Request 3 to service 1"), RETURN_RESPONSE)).toList().toBlockingObservable().single();
    }

    public List<Response> service2() {
        try {
            Thread.sleep(5000); // simulate latency
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return Observable.merge(
                callService2(new Request("Request 1 to service 2"), RETURN_RESPONSE),
                callService2(new Request("Request 2 to service 2"), THROW_EXCEPTION),
                callService2(new Request("Request 3 to service 2"), RETURN_RESPONSE)).toList().toBlockingObservable().single();
    }

    public List<Response> service3() {
        try {
            Thread.sleep(10000); // simulate latency
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return Observable.merge(
                callService3(new Request("Request 1 to service 3"), RETURN_RESPONSE),
                callService3(new Request("Request 2 to service 3"), THROW_EXCEPTION),
                callService3(new Request("Request 3 to service 3"), RETURN_RESPONSE)).toList().toBlockingObservable().single();
    }

    public List<Response> asynchronousServiceCall() {
        try {
            Thread.sleep(5000); // simulate latency
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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

        return o.toList().toBlockingObservable().single();
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
