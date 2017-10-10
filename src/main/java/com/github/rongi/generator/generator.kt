@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package com.github.rongi.generator

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeType.ARRAY
import com.fasterxml.jackson.databind.node.JsonNodeType.OBJECT
import com.squareup.javapoet.*
import java.io.File
import java.util.*
import java.util.List
import javax.lang.model.element.Modifier.*

private const val ROOT_CLASS_NAME = "ApiResponse"
const val INDENT = "    "
const val DOMAIN_SUFFIX = "Rest"

fun generate(json: File, domainPackage: String, entityPackage: String, transformPackage: String): Generated {
	val root = ObjectMapper().readTree(json)
	return generate(
		root,
		ROOT_CLASS_NAME,
		domainPackage,
		entityPackage,
		transformPackage
	)
}

fun generate(json: String, domainPackage: String, entityPackage: String, transformPackage: String): Generated {
	val root = ObjectMapper().readTree(json)
	return generate(
		root,
		ROOT_CLASS_NAME,
		domainPackage,
		entityPackage,
		transformPackage
	)
}

private fun generate(json: JsonNode, pojoName: String, domainPackage: String, entityPackage: String, transformPackage: String): Generated {
	val fromChildren = ArrayList<Generated>()

	json.fields().forEach {
		val node = it.value!!
		val nodeName = it.key!!

		val fromChild = generateFromChild(node, nodeName, domainPackage, entityPackage, transformPackage)
		if (fromChild != null) fromChildren.add(fromChild)
	}

	val domainFile = generateDomain(json, pojoName, domainPackage)
	val entityFile = generateEntity(json, pojoName, entityPackage)
	val transform = generateTransform(pojoName, domainFile, entityFile, transformPackage)

	return Generated(domainFile, entityFile, transform, fromChildren)
}

private fun generateDomain(json: JsonNode, pojoName: String, packageName: String): JavaFile {
	val fieldSpecs = mutableListOf<FieldSpec>()

	json.fields().forEach {
		val node = it.value!!
		val nodeName = it.key!!

		val fieldType = javaTypeFromJsonType(node, nodeName, packageName, ::domainNameFromPojoName)
		val serializedAnnotation = AnnotationSpec.builder(ClassName.bestGuess("com.google.gson.annotations.SerializedName"))
			.addMember("value", "\$S", nodeName).build()
		val fieldSpec = FieldSpec.builder(fieldType, nodeName, PUBLIC)
			.addAnnotation(serializedAnnotation)
			.build()

		fieldSpecs.add(fieldSpec)
	}

	val className = domainNameFromPojoName(pojoName)
	val typeSpecBuilder = TypeSpec.classBuilder(className)
		.addFields(fieldSpecs)
		.addModifiers(PUBLIC, FINAL)
		.build();

	val javaFile = JavaFile.builder(packageName, typeSpecBuilder)
		.indent(INDENT)
		.skipJavaLangImports(true)
		.build()

	return javaFile
}

private fun generateEntity(json: JsonNode, pojoName: String, packageName: String): JavaFile {
	val fieldSpecs = mutableListOf<FieldSpec>()
	val methodSpecs = mutableListOf<MethodSpec>()

	json.fields().forEach {
		val node = it.value!!
		val nodeName = it.key!!

		val fieldName = nodeName;
		val fieldType = javaTypeFromJsonType(node, nodeName, packageName, ::entityNameFromPojoName)
		val fieldSpec = FieldSpec.builder(fieldType, fieldName, PRIVATE).build()

		val getterName = getterNameFor(fieldName)
		val getterSpec = MethodSpec.methodBuilder(getterName)
			.addModifiers(PUBLIC)
			.returns(fieldType)
			.addStatement("return $fieldName")
			.build()

		val setterName = setterNameFor(fieldName)
		val setterSpec = MethodSpec.methodBuilder(setterName)
			.addModifiers(PUBLIC)
			.addParameter(fieldType, fieldName)
			.addStatement("this.$fieldName = $fieldName")
			.build()

		fieldSpecs.add(fieldSpec)
		methodSpecs.add(getterSpec)
		methodSpecs.add(setterSpec)
	}

	val className = entityNameFromPojoName(pojoName)
	val typeSpecBuilder = TypeSpec.classBuilder(className)
		.addFields(fieldSpecs)
		.addMethods(methodSpecs)
		.addModifiers(PUBLIC, FINAL)
		.build();

	val javaFile = JavaFile.builder(packageName, typeSpecBuilder)
		.indent(INDENT)
		.skipJavaLangImports(true)
		.build()

	return javaFile
}

fun setterNameFor(fieldName: String): String {
	return "set" + fieldName.capitalize()
}

fun getterNameFor(fieldName: String): String {
	return "get" + fieldName.capitalize()
}

private fun javaTypeFromJsonType(node: JsonNode, nodeName: String, packageName: String, pojoToClass: (String) -> String): TypeName {
	val javaType: TypeName

	when {
		node.nodeType == OBJECT || node.isNull -> {
			javaType = ClassName.get(packageName, pojoToClass(pojoNameFromNodeName(nodeName)))
		}
		node.nodeType == ARRAY -> {
			val listClassName = ClassName.get(List::class.java);
			val fieldItemClassName = pojoToClass(pojoNameFromArrayNodeName(nodeName))
			val fieldItemType = ClassName.get(packageName, fieldItemClassName)
			javaType = ParameterizedTypeName.get(listClassName, fieldItemType)
		}
		else -> javaType = fieldTypeFromJsonToken(node)
	}

	return javaType
}

private fun fieldTypeFromJsonToken(node: JsonNode): TypeName {
	return when {
		node.isTextual -> TypeName.get(String::class.java)
		node.isInt -> TypeName.LONG
		node.isLong -> TypeName.LONG
		node.isBoolean -> TypeName.BOOLEAN
		node.isDouble -> TypeName.DOUBLE
		else -> throw RuntimeException("Unsupported field format: ${node.nodeType}, value: ${node.asText()}")
	}
}


private fun generateFromChild(node: JsonNode, nodeName: String, domainPackage: String, entityPackage: String, transformPackage: String): Generated? {
	if (node.nodeType == OBJECT || node.isNull) {
		val pojoName = pojoNameFromNodeName(nodeName)
		return generate(node, pojoName, domainPackage, entityPackage, transformPackage)
	} else if (node.nodeType == ARRAY) {
		val nodeElements = node.elements()

		val element = if (nodeElements.hasNext()) {
			nodeElements.next()
		} else {
			ObjectMapper().createObjectNode()
		}

		val pojoName = pojoNameFromArrayNodeName(nodeName)
		return generate(element, pojoName, domainPackage, entityPackage, transformPackage)
	} else {
		return null
	}
}

fun pojoNameFromNodeName(nodeName: String): String {
	return nodeName.capitalize()
}

fun pojoNameFromArrayNodeName(nodeName: String): String {
	return nodeName.removeSuffix("s").capitalize()
}

private fun entityNameFromPojoName(pojoName: String): String {
	return pojoName
}

private fun domainNameFromPojoName(pojoName: String): String {
	return "$pojoName${DOMAIN_SUFFIX}"
}
