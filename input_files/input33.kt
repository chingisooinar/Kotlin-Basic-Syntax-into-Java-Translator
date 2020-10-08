fun feeling(day: String): String {
	when(day) {
		"Mon"-> return "sad"
		"Sat"    -> return"happy"
		else-> return"bad"
	}
}
fun main() {
	println(feeling("Wed"))
	println(feeling("Sat"))
}
