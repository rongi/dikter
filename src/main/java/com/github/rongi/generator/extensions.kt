package com.github.rongi.generator

import com.squareup.javapoet.MethodSpec
import java.io.File

fun MethodSpec.Builder.newLine(): MethodSpec.Builder {
	this.addCode("\n")
	return this
}

fun String.asFile(): File {
	return File(this)
}