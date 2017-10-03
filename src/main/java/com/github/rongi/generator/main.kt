@file:JvmName("MainKt")

package com.github.rongi.generator

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*
import kotlin.system.exitProcess

private val DEFAULT_CONFIG_FILE = "dikter.properties"

fun main(args: Array<String>) {
	val domainPackage: String
	val entityPackage: String
	val transformPackage: String

	val outputDir: String
	val fileName: String

	val configFile = File(DEFAULT_CONFIG_FILE);
	if (configFile.exists() && !configFile.isDirectory) {
		val properties = readProperties()

		domainPackage = properties.getProperty("domainPackage")
		entityPackage = properties.getProperty("entityPackage")
		transformPackage = properties.getProperty("transformPackage")
		outputDir = properties.getProperty("src")

		if (args.size < 1) exitWithHelpMessage()
		fileName = args[0]
	} else {
		if (args.size < 4) exitWithHelpMessage()

		domainPackage = args[1]
		entityPackage = args[2]
		transformPackage = args[3]
		outputDir = "."

		fileName = args[0]
	}

	val generated = generate(fileName.asFile(), domainPackage, entityPackage, transformPackage)
	generated.dumpRecursively(outputDir)
}

fun Generated.dumpRecursively(outputDir: String) {
	val outputDirFile = outputDir.asFile()

	domain.writeTo(outputDirFile)
	println("Poured: ${domain.toJavaFileObject().name}")

	entity.writeTo(outputDirFile)
	println("Poured: ${entity.toJavaFileObject().name}")

	transform.writeTo(outputDirFile)
	println("Poured: ${transform.toJavaFileObject().name}")

	fromChildren.forEach { it.dumpRecursively(outputDir) }
}

fun exitWithHelpMessage() {
	exitWithMessage("usage: " +
			"dikter <json_file> <path_where_to_generate> <domain_package> <entity_package> <transform_package>\n" +
			"or\n" +
			"dikter <json_file>\n" +
			"if you have file dikter.properties in the working directory\n" +
			"with properties: src, domainPackage, entityPackage, then use this util with:\n")
}

private fun exitWithMessage(message: String) {
	println(message)
	exitProcess(0)
}


fun readProperties(): Properties {
	val properties = Properties()

	try {
		val input = FileInputStream(DEFAULT_CONFIG_FILE)
		properties.load(input)
		input.close()
	} catch (ex: IOException) {
		ex.printStackTrace()
	}

	return properties
}
