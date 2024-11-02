package com.htd.flow.twenty_five_flow_on

import com.htd.utils.Logit
import com.htd.utils.RepeatChar
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-11-02 17:26
 *
 * Desc: flowOn --- 只切换上游的 CoroutineContext
 */
fun main() = runBlocking {
    val scope = CoroutineScope(EmptyCoroutineContext)

    /**
     * flowOn 会影响上游的 CoroutineContext，其下游还是 scope 默认的 Default
     */
    val flow1 = flow {
        Logit.d("CoroutineContext: in flow1: ${currentCoroutineContext()}") // [ProducerCoroutine{Active}@77179ec7, Dispatchers.IO]
        for (i in 1..5) {
            emit(i)
        }
    }.map {
        // [ProducerCoroutine{Active}@1b11d39e, Dispatchers.IO]
        Logit.d("Map 1 CoroutineContext: : ${currentCoroutineContext()}")
        it * 2
    }.flowOn(Dispatchers.IO).flowOn(CoroutineName("Custom")) // flowOn 连续调用会 fuse 融合，从右往左相加
        .map {
        // [ScopeCoroutine{Active}@7773246a, Dispatchers.Default]
        Logit.d("Map 2 CoroutineContext: : ${currentCoroutineContext()}")
        it * 2
    }
    scope.launch {
        // 推荐写法
        flow1.map {
            it + 1
        }.onEach {
            Logit.d(" Data: $it --- ${currentCoroutineContext()}") // [StandaloneCoroutine{Active}@42444a44, Dispatchers.IO]
        }.flowOn(Dispatchers.IO).collect {
            Logit.d(" Data 2: $it --- ${currentCoroutineContext()}") // [StandaloneCoroutine{Active}@42444a44, Dispatchers.Default]
        }
        Logit.d(RepeatChar.getStr("="))
    }

    // 推荐写法 2
    flow1.map {
        it + 1
    }.onEach {
        Logit.d(" Data 3: $it --- ${currentCoroutineContext()}") // [StandaloneCoroutine{Active}@42444a44, Dispatchers.IO]
    }.launchIn(scope + Dispatchers.IO)
    delay(10000)
}