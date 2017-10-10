package com.github.rongi.generator

import com.squareup.javapoet.JavaFile

data class Generated(
	val domain: JavaFile,
	val entity: JavaFile,
	val transform: JavaFile,
	val fromChildren: List<Generated>
)