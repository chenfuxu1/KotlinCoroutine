package com.htd.flow.twenty_seven_merge

import com.htd.utils.Logit
import com.htd.utils.RepeatChar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-11-02 20:50
 *
 * Desc:
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun main() = runBlocking {
    val scope = CoroutineScope(EmptyCoroutineContext)
    val flow1 = flow {
        delay(500)
        emit(1)
        delay(500)
        emit(2)
        delay(500)
        emit(3)
    }
    val flow2 = flow {
        delay(250)
        emit(4)
        delay(500)
        emit(5)
        delay(500)
        emit(6)
    }
    // 将两个 flow 合并发送，同时收集
    val mergeFlow = merge(flow1, flow2)

    val flowList = listOf(flow1, flow2)
    val mergeFlowFromList = flowList.merge()

    // 将 flow 按顺序展开合并发送
    val flowFlow = flowOf(flow1, flow2)
    val flattenConcat = flowFlow.flattenConcat() // 顺序展开
    val flattenMerge = flowFlow.flattenMerge() // 穿插合并

    val mappedFlow = flow1.map { from ->
        (1..from).asFlow().map {
            "$from - $it"
        }
    }
    val concattedMappedFlow = mappedFlow.flattenConcat()
    // 等价操作 flatMapConcat
    val concattedMappedFlow2 = flow1.flatMapConcat { from ->
        (1..from).asFlow().map {
            "$from - $it"
        }
    }

    val mergedMappedFlow = flow1.flatMapMerge { from -> // 穿插
        (1..from).asFlow().map {
            "$from - $it"
        }
    }

    val mapLatestFlow = flow1.flatMapLatest { from ->
        (1..from).asFlow().map {
            "$from - $it"
        }
    }

    // combine 合并
    val combineFlow = flow1.combine(flow2) { a, b ->
        "$a - $b"
    }
    // 等价写法
    val combineFlow2 = combine(flow1, flow2) { a, b ->
        "$a - $b"
    }

    // zip, zip 会成对成对出现，combine 每个 flow 元素到来都会结合一次
    val zippedFlow = flow1.zip(flow2) { a, b ->
        "$a - $b"
    }

    // combineTransform，自定义 emit 发送规则
    flow1.combineTransform(flow2) { a, b ->
        emit("$a - $b")
    }

    scope.launch {
        // mergeFlow.collect {
        //     Logit.d("mergeFlow data: $it")
        // }
        // Logit.d(RepeatChar.getStr("="))

        // mergeFlowFromList.collect {
        //     Logit.d("mergeFlowFromList data: $it")
        // }

        // flattenConcat.collect {
        //     Logit.d("flattenConcat data: $it")
        // }

        // flattenMerge.collect {
        //     Logit.d("flattenMerge data: $it")
        // }

        // concattedMappedFlow.collect {
        //     Logit.d("concattedMappedFlow data: $it")
        // }

        // concattedMappedFlow2.collect {
        //     Logit.d("concattedMappedFlow2 data: $it")
        // }

        // mergedMappedFlow.collect {
        //     Logit.d("mergedMappedFlow data: $it")
        // }

        // mapLatestFlow.collect {
        //     Logit.d("mapLatestFlow data: $it")
        // }

        // combineFlow.collect {
        //     Logit.d("combineFlow data: $it")
        // }

        zippedFlow.collect {
            Logit.d("zippedFlow data: $it")
        }

    }
    delay(10000)
}