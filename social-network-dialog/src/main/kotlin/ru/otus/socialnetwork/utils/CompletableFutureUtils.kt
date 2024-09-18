package ru.otus.socialnetwork.utils

import java.util.concurrent.CompletableFuture

/**
 * CompletableFutureUtils.
 *
 * @author Vasily Kuchkin
 */


fun CompletableFuture<List<Any?>>.clientHandleList(resFunc: (resultArg: List<Any?>) -> Unit) {
  this.handle { result, e ->
    if (e != null) {
      e.printStackTrace()
    } else {
      resFunc(result)
    }
  }
}

fun CompletableFuture<*>.clientHandle(resFunc: (resultArg: Any?) -> Unit): CompletableFuture<*> {
  return this.handle { result, e ->
    if (e != null) {
      e.printStackTrace()
    } else {
      resFunc(result)
    }
  }
}