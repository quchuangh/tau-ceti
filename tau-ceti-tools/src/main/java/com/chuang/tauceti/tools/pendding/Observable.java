package com.chuang.tauceti.tools.pendding;


import java.util.concurrent.ScheduledExecutorService;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * https://cn.rx.js.org/manual/overview.html#-
 * 该接口单纯只是为了将 RxJS的方法列表用 Java 的语法表示出来。
 * 并加上注释，以便将来参考
 * 需要说明的是 Observable 内部可以是无限长的流，也可以是固定长度流。当流结束时会触发监听者的complete。
 * 注意：触发complete后，内部将会调用unsubscribe() 进行退订，释放所有观察者(监听器)，进而释放观察者内部对象引用。
 *      这很重要，避免了Observable不释放导致的其他监听器内部对象都无法释放的问题。
 * 每个缓冲区的值都将触发next
 * 固定长度流推送完毕后，自动关闭。
 * https://segmentfault.com/a/1190000008834251#articleHeader38
 *
 * map 操作:
 *      所有的map都是将一个发射的对象 转换成 另一个对象（这个对象可以是集合，数组，Observable）
 * 压平 操作:
 *      压平操作是为了将多个Observable 压入到一个。比如 obs.map(value -> Request.Get("/").toObservable())
 *      得到结果是 Observable<Observable<String>> 通过 merge 可以压平多个Observable为一个 Observable<String>
 *      1.merge, 将多个Observable压平成一个，N个Observable发射的值顺序按：发射值的先后排序，所有Observable一起接收，先到先接。
 *      2.concat,将多个Observable压平成一个，N个Observable发射的值顺序按：Observable 的先后顺序排序，一个Observable发射完再接收另一个。
 *      3.switch,将多个Observable压平成一个，N个Observable发射的值顺序按：和merge类似，但一旦后面的Observable开始发射，则立即放弃前面的Observable的接收。
 *          switch的典型场景是用户点击一次按钮就发送一个 AddAndGet的网络请求，给某个值加1并返回结果。网络情况可能出现第二个请求的response是比第一个先到。
 *          如果用merge的话，结果会是2，1。这明显是错误的。如果使用switch会因为先接受到第二个网络请求的response而立即放弃第一个请求的结果。结果为是：2（1被放弃）
 * mergeMap  和 flatMap 是一回事，只是版本不同。现在用 mergeMap。
 *
 *
 * @param <V>
 */
public interface Observable<V> {

    interface Static {
        /**
         * 创建一个  Observable ，该Observable 会不断的生成累加值。生成的间隔为 time 毫秒。
         * 可以方便测试。
         * @param time
         * @param <R>
         * @return
         */
        <R> Observable<R> interval(long time);

    }
//========================== 转换 操作 （返回的Observable 的泛类型会改变）==============================================
    /**
     * 类似于Promise 的 then， 不同的是Promise 始终只有一个值。
     * 但Observable则不是。因此 Function多了一个参数，表示这个值的index
     * 应用场景：对缓冲区中的值进行一次转换，变成另一个类型的流(缓冲区)
     * @param mapFunc V: 缓冲区中的值，Integer：值的index， R：转换后的值
     * @param <R> 转换后的值类型
     * @return
     */
    <R> Observable<R> map(BiFunction<V, Integer, R> mapFunc);

    /**
     * 同 map，不同的是，map是通过function进行convert，但mapTo则直接转换成固定值
     * 应用场景不明。
     * @param object 将缓冲区中的值转换成 object。
     * @param <R>
     * @return
     */
    <R> Observable<R> mapTo(R object);

    /**
     * 类似于 reduce，将缓冲区中的值进行 scanFunc 计算
     * @param scanFunc R：上一次计算的结果， V：缓冲区中的值， Integer：缓冲区值的Index， R：计算结果
     */
    <R> Observable<R> scan(ThreeArgFunction<R, V, Integer, R> scanFunc);

    /**
     * 监听 other， 直到 other 触发结果后，再一次性将缓冲区中的所有结果一次返回。
     * 流(缓冲区)的类型将变成原类型的 数组。
     * @param other
     * @param <R>
     * @return
     */
    <R> Observable<R[]> buffer(Observable<?> other);

    /**
     * 每隔 bufferTimeSpan 对缓冲区的所有数据进行一次打包推送，推送结果是一个数组。
     * 如果设置 bufferCreationInterval ，则每隔 bufferCreationInterval 打开缓冲区。
     *          （不太明白这个意思，难道不在这个间隔时间里，所有的触发结果都不会被缓存？）
     * @param bufferTimeSpan **必须**、bufferTimeSpan设置发射值的时间间隔
     * @param bufferCreationInterval 可选、设置打开缓存区和发射值的时间间隔
     * @param maxBufferSize 可选、设置缓存区长度，当达到这个长度时会推送缓存。
     * @param scheduler
     * @param <R>
     * @return
     */
    <R> Observable<R[]> bufferTime(long bufferTimeSpan,
                                   long bufferCreationInterval,
                                   int maxBufferSize,
                                   ScheduledExecutorService scheduler);

    /**
     * 点击 bufferSize 次后进行一次推送
     * @param bufferSize
     * @param startBufferEvery 用处不明
     * @param <R>
     * @return
     */
    <R> Observable<R[]> bufferCount(int bufferSize, int startBufferEvery);

    /**
     * 在此基础上返回一个 Observable
     * 该Observable 将在源 Observable 触发后，对 c 进行处理
     * @param func 一个被map的方法，返回值会与源 Observable 连接
     * @return
     */
    Observable<?> concatMap(Function<V, Observable<?>> func);

    /**
     * concatMap 不同的是，当第一次触发处理完成前，第二次触发已经到来，那么switch会立即退订(不再监听第一次的触发)
     *
     * 注意：需要说明的是 concatMap 和 switchMap 在功能上很像 Promise 的 thenCall. 但thenCall基于Promise只有一个值。
     * @param func 与之连接的 Observable
     * @return
     */
    Observable<?> switchMap(Function<V, Observable<?>> func);

// ================================ 过滤 操作（返回的Observable泛类型不变） ======================================
    /**
     * 获取前 num 个。然后结束
     * @param num
     * @return
     */
    Observable<V> take(int num);

    /**
     * 获取第一个。然后结束。相当于 take(1);
     * @return
     */
    Observable<V> first();

    /**
     * 在此基础上返回一个 Observable
     * 该Observable 获取缓冲区最后 num 个(前提当然需要源Observable已经complete)。
     * @return
     */
    Observable<V> takeLast(int num);

    /**
     *
     * 在此基础上返回一个 Observable
     * 该Observable 获取缓冲区最后1个(前提当然需要源Observable已经complete)。
     * 相当于 takeLast(1);
     * @return
     */
    Observable<V> last();

    /**
     * 在此基础上返回一个 Observable
     * 该Observable 会在 notifier 触发时，Observable进入完成状态(complete)
     * @param notifier
     * @return
     */
    Observable<V> takeUntil(Observable<?> notifier);

    /**
     * 在此基础上返回一个新的Observable
     * 该Observable 会在 func 返回结果是false时进入 complete
     * @param func
     * @return
     */
    Observable<V> takeWhile(Function<V, Boolean> func);

    /**
     * 在此基础上返回一个 Observable
     * 该Observable 在 dueTime 时间后如果还没有新的值发出，也就是在dueTime的时间内，没有next。
     * 那么Observable才会返回最近一次的值(并不会 complete)。
     * 应用场景，关键字查询。当用户打字有接近500毫秒的明显延迟才真正获取值进行查询。
     * @param dueTime
     * @return
     */
    Observable<V> debounceTime(long dueTime);

    /**
     * 在此基础上返回一个 Observable
     * 该Observable 在源Observable发出第一个值开始， 忽略 duration 时间内的所有值。等待时间过后，会重新开始获取
     * 应用场景：用户点击按钮。第一次点击会触发。然后在 duration秒内继续点击都不触发next。
     * @param duration
     * @return
     */
    Observable<V> throttleTime(long duration);

    /**
     * 在此基础上返回一个 Observable
     * 该Observable 对 源Observable进行 去重复的 过滤，
     *
     *
     * 注意：distinct 内部会创建一个 Set 集合，当接收到元素时，会判断 Set 集合中，是否已存在相同的值，
     * 如果已存在的话，就不会发出值。若不存在的话，会把值存入到 Set 集合中并发出该值。
     * 所以尽量不要直接把 distinct 操作符应用在无限的 Observable 对象中，这样会导致 Set 集合越来越大。
     *
     * @param filterFunc V缓冲区数据，缓冲数据比较值。 如果两个缓冲数据的比较值一样，则认为是重复。
     * @return
     */
    Observable<V> distinct(Function<V, Object> filterFunc);

    /**
     * 同  {@link #distinct(Function)}
     * 差别在于，该方法不使用filterFunc获取比较值进行比较。而是直接比较每隔对象本身
     * @return
     */
    Observable<V> distinct();

    /**
     * 同  {@link #distinct(Function)} 差别在于，这个只比较该值和上一个值是否重复，而不是整个缓冲区。
     * @param filterFunc
     * @return
     */
    Observable<V> distinctUntilChanged(Function<V, Object> filterFunc);
    Observable<V> distinctUntilChanged();

    /**
     * 过滤
     * @param filterFunc V缓冲区数据，数据index，返回值：是否合格
     * @return
     */
    Observable<V> filter(BiFunction<V, Integer, Boolean> filterFunc);

    /**
     * 跳过前面 num 项，例如有 Observable.range(1,6).skip(2);
     * 那么最终缓冲区只有3，4，5，6
     * @return
     */
    Observable<V> skip(int num);

//============================== 组合 操作 ================================================
    /**
     * 在此基础上返回一个 Observable
     * 该Observable 将在源 Observable 触发后，对 c 进行处理
     * 也就是将多个Observable 进行连接。
     * 例如以下 JS 源码：
     * var source = Rx.Observable.interval(1000).take(3);
         var source2 = Rx.Observable.of(3)
         var source3 = Rx.Observable.of(4,5,6)
         var example = source.concat(source2, source3);

         example.subscribe({
         next: (value) => { console.log(value); },
         error: (err) => { console.log('Error: ' + err); },
         complete: () => { console.log('complete'); }
         });
     * 结果为：
     * ----0----1----2(3456)|
     * source由于是 1 秒累加一次，所以前3个结果 1 秒输出一个。后面的结果 source2 和source3没有时间延迟，直接一次输出。
     * @param o1
     * @return
     */
    Observable<?> concat(Observable<?> o1, Observable<?> o2);

    /**
     * 连接所有
     * @return
     */
    Observable<?> concatAll();

    /**
     * 发出通知前，先初始化一个。
     * 相当于 创建后立即next(v);
     * @param v
     * @return
     */
    Observable<V> startWith(V v);

    /**
     * 合并多个Observable。 和concat不同的是， concat是一个Observable完成后，再触发下一个。
     * 而merge只是多个一起。顺序按照每隔Observable发射值的先后来。相当于将所有Observable的next的值
     * 都按先来后到的顺序保存在一个新的缓冲区，然后再一个个的触发。
     * @param o1
     * @return
     */
    Observable<?> merge(Observable<?> o1);

    /**
     * 用于合并输入的 Observable 对象，当源 Observable 对象和 other Observable 对象都发出值后，才会调用 project 函数。
     * 每次任意一个 Observable 在触发时，都会找另一个 Observable 最后一个值。如果另一个 Observable 没有触发过任何值
     * 或者已经结束，则不会调用project方法。
     * project方法的结果，就是新返回的Observable 的每次next值。
     * @param o1
     * @param project
     * @return
     */
    <Arg2, R> Observable<R> combineLatest(Observable<Arg2> o1, BiFunction<R, Arg2, R> project);

    /**
     * 和combineLatest 不同，zip是按照每个 Observable 缓冲区值的index来关联的。
     * 换句话说，就是将每个Observable中index相同的值进行 project处理。
     * @param o1
     * @param project
     * @param <Arg2>
     * @param <R>
     * @return
     */
    <Arg2, R> Observable<R> zip(Observable<Arg2> o1, BiFunction<V, Arg2, R> project);

    @FunctionalInterface
    interface ThreeArgFunction<A1, A2, A3, R> {
        R apply(A1 a1, A2 a2, A3 a3);
    }
}

