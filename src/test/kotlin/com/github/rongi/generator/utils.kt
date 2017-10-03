package com.github.rongi.generator

import java.io.File
import java.io.IOException
import java.util.*

fun getFile(fileName: String, clazz: Class<Test>): String {

	val result = StringBuilder("")

	//Get file from resources folder
	val classLoader = clazz.classLoader
	val file = File(classLoader.getResource(fileName)!!.file)

	try {
		Scanner(file).use({ scanner ->

			while (scanner.hasNextLine()) {
				val line = scanner.nextLine()
				result.append(line).append("\n")
			}

			scanner.close()

		})
	} catch (e: IOException) {
		e.printStackTrace()
	}

	return result.toString()

}

