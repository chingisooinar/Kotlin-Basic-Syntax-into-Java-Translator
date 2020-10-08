fun main() {
    var a = 1
    // simple name in template:
    val s1 = "a is $a" 

    a = 2
    // arbitrary expression in template:
    val s2 = s1.replace("is", "was")
    println(s2)
}
