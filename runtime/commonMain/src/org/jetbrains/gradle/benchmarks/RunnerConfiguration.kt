package org.jetbrains.gradle.benchmarks

import kotlinx.cli.*

class RunnerConfiguration : CommandLineInterface("Client") {
    val name by onFlagValue("-n", "name", "Name of the configuration").store()
    val reportFile by onFlagValue("-r", "reportFile", "File to save report to").store()
    val traceFormat by onFlagValue("-t", "traceFormat", "Format of tracing report (text or xml)").store("text")

    val params by onFlagValue(
        "-P",
        "<name>=<value>",
        "Specifies parameter with the given name and value"
    ).toKeyValuePair().storeToMap()

    val include by onFlagValue("-I", "<pattern>", "Include benchmarks matching the given pattern").storeToList()
    val exclude by onFlagValue("-E", "<pattern>", "Include benchmarks matching the given pattern").storeToList()

    val iterations by onFlagValue("-i", "iterations", "Number of iterations per benchmark").map { it.toInt() }.store()
    val warmups by onFlagValue("-w", "warmups", "Number of warmup iterations per benchmark").map { it.toInt() }.store()

    val iterationTime by onFlagValue(
        "-it",
        "iterationTime",
        "Time to run one iteration in milliseconds"
    ).map { it.toLong() }.store()

    val iterationTimeUnit by onFlagValue(
        "-itu",
        "iterationTimeUnit",
        "Time unit for iteration time"
    ).map { parseTimeUnit(it) }.store()

    private fun parseTimeUnit(text: String) = when (text) {
        BenchmarkTimeUnit.SECONDS.name, "s" -> BenchmarkTimeUnit.SECONDS
        BenchmarkTimeUnit.MICROSECONDS.name, "us" -> BenchmarkTimeUnit.MICROSECONDS
        BenchmarkTimeUnit.MILLISECONDS.name, "ms" -> BenchmarkTimeUnit.MILLISECONDS
        BenchmarkTimeUnit.NANOSECONDS.name, "ns" -> BenchmarkTimeUnit.NANOSECONDS
        BenchmarkTimeUnit.MINUTES.name, "m" -> BenchmarkTimeUnit.MINUTES
        else -> throw UnsupportedOperationException("Unknown time unit: $text")
    }

    val outputTimeUnit by onFlagValue(
        "-otu",
        "outputTimeUnit",
        "Time unit for output values, one of: NANOSECONDS or ns, MICROSECONDS or us, MILLISECONDS or ms, SECONDS or s, MINUTES or m"
    ).map { parseTimeUnit(it) }.store()

    val mode by onFlagValue(
        "-m",
        "mode",
        "Result display mode, one of: Throughput, AverageTime"
    ).map { Mode.valueOf(it) }.store()


    private fun Event<String>.toKeyValuePair() =
        map {
            val parts = it.split('=', limit = 2)
            if (parts.size == 1)
                parts[0] to ""
            else
                parts[0] to parts[1]
        }

    private fun Event<Pair<String, String>>.storeToMap(): ArgumentValue<Map<String, String>> {
        val map = hashMapOf<String, String>()
        add { (key, value) -> map[key] = value }
        return ArgumentStorage(map)
    }

    private fun Event<String>.storeToList(): ArgumentValue<List<String>> {
        val list = mutableListOf<String>()
        add { value -> list.add(value) }
        return ArgumentStorage(list)
    }

    override fun toString(): String {
        return """$name -> $reportFile ($traceFormat)
params: ${params.entries.joinToString(prefix = "{", postfix = "}") { "${it.key}: ${it.value}" }}
include: $include
exclude: $exclude
iterations: $iterations            
warmups: $warmups            
iterationTime: $iterationTime            
iterationTimeUnit: $iterationTimeUnit            
outputTimeUnit: $outputTimeUnit            
mode: $mode            
"""
    }
}