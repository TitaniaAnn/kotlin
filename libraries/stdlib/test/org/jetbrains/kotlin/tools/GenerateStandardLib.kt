package org.jetbrains.kotlin.tools

import java.io.*
import java.util.List

private val COMMON_AUTOGENERATED_WARNING: String = """//
// NOTE THIS FILE IS AUTO-GENERATED by the GenerateStandardLib.kt
// See: https://github.com/JetBrains/kotlin/tree/master/libraries/stdlib
//"""

fun generateFile(outFile: File, header: String, inputFile: File, f: (String)-> String) {
    generateFile(outFile, header, arrayList(inputFile), f)
}

fun generateFile(outFile: File, header: String, inputFile: File, jvmFile: File, f: (String)-> String) {
    generateFile(outFile, header, arrayList(inputFile, jvmFile), f)
}

fun generateFile(outFile: File, header: String, inputFiles: List<File>, f: (String)-> String) {
    outFile.getParentFile()?.mkdirs()
    val writer = PrintWriter(FileWriter(outFile))
    try {
        writer.println(header)

        for (file in inputFiles) {
            writer.println("""
$COMMON_AUTOGENERATED_WARNING
// Generated from input file: $file
//
""")

        println("Parsing $file and writing $outFile")
        val reader = FileReader(file).buffered()
        try {
            // TODO ideally we'd use a filterNot() here :)
            val iter = reader.lineIterator()
            while (iter.hasNext) {
                val line = iter.next()

                if (line.startsWith("package")) continue

                val xform = f(line)
                writer.println(xform)
            }
        } finally {
            reader.close()
            reader.close()
        }
        }
    } finally {
        writer.close()
    }
}


/**
 * Generates methods in the standard library which are mostly identical
 * but just using a different input kind.
 *
 * Kinda like mimicking source macros here, but this avoids the inefficiency of type conversions
 * at runtime.
 */
fun main(args: Array<String>) {
    var srcDir = File("src/kotlin")
    if (!srcDir.exists()) {
        srcDir = File("stdlib/src/kotlin")
        require(srcDir.exists(), "Could not find the src/kotlin directory!")
    }
    val outDir = File(srcDir, "../generated")

    val jsCoreDir = File(srcDir, "../../../../js/js.libraries/src/core")
    require(jsCoreDir.exists())
    generateDomAPI(File(jsCoreDir, "dom.kt"))
    generateDomEventsAPI(File(jsCoreDir, "domEvents.kt"))

    val otherArrayNames = arrayList("Boolean", "Byte", "Char", "Short", "Int", "Long", "Float", "Double")

    // JLangIterables - Generic iterable stuff
    generateFile(File(outDir, "ArraysFromJLangIterables.kt"), "package kotlin\n", File(srcDir, "JLangIterables.kt")) {
        it.replaceAll("java.lang.Iterable<T", "Array<T").
            replaceAll("java.lang.Iterable<T", "Array<T").
            replaceAll("iterator.hasNext\\(\\)", "iterator.hasNext")
    }
    generateFile(File(outDir, "ArraysFromJLangIterablesJVM.kt"), "package kotlin\n", File(srcDir, "JLangIterablesJVM.kt")) {
        it.replaceAll("java.lang.Iterable<T", "Array<T").
            replaceAll("java.lang.Iterable<T", "Array<T").
            replaceAll("iterator.hasNext\\(\\)", "iterator.hasNext")
    }
    generateFile(File(outDir, "ArraysFromJLangIterablesLazy.kt"), "package kotlin\n", File(srcDir, "JLangIterablesLazy.kt")) {
        it.replaceAll("java.lang.Iterable<T", "Array<T").replaceAll("java.lang.Iterable<T", "Array<T")
    }
    for (arrayName in otherArrayNames) {
        fun replace(it: String): String {
            replaceGenerics(arrayName, it.replaceAll("<T> java.lang.Iterable<T>", "${arrayName}Array").
            replaceAll("<T> java.lang.Iterable<T\\?>", "${arrayName}Array").
            replaceAll("java.lang.Iterable<T\\?>", "${arrayName}Array").
            replaceAll("java.lang.Iterable<T>", "${arrayName}Array")).
            replaceAll("iterator.hasNext\\(\\)", "iterator.hasNext")
        }

        generateFile(File(outDir, "${arrayName}ArraysFromJLangIterables.kt"), "package kotlin\n", File(srcDir, "JLangIterables.kt")) {
            replace(it)
        }
        generateFile(File(outDir, "${arrayName}ArraysFromJLangIterablesJVM.kt"), "package kotlin\n", File(srcDir, "JLangIterablesJVM.kt")) {
            replace(it)
        }
        generateFile(File(outDir, "${arrayName}ArraysFromJLangIterablesLazy.kt"), "package kotlin\n", File(srcDir, "JLangIterablesLazy.kt")) {
            replace(it)
        }
    }

    generateFile(File(outDir, "StandardFromJLangIterables.kt"), "package kotlin\n", File(srcDir, "JLangIterables.kt")) {
        it.replaceAll("java.lang.Iterable<T", "Iterable<T").
            replaceAll("iterator.hasNext\\(\\)", "iterator.hasNext")
    }
    generateFile(File(outDir, "StandardFromJLangIterablesJVM.kt"), "package kotlin\n", File(srcDir, "JLangIterablesJVM.kt")) {
        it.replaceAll("java.lang.Iterable<T", "Iterable<T").
            replaceAll("iterator.hasNext\\(\\)", "iterator.hasNext")
    }
    generateFile(File(outDir, "StandardFromJLangIterablesLazy.kt"), "package kotlin\n", File(srcDir, "JLangIterablesLazy.kt")) {
        it.replaceAll("java.lang.Iterable<T", "Iterable<T")
    }

    generateFile(File(outDir, "JUtilIteratorsFromJLangIterables.kt"), "package kotlin", File(srcDir, "JLangIterables.kt")) {
        it.replaceAll("java.lang.Iterable<T", "java.util.Iterator<T")
    }
    generateFile(File(outDir, "JUtilIteratorsFromJLangIterablesJVM.kt"), "package kotlin", File(srcDir, "JLangIterablesJVM.kt")) {
        it.replaceAll("java.lang.Iterable<T", "java.util.Iterator<T")
    }


    // JUtilCollections - methods returning a collection of the same input size (if its a collection)
    generateFile(File(outDir, "ArraysFromJUtilCollections.kt"), "package kotlin", File(srcDir, "JUtilCollections.kt")) {
        it.replaceAll("java.util.Collection<T", "Array<T")
    }
    generateFile(File(outDir, "ArraysFromJUtilCollectionsJVM.kt"), "package kotlin", File(srcDir, "JUtilCollectionsJVM.kt")) {
        it.replaceAll("java.util.Collection<T", "Array<T")
    }
    for (arrayName in otherArrayNames) {
        generateFile(File(outDir, "${arrayName}ArraysFromJUtilCollections.kt"), "package kotlin", File(srcDir, "JUtilCollections.kt")) {
            replaceGenerics(arrayName, it.replaceAll("<T> java.util.Collection<T>", "${arrayName}Array").
            replaceAll("java.util.Collection<T>", "${arrayName}Array"))
        }
        generateFile(File(outDir, "${arrayName}ArraysFromJUtilCollectionsJVM.kt"), "package kotlin", File(srcDir, "JUtilCollectionsJVM.kt")) {
            replaceGenerics(arrayName, it.replaceAll("<T> java.util.Collection<T>", "${arrayName}Array").
            replaceAll("java.util.Collection<T>", "${arrayName}Array"))
        }
    }

    generateFile(File(outDir, "JUtilIterablesFromJUtilCollections.kt"), "package kotlin", File(srcDir, "JUtilCollections.kt")) {
        it.replaceAll("java.util.Collection<T", "java.lang.Iterable<T").replaceAll("(this.size)", "")
    }
    generateFile(File(outDir, "JUtilIterablesFromJUtilCollectionsJVM.kt"), "package kotlin", File(srcDir, "JUtilCollectionsJVM.kt")) {
        it.replaceAll("java.util.Collection<T", "java.lang.Iterable<T").replaceAll("(this.size)", "")
    }

    generateFile(File(outDir, "StandardFromJUtilCollections.kt"), "package kotlin", File(srcDir, "JUtilCollections.kt")) {
        it.replaceAll("java.util.Collection<T", "Iterable<T").replaceAll("(this.size)", "")
    }
    generateFile(File(outDir, "StandardFromJUtilCollectionsJVM.kt"), "package kotlin",  File(srcDir, "JUtilCollectionsJVM.kt")) {
        it.replaceAll("java.util.Collection<T", "Iterable<T").replaceAll("(this.size)", "")
    }

    generateDownTos(File(outDir, "DownTo.kt"), "package kotlin")
}

// Pretty hacky way to code generate; ideally we'd be using the AST and just changing the function prototypes
fun replaceGenerics(arrayName: String, it: String): String {
    return it.replaceAll(" <in T>", " ").replaceAll("<in T, ", "<").replaceAll("<T, ", "<").replaceAll("<T,", "<").
    replaceAll(" <T> ", " ").
    replaceAll("<T>", "<${arrayName}>").replaceAll("<in T>", "<${arrayName}>").
    replaceAll("\\(T\\)", "(${arrayName})").replaceAll("T\\?", "${arrayName}?").
    replaceAll("T,", "${arrayName},").
    replaceAll("T\\)", "${arrayName})").
    replaceAll(" T ", " ${arrayName} ")
}

