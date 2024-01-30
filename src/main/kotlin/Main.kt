package org.example

import io.reactivex.Observable

fun main() {
    Observable.just(1,2,3,4,5).subscribe {
        println("$it")
    }
}