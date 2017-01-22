package com.artemis.compile

infix operator fun StringBuilder.plusAssign(s: String) {
	this.append(s)
}

fun StringBuilder.appendf(format: String, vararg params: Any?) {
	this.append(String.format(format, params))
}