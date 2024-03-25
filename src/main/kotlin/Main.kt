package org.example

import io.reactivex.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.ResourceObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

fun main() {
//    createObservableWithJust()
//    createObservableFromIterable()
//    createObservableUsingCreate()

//     createColdObservable()
//    createHotAndConnectableObservable()
//    throwException()
//    throwExceptionUsingCallable()
//    createObservableUsingEmpty()
//    createObservableUsingNever()
//    createObservableUsingRange()
//    createObservableUsingDefer()
//    createObservableUsingFromCallable()
//    createObservableUsingFromInterval()

//    createSingle()
//    createMaybe()
//    createCompletable()

//    handleDisposable()
//    handleDisposableInObserver()
//    handleDisposableOutsideObserver()
//    compositeDisposable()

//    mapOperator()
//    mapOperatorReturnsDifferentData()
//    filterOperator()
//    combineMapAndFilter()

//    takeOperator()
//    takeWithInterval()
//    takeWhileOperator()
//    skipOperator()
//    skipWhileOperator()

//    distinctOperator()
//    distinctWithKeySelector()
//    distinctUntilChangedOperator()
//    distinctUntilChangedWithKeySelector()

//    useDefaultIfEmpty()
//    useSwitchIfEmpty()

//    useRepeat()
//    useScan()
//    useScanWithInitialValue()

//    useSorted()
//    useSortedWithOwnComparator()
//    useSortedOnNonComparator()

//    delay()
//    delayError()

//    containsWithPremitive()
//    containsWithNonPremitive()

//    exDoOnError()
//    exOnErrorResumeNext()
//    exOnErrorReturn()
//    exOnErrorReturnItem()

//    retryWithPredicate()
//    exRetry()
//    exRetryUntil()

//    exDoOnSubscribe()
//    exDoOnNext()
//    exDoOnComplete()

//    exDoFinally()
//    exDoOnDispose()

//    exMerge()
//    exMergeArray()
//    exMergeIterable()
//    exMergeWith()
//    exMergeInfinite()

//    exZip()
//    exZipWith()
//    exZipIterable()

    exFlatMap()
//    exFlatMapBiFunction()

//    exMapWithObservable()

//    exConcat()
//    exConcatWith()
//    exConcatMap()

//    observerOn()
//    subscribeOn()
//    subscribeOnWithObserverOn()

//    val publishSubject = PublishSubject.create<String>()
//    val str = ""
//    str.intern()
//    val subscriber1 = publishSubject.subscribe(
//        {
//            println("subscriber 1 $it")
//        },
//        {
//            println("error subscriber 1 ${it.localizedMessage}")
//        },
//        {
//            println("subscriber 1 onComplete")
//        }
//    )
//    publishSubject.onNext("event 1")
}

/**
 * observeOn изменяет планировщик указанного в subscribeOn для нижних наблюдателей по цепочке
 */
fun subscribeOnWithObserverOn() {
    Single.just(2)
        .flatMap {
            println(Thread.currentThread().name) //RxCachedThreadScheduler-1
            Single.just(it * 2)
        }
        .flatMap {
            println(Thread.currentThread().name) //RxCachedThreadScheduler-1
            Single.just(it * 3)
        }
        .observeOn(Schedulers.computation())
        .subscribeOn(Schedulers.io())
        .flatMap {
            println(Thread.currentThread().name) //RxComputationThreadPool-1
            Single.just(it * 4)
        }
        .subscribe(
            {
                println(Thread.currentThread().name) //RxComputationThreadPool-1
                println(it)
            },
            {
                println(it.localizedMessage)
            }
        )
    Thread.sleep(1000)
}

/**
 * subscribeOn указывает планировщик, в котором будет работать Наблюдаемый объект
 */
fun subscribeOn() {
    Single.just(2)
        .flatMap {
            println(Thread.currentThread().name) //RxCachedThreadScheduler-1
            Single.just(it * 2)
        }
        .flatMap {
            println(Thread.currentThread().name) //RxCachedThreadScheduler-1
            Single.just(it * 3)
        }
        .subscribeOn(Schedulers.io())
        .subscribeOn(Schedulers.computation())
        .flatMap {
            println(Thread.currentThread().name) //RxCachedThreadScheduler-1
            Single.just(it * 4)
        }
        .subscribe(
            {
                println(Thread.currentThread().name) //RxCachedThreadScheduler-1
                println(it)
            },
            {
                println(it.localizedMessage)
            }
        )
    Thread.sleep(1000)
}

/**
 * ObserveOn указавает планировщик, на котором наблюдатель будет наблюдать за данной Observable
 */
fun observerOn() {
    Single.just(2)
        .flatMap {
            println(Thread.currentThread().name) //main
            Single.just(it * 2)
        }
        .observeOn(Schedulers.io())
        .flatMap {
            println(Thread.currentThread().name) //RxCachedThreadScheduler-1
            Single.just(it * 3)
        }
        .observeOn(Schedulers.computation())
        .flatMap {
            println(Thread.currentThread().name) //RxComputationThreadPool-1
            Single.just(it * 4)
        }
        .subscribe(
            {
                println(it)
            },
            {
                println(it.localizedMessage)
            }
        )
    Thread.sleep(1000)
}

/**
 * если у вас есть наблюдаемый объект, который выдает значения,
 * и для каждого из этих значений вы хотите выполнить другую
 * последовательность наблюдаемого объекта, гарантируя,
 * что они будут обрабатываться по порядку, а не параллельно.
 */
fun exConcatMap() {
    Observable.just("foo", "bar", "jam")
        .concatMap {
            Observable.fromArray(it.split(""))
        }
        .subscribe { println(it) }

}

/**
 * concat сначала дождется завершения предыдущих потоков, прежде чем обрабатывать последующие потоки
 */
fun exConcatWith() {
    val observable1 = Observable.interval(1, TimeUnit.SECONDS)
        .take(4)
        .map { "Observable1: $it" }
    val observable2 = Observable.interval(300, TimeUnit.MILLISECONDS)
        .map { "Observable2: $it" }
    observable1.concatWith(observable2).subscribe { println(it) }
    Thread.sleep(10000)
}

/**
 * Оператор Concat объединяет выходные данные нескольких наблюдаемых объектов таким образом,
 * что они действуют как один наблюдаемый объект, при этом все элементы, испускаемые первым наблюдаемым объектом,
 * испускаются раньше любого из элементов, испускаемых вторым наблюдаемым объектом (и так далее, если их более двух).
 */
fun exConcat() {
    val observable1 = Observable.just(1, 2, 3, 4, 5)
    val observable2 = Observable.just(6, 7, 8, 9, 10)
    Observable.concat(observable1, observable2).subscribe { println(it) }
}


/**
 * Здесь используются операторы map для преобразования потока между ними в Observables
 * и по очевидным причинам подписчики получают вместо этого объект Observable
 */
fun exMapWithObservable() {
    val observable = Observable.just("foo", "bar", "jam")
    observable.map {
        Observable.fromArray(it.split(""))
    }.subscribe { obs ->
        println(obs)
        obs.subscribe { println(it) }
    }
}

/**
 * Эта функция использует flatMap для возврата желаемых наблюдаемых в нисходящий поток.
 * на основе выбросов восходящего потока, которые он получает в Функции
 */
fun exFlatMap() {
    val observable = Observable.just("foo", "bar", "jam")
    observable.flatMap {
        if (it == "bar") return@flatMap Observable.empty<Any>()
        return@flatMap Observable.fromArray(it.split(""))
    }.subscribe { println(it) }
}

/**
 * Это похоже на обычную flatMap, за исключением biFunction.
 * которая объединяет выбросы восходящего потока с выбросами из flatMap, возвращаемыми Observables из Function
 */
fun exFlatMapBiFunction() {
    val observable = Observable.just("foo", "bar", "jam")
    observable.flatMap(
        { Observable.fromArray(it.split("")) },
        { actual, second -> "$actual $second" }
    ).subscribe { println(it) }
}

/**
 * Использует оператор Zip для получения потока на функции Zipper.
 */
fun exZip() {
    val observable1 = Observable.just(1, 2, 3, 4, 5)
    val observable2 = Observable.range(6, 5)
    val observable3 = Observable.fromIterable(listOf(11, 12, 13, 14, 15))
    Observable.zip(observable1, observable2, observable3) { a, b, c ->
        a + b + c
    }.subscribe { println(it) }
}

/**
 * Использует оператор ZipWith на Observable, чтобы легко застегнуть одну Observable с другой
 */
fun exZipWith() {
    val observable1 = Observable.range(6, 5)
    val observable2 = Observable.fromIterable(listOf(11, 12, 13, 14, 15))
    observable1.zipWith(observable2) { a, b ->
        a + b
    }.subscribe { println(it) }
}

/**
 * Использует оператор zipIterable, который принимает список Observables и выдает зацикленное излучение в виде массива
 */
fun exZipIterable() {
    val observable1 = Observable.range(1, 5)
    val observable2 = Observable.range(6, 5)
    val observable3 = Observable.range(11, 5)
    val observableList = listOf<Observable<Int>>(observable1, observable2, observable3)
    Observable.zipIterable(
        observableList,
        { array ->
            array.forEach {
                print("$it ")
            }
            println()
        },
        true,
        1
    ).subscribe(
        {},
        {
            it.localizedMessage
        },
        {
            println("onComplete")
        })
}

/**
 * Использует статическую функцию merge для слияния Observables.
 * Эта функция может принимать не более 4 Observables
 */
fun exMerge() {
    val oneToFive = Observable.just(1, 2, 3, 4, 5)
    val sixToTen = Observable.just(6, 7, 8, 9, 10)
    Observable.merge(oneToFive, sixToTen).subscribe { println(it) }
}

/**
 * Использует статическую функцию mergeArray для объединения неограниченных Observables, так как она принимает vararg
 */
fun exMergeArray() {
    val observable1 = Observable.just(1, 2, 3, 4, 5)
    val observable2 = Observable.just(6, 7, 8, 9, 10)
    val observable3 = Observable.just(11, 12, 13, 14, 15)
    val observable4 = Observable.just(16, 17, 18, 19, 20)
    val observable5 = Observable.just(21, 22, 23, 24, 25)
    Observable.mergeArray(observable1, observable2, observable3, observable4, observable5).subscribe { println(it) }
}

/**
 * Использует статическую функцию merge для объединения списка Observables.
 */
fun exMergeIterable() {
    val observable1 = Observable.just(1, 2, 3, 4, 5)
    val observable2 = Observable.just(6, 7, 8, 9, 10)
    val observable3 = Observable.just(11, 12, 13, 14, 15)
    val observable4 = Observable.just(16, 17, 18, 19, 20)
    val observable5 = Observable.just(21, 22, 23, 24, 25)
    val observableList = listOf<Observable<Int>>(
        observable1,
        observable2,
        observable3,
        observable4,
        observable5
    )
    Observable.merge(observableList).subscribe { println(it) }
}

/**
 * Все Observables имеют функцию mergeWith, чтобы легко объединить ее с другой Observable.
 * В этом случае мы не можем объединить более одной Observable.
 */
fun exMergeWith() {
    val observable1 = Observable.just(1, 2, 3, 4, 5)
    val observable2 = Observable.just(6, 7, 8, 9, 10)
    observable1.mergeWith(observable2).subscribe { println(it) }
}

/**
 * Здесь показана реализация функции слияния с бесконечными Observables.
 * Как интервал испускает данные в заданное время
 */
fun exMergeInfinite() {
    val infinite1 = Observable.interval(1, TimeUnit.SECONDS)
        .map { item -> "From infinite1: $item" }
    val infinite2 = Observable.interval(2, TimeUnit.SECONDS)
        .map { item -> "From infinite2: $item" }
    infinite1.mergeWith(infinite2).subscribe { println(it) }
    Thread.sleep(6000)
}

/**
 * doFinally работает после завершения работы наблюдаемого объекта или onComplete
 */
fun exDoFinally() {
    Observable.just(1, 2, 3, 4, 5)
        .doFinally { println("doFinally") }
        .subscribe(
            { println(it) },
            { println(it.message) },
            { println("onComplete") }
        )
}

/**
 * doOnDispose работает только в том случае, если мы можем утилизировать наблюдаемую явно.
 * до onComplete или onError
 */
fun exDoOnDispose() {
    Observable.just(1, 2, 3, 4, 5)
        .doOnDispose { println("doOnDispose") }
        .doOnSubscribe { it.dispose() }
        .subscribe(
            { println(it) },
            { println(it.message) },
            { println("onComplete") }
        )
}

/**
 * doOnSubscribe будет получать одноразовые данные, как только мы подпишемся на конкретную наблюдаемую.
 */
fun exDoOnSubscribe() {
    Observable.just(1, 2, 3, 4, 5)
        .doOnSubscribe {
            println("doOnSubscribe")
        }
        .subscribe { println(it) }
}

/**
 * doOnNext получит элемент непосредственно перед тем, как он достигнет нижнего уровня onNext
 */
fun exDoOnNext() {
    Observable.just(1, 2, 3, 4, 5)
        .doOnNext {
            println("doOnNext: $it")
        }
        .subscribe { println(it) }
}

/**
 * doOnComplete получит значение void непосредственно перед тем, как достигнет onComplete
 */
fun exDoOnComplete() {
    Observable.just(1, 2, 3, 4, 5)
        .doOnComplete {
            println("doOnComplete")
        }
        .subscribe(
            { println(it) },
            { println(it.message) },
            { println("onComplete") })
}

/**
 * retryUntil зависит от передаваемого нами булевого значения, он продолжает повторять попытки, пока мы не передадим true, основываясь на логике.
 */
fun exRetryUntil() {
    val atomicInt = AtomicInteger()
    Observable.error<Any>(Exception("This is an example error"))
        .doOnError {
            println(atomicInt.get())
            atomicInt.getAndIncrement()
        }
        .retryUntil {
            println("Retrying")
            return@retryUntil atomicInt.get() >= 3
        }
        .subscribe(
            { println("") },
            { println("Subscribed Error: ${it.localizedMessage}") },
            { println("onComplete") }
        )
}

/**
 * Эта повторная попытка принимает число и пытается повторить подписку и получение данных из наблюдаемого объекта.
 */
fun exRetry() {
    Observable.error<Any>(Exception("This is an example error"))
        .retry(3)
        .subscribe(
            { println("") },
            { println("Subscribed Error: ${it.localizedMessage}") },
            { println("onComplete") }
        )
}

/**
 * Этот блок повторного выполнения пытается проанализировать ошибку и принять решение, основанное на ошибке, повторять ее или нет.
 * основываясь на нашей логике внутри этого блока
 */
fun retryWithPredicate() {
    Observable.error<Any>(Exception("This is an example error"))
        .retry { error ->
            if (error is Exception) {
                println("retrying")
                true
            } else {
                false
            }
        }
        .subscribe(
            { println("") },
            { println("Subscribed Error: ${it.localizedMessage}") },
            { println("onComplete") }
        )
}

/**
 * Ошибка сначала попадает в лямбду doOnError в цепочке, так что мы можем ее обработать.
 */
fun exDoOnError() {
    Observable.error<Any>(Exception("This is an example error"))
        .doOnError { error ->
            println("Error: ${error.message}")
        }
        .subscribe(
            { println("") },
            { println("Subscribed Error: ${it.localizedMessage}") },
            { println("onComplete") }
        )
}

/**
 * При обнаружении ошибки в цепочке происходит переход к onErrorResumeNext.
 * Поскольку это принимает другую Observable, подписчик переключается на эту Observable.
 * чтобы пропустить ошибку
 */
fun exOnErrorResumeNext() {
    Observable.error<Any>(Exception("This is an example error"))
        .onErrorResumeNext(Observable.just(1, 2, 3, 4, 5))
        .subscribe(
            { println(it) },
            { println("Subscribed Error: ${it.localizedMessage}") },
            { println("onComplete") }
        )
}

/**
 * Мы можем возвращать определенные значения в зависимости от типа ошибки.
 * Когда мы получаем ошибку, она переходит в лямбду onErrorReturn.
 */
fun exOnErrorReturn() {
    Observable.error<Any>(Exception("This is an example error"))
        .onErrorReturn { error ->
            if (error is IOException) 1
            else
                throw Exception("This is an exception")
        }
        .subscribe(
            { println(it) },
            { println("Subscribed Error: ${it.localizedMessage}") },
            { println("onComplete") }
        )
}

/**
 * Мы можем передать альтернативу для подписчика ниже по цепочке.
 * При возникновении ошибки он выдает эту конкретную альтернативу.
 */
fun exOnErrorReturnItem() {
    Observable.error<Any>(Exception("This is an example error"))
        .onErrorReturnItem("Hello!!!")
        .subscribe(
            { println(it) },
            { println("Subscribed Error: ${it.localizedMessage}") },
            { println("onComplete") }
        )
}

/**
 * Оператор contains проверяет, существует ли конкретный объект в эмиссии Observable.
 * на основе хэш-кода объекта
 * Как только он получает объект, он выдает true или false в противном случае
 */
fun containsWithNonPremitive() {
    val user = User("Ivan")
    Observable.just(user)
        .contains(user)
        .subscribe({
            println(it)
        }, {
            println(it.localizedMessage)
        })
}

class User(private val name: String)

/**
 * Оператор contains проверяет, существует ли номер в Observable emission.
 * Как только он получает элемент, он выдает true или false в противном случае
 */
fun containsWithPremitive() {
    Observable.just(1, 2, 3, 4, 5)
        .contains(3)
        .subscribe({
            println(it)
        }, {
            println(it.localizedMessage)
        })

}

/**
 * Оператор 'delay' не добавляет никакой задержки перед выдачей ошибки.
 * Это означает, что по умолчанию ошибка немедленно отправляется подписчикам.
 * Чтобы отложить выдачу ошибки, нам нужно передать параметр delayError как true
 */
fun delayError() {
    Observable.error<Any>(Exception("Error"))
        .delay(2000, TimeUnit.MILLISECONDS, true)
        .subscribe(
            { println("") },
            { println(it.localizedMessage) },
            { println("onComplete") }
        )
    Thread.sleep(5000)
}

/**
 * Используется оператор 'delay' для добавления задержки перед стартовым выбросом Observable.
 * Примечание: 'delay' не задерживает каждое испускание, вместо этого он задерживает начало испускания
 */
fun delay() {
    Observable.just(1, 2, 3, 4, 5)
        .delay(3000, TimeUnit.MILLISECONDS)
        .subscribe { println(it) }
    Thread.sleep(5000)
}

/**
 * Здесь используется оператор sorted вместе с функцией сравнения Integer, чтобы
 * сортировки выбросов на основе их длины
 */
fun useSortedOnNonComparator() {
    Observable.just("foo", "john", "bar")
        .sorted { first, second -> first.length.compareTo(second.length) }
        .subscribe { println(it) }
}

/**
 * Здесь используется оператор sorted вместе с функцией Comparators reverse.
 * для сортировки и обратного выброса
 */
fun useSortedWithOwnComparator() {
    Observable.just(3, 5, 2, 4, 1)
        .sorted(Comparator.reverseOrder())
        .subscribe { println(it) }
}

/**
 * Для сортировки используется оператор sorted.
 */
fun useSorted() {
    Observable.just(3, 5, 2, 4, 1)
        .sorted()
        .subscribe { println(it) }
}

/**
 * Здесь используется оператор scan, выводящий сумму ранее испущенного элемента и текущего, который собирается испустить,
 * но здесь также учитывается начальное испускание путем указания начального значения
 */
fun useScanWithInitialValue() {
    Observable.just(1, 2, 3, 4, 5)
        .scan(10) { accumulator, next ->
            accumulator + next
        }
        .subscribe { println(it) }
}

/**
 * Здесь используется оператор scan, чтобы вывести сумму ранее испущенного элемента и текущего, который собирается испустить.
 */
fun useScan() {
    Observable.just(1, 2, 3, 4, 5)
        .scan { accumulator, next ->
            accumulator + next
        }
        .subscribe { println(it) }
}

/**
 * Здесь используется оператор repeat, чтобы указать, сколько раз будет повторяться выброс.
 */
fun useRepeat() {
    Observable.just(1, 2, 3, 4, 5)
        .repeat(3)
        .subscribe { println(it) }
}

/**
 * Это приведет к переключению на некоторый альтернативный наблюдаемый источник.
 * если выброс окажется пустым
 */
fun useSwitchIfEmpty() {
    Observable.just(1, 2, 3, 4, 5)
        .filter { it > 10 }
        .switchIfEmpty(Observable.just(6, 7, 8, 9))
        .subscribe { println(it) }
}

/**
 * Используйте оператор defaultIfEmpty(), чтобы наблюдатель излучал хотя бы значение по умолчанию.
 * если эмиссия окажется пустой
 */
fun useDefaultIfEmpty() {
    Observable.just(1, 2, 3, 4, 5)
        .filter { it > 10 }
        .defaultIfEmpty(100)
        .subscribe { println(it) }
}

/**
 * Используйте distinctUntilChangedOperator() на основе свойства элемента, чтобы различать последовательные дубликаты элементов
 */
fun distinctUntilChangedWithKeySelector() {
    Observable.just("foo", "fool", "super", "foss", "foil")
        .distinctUntilChanged { s, s2 ->
            s.length == s2.length
        }
        .subscribe { println(it) }
}

/**
 * Используйте distinctUntilChanged(), чтобы избежать последовательного дублирования элементов друг за другом
 */
fun distinctUntilChangedOperator() {
    Observable.just(1, 1, 2, 2, 3, 3, 4, 5, 1, 2)
        .distinctUntilChanged()
        .subscribe { println(it) }
}

/**
 * Для различения эмиссии используется свойство distinct, основанное на свойстве элемента.
 */
fun distinctWithKeySelector() {
    Observable.just("foo", "fool", "super", "foss", "foil")
        .distinct { it.length }
        .subscribe { println(it) }
}

/**
 * Используйте distinct(), чтобы получить уникальную эмиссию.
 */
fun distinctOperator() {
    Observable.just(1, 1, 2, 2, 3, 3, 4, 5, 1, 2)
        .distinct()
        .subscribe { println(it) }
}

/**
 * skipWhile() - это комбинация фильтров и skip,
 * с той лишь разницей, что фильтр просматривает все элементы, чтобы проверить, истинна ли логика.
 * тогда как skipWhile() пропускает элементы только в том случае, если какая-то логика истинна
 * и как только логика становится ложной, она пропускает оставшиеся элементы без проверки
 */
fun skipWhileOperator() {
    Observable.just(1, 2, 3, 4, 5, 1, 2, 3, 4, 5)
        .skipWhile { item -> item <= 3 }
        .subscribe { println(it) }
}

/**
 * skip(2) является противоположностью take(2)
 * он пропускает первые значения и выводит оставшиеся.
 */
fun skipOperator() {
    Observable.just(1, 2, 3, 4, 5)
        .skip(2)
        .subscribe { println(it) }
}

/**
 * Эта takeWhile() похожа на комбинацию filter и take,
 * с той лишь разницей, что filter просматривает все элементы, чтобы проверить, истинна ли логика.
 * в то время как takeWhile() выдает только некоторые истинные логики
 * и завершается, как только логика становится ложной
 */
fun takeWhileOperator() {
    Observable.just(1, 2, 3, 1, 5, 1, 2, 3, 4, 5)
        .takeWhile { item -> item <= 3 }
        .subscribe { println(it) }
}

/**
 * Используется take(2), но с интервалом, который выдает элементы только для указанного интервала времени
 */
fun takeWithInterval() {
    Observable.interval(300, TimeUnit.MILLISECONDS)
        .take(2, TimeUnit.SECONDS)
        .subscribe { println(it) }
    Thread.sleep(5000)
}

/**
 * Здесь используется take(2), который выдает только первые 2 элемента, а затем завершает.
 */
fun takeOperator() {
    Observable.just(1, 2, 3, 4, 5)
        .take(2)
        .subscribe {
            println(it)
        }
}


/**
 * Объединяет операторы map() и filter()
 * и поскольку map() и filter() оба являются ничем иным, как Observable
 * и также работает как Observable, поэтому мы можем соединить их в цепочку,
 * но порядок работы здесь имеет значение.
 * Здесь filter() будет пинаться первым, а map() будет работать с отфильтрованным выбросом,
 * а не со всем выбросом в целом
 */
fun combineMapAndFilter() {
    val observable = Observable.just(1, 2, 3, 4, 5)
    observable
        .filter { it % 2 == 0 }
        .map { it * 2 }
        .subscribe { println(it) }
}

/**
 * Использует оператор filter() для отсеивания значений между,
 * которые не соответствуют логике, указанной в filter,
 * и filter() не может выдать ни одного элемента, если ни один элемент не соответствует этому критерию
 */
fun filterOperator() {
    val observable = Observable.just(1, 2, 3, 4, 5)
    observable
        .filter { it % 2 == 0 }
        .subscribe { println(it) }
}

/**
 * Использует оператор map() для преобразования значения между ними,
 * прежде чем оно попадет к наблюдателю, и здесь map() испускает различные типы данных и
 * Наблюдатель просто должен подстроиться под него или принять тот же тип данных.
 */
fun mapOperatorReturnsDifferentData() {
    val observable = Observable.just(1, 2, 3, 4, 5)
    observable
        .map { "Hello!" }
        .subscribe { println(it) }
}

/**
 * Использует оператор map() для преобразования значения между ними,
 * прежде чем оно попадет к наблюдателю
 */
fun mapOperator() {
    val observable = Observable.just(1, 2, 3, 4, 5)
    observable
        .map { it * 2 }
        .subscribe { println(it) }
}

/**
 * Используем CompositeDisposable и применяем его метод add.
 * для добавления одноразовых disposable в набор
 * вызывая dispose на CompositeDisposable вместо того, чтобы избавляться от каждого.
 * Мы даже можем использовать метод delete, чтобы удалить любой одноразовый disposable из набора CompositeDisposable
 */
fun compositeDisposable() {
    val compositeDisposable = CompositeDisposable()
    val observable = Observable.interval(1, TimeUnit.SECONDS)
    val disposable1 = observable.subscribe { println("Observer1: $it") }
    val disposable2 = observable.subscribe { println("Observer2: $it") }
    compositeDisposable.addAll(disposable1, disposable2)
    Thread.sleep(3000)
    compositeDisposable.delete(disposable1)
    println("compositeDisposable delete disposable1")
    compositeDisposable.dispose()
    println("compositeDisposable dispose")
    Thread.sleep(2000)

//Observer1: 0
//Observer2: 0
//Observer1: 1
//Observer2: 1
//Observer1: 2
//Observer2: 2
//compositeDisposable delete disposable1
//compositeDisposable dispose
//Observer1: 3
//Observer1: 4
}

/**
 * Здесь используется ResourceObserver, чтобы избавиться от подписки.
 * Вместо subscribe() здесь используется subscribeWith()
 * Которая возвращает Observer, который мы передаем.
 * Поскольку ResourceObserver реализует Disposable,
 * поэтому мы можем работать с ним, как с одноразовым
 */
fun handleDisposableOutsideObserver() {
    val observable = Observable.just(1, 2, 3, 4, 5)
    val observer = object : ResourceObserver<Int>() {

        override fun onError(p0: Throwable) {
        }

        override fun onComplete() {
        }

        override fun onNext(p0: Int) {
            println(p0)
        }

    }
    val disposable = observable.subscribeWith(observer)
}

/**
 * Когда мы передаем наблюдателя в метод subscribe(), он возвращает void.
 * Поэтому нам нужно получить Disposable из переопределенного метода onSubscribe,
 * чтобы мы могли работать с ним в любом месте и в любой момент времени.
 */
fun handleDisposableInObserver() {
    val observable = Observable.just(1, 2, 3, 4, 5)
    val observer = object : Observer<Int> {
        private var disposable: Disposable? = null
        override fun onSubscribe(p0: Disposable) {
            disposable = p0
        }

        override fun onError(p0: Throwable) {
        }

        override fun onComplete() {
        }

        override fun onNext(p0: Int) {
            if (p0 == 3) disposable?.dispose()
            println(p0)
        }

    }
    observable.subscribe(observer)
}

/**
 * Сохраняет возвращенное одноразовое сообщение из subscribe(),
 * и утилизирует его через 3000 миллисекунд, а также приостанавливает
 * поток еще на 3000 миллисекунд, чтобы проверить, излучает он или нет
 */
fun handleDisposable() {
    val observable = Observable.interval(1, TimeUnit.SECONDS)
    val disposable = observable.subscribe { println("Observer: $it") }
    Thread.sleep(3000)
    disposable.dispose()
    Thread.sleep(2000)
}

/**
 * Создает завершаемый
 * Здесь использован фабричный метод Completable.fromSingle(), который принимает один элемент.
 * Но он не излучает никаких элементов своим наблюдателям.
 * Потому что CompletableObserver не имеет метода onNext()
 * И его работа ограничивается тем, что он сообщает своим наблюдателям, что что-то было завершено.
 * Возможно, вы будете использовать его только для тестирования некоторых вещей.
 * В противном случае, это не часто используется в производстве
 */
fun createCompletable() {
    Completable.fromSingle(Single.just("Hello!!!")).subscribe {
        println("Done")
    }
}

/**
 * Создает Maybe, который может передавать или не передавать данные своим наблюдателям.
 * Здесь был вызван метод Maybe.empty(), и этот фабричный метод не излучает, а только завершает
 */
fun createMaybe() {
    Maybe.empty<Any>().subscribe(
        {
            println("Any")
        },
        {
            println("Error: ${it.localizedMessage}")
        },
        {
            println("Done")
        }
    )
}

/**
 * Создает единичное устройство и отправляет данные его наблюдателю только один раз.
 */
fun createSingle() {
    Single.just("Hello!!!").subscribe(
        {
            println(it)
        },
        {
            println("Error: ${it.localizedMessage}")
        },
    )
}

fun createObservableUsingFromInterval() {
//    val observable = Observable.interval(1, TimeUnit.SECONDS) <- COLD
    val observable = Observable.interval(1, TimeUnit.SECONDS).publish() //<- HOT
    observable.subscribe { println("Observer1: $it") }
    observable.connect()
    Thread.sleep(2000)
    observable.subscribe { println("Observer2: $it") }
    Thread.sleep(3000)
}
//HOT
//Observer1: 0
//Observer1: 1
//Observer1: 2
//Observer2: 2
//Observer1: 3
//Observer2: 3
//Observer1: 4
//Observer2: 4

fun createObservableUsingFromCallable() {
    val observable = Observable.fromCallable {
        println("Calling Method")
        getNumber()
    }

    observable.subscribe(
        { println(it) },
        { println("An Exception Occurred ${it.localizedMessage}") }
    )
}

/**
 * Этот метод возвращает выражение, которое является int
 * @return фиктивное выражение (int)
 */
private fun getNumber(): Int {
    println("Generating value")
    return 1 / 0
}

fun createObservableUsingDefer() {
    val start = 5
    var count = 2
    val observable = Observable.defer {
        println("New Observable is created with start=$start and count=$count")
        Observable.range(start, count)
    }
    observable.subscribe { println("Observer1: $it") }
    count = 3
    observable.subscribe { println("Observer2: $it") }

//    New Observable is created with start=5 and count=2
//    Observer1: 5
//    Observer1: 6
//    New Observable is created with start=5 and count=3
//    Observer2: 5
//    Observer2: 6
//    Observer2: 7
}

fun createObservableUsingRange() {
//    val observable = Observable.range(0, 10)
//    observable.subscribe { println(it) }
    val observable = Observable.range(5, 2)
    observable.subscribe { println(it) }
}

/**
 * Создает Observable с помощью фабричного метода never()
 * Который не испускает никаких элементов и никогда не завершается.
 * Таким образом, его наблюдатели будут ждать, пока поток не будет запущен.
 * Observable.never() используется в основном для тестирования
 */
fun createObservableUsingNever() {
    val observable = Observable.never<Any>()
    observable.subscribe(
        { println(it) },
        { println(it.message) },
        { println("OnComplete") }
    )
    Thread.sleep(1000)
}

/**
 * Создает Observable с помощью фабричного метода empty()
 * Который не передает никаких элементов в onNext() и завершается сразу.
 * Таким образом, мы получаем обратный вызов onComplete()
 */
fun createObservableUsingEmpty() {
    val observable = Observable.empty<Any>()
    observable.subscribe(
        { println(it) },
        { println(it.message) },
        { println("OnComplete") }
    )
}

/**
 * Этот метод использует Observable.error() для передачи нового экземпляра Callable
 * который принимает Exception в качестве возвращаемого типа через лямбду
 * Таким образом, их наблюдатели каждый раз получают новый экземпляр исключения в onError()
 */
fun throwExceptionUsingCallable() {
    val observable = Observable.error<Any> {
        // Мы печатаем это сообщение, чтобы показать, что новый экземпляр создан, прежде чем
        // публикации ошибки своим наблюдателям
        println("New Exception Created")
        Exception("An Exception")
    }

    observable.subscribe(
        { println(it) },
        { println("Error1: ${it.hashCode()}") }
    )
    observable.subscribe(
        { println(it) },
        { println("Error2: ${it.hashCode()}") }
    )
}

/**
 * Этот метод использует Observable.error() для прямой передачи нового экземпляра исключения.
 * Таким образом, их наблюдатели получают один и тот же экземпляр исключения каждый раз.
 */
fun throwException() {
    val observable = Observable.error<Any>(Exception("Error"))
    observable.subscribe(
        { println(it) },
        { println("Error1: ${it.hashCode()}") }
    )
    observable.subscribe(
        { println(it) },
        { println("Error2: ${it.hashCode()}") }
    )
}

fun createHotAndConnectableObservable() {
    val observable = Observable.just(1, 2, 3, 4, 5).publish()
    observable.subscribe { println("Observer1: $it") }
    observable.subscribe { println("Observer2: $it") }
    observable.connect()
}

fun createColdObservable() {
    val observable = Observable.just(1, 2, 3, 4, 5)
    observable.subscribe { println("Observer1: $it") }
    Thread.sleep(1000)
    observable.subscribe { println("Observer2: $it") }
}

fun createObservableUsingCreate() {
    println("createObservableUsingCreate:")
    val observable = Observable.create<Int> { emitter ->
        emitter.onNext(1)
        emitter.onNext(2)
        emitter.onNext(3)
        emitter.onError(RuntimeException("Ошибка")) //Вызовет ошибку и не пойдет дальше
        emitter.onComplete() //Выведутся все до OnComplete
        emitter.onNext(4)
    }
    observable.subscribe(object : Observer<Int> {
        override fun onSubscribe(p0: Disposable) {
            println("onSubscribe1")
            observable.subscribe(object : Observer<Int> {
                override fun onSubscribe(p0: Disposable) {
                    println("onSubscribe2")
                }

                override fun onError(p0: Throwable) {
                    println("onError2: ${p0.localizedMessage}")
                }

                override fun onComplete() {
                    println("onComplete")
                }

                override fun onNext(p0: Int) {
                    println("2:$p0")
                }
            })
        }

        override fun onError(p0: Throwable) {
            println("onError: ${p0.localizedMessage}")
        }

        override fun onComplete() {
            println("onComplete")
        }

        override fun onNext(p0: Int) {
            println(p0)
        }
    })
}

fun createObservableFromIterable() {
    println("createObservableFromIterable:")
    val dd = Observable.create { p0 -> p0.onNext("Test") }
    dd.subscribe {
        println(it)
    }

//    val list = listOf(1, 2, 3, 4, 5)
//    Observable.fromIterable(list).subscribe {
//        println(it)
//    }
}

fun createObservableWithJust() {
    println("createObservableWithJust:")
    Observable.just(1, 2, 3, 4, 5).subscribe {
        println(it)
    }
}
