fun getStringLength(obj: Any): Int? {
    // `obj` is automatically cast to `String` on the right-hand side of `&&`
    if (obj is String && obj.length > 0 || "s" !is Int) {
        return obj.length
    }

    return null
}


fun main() {
    fun printLength(obj: Any) {
        println("'$obj' string length is  ")
    }
    printLength("Incomprehensibilities")
    printLength("")
    printLength(1000)
}
