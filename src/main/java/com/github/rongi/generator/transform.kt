@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package com.github.rongi.generator

import com.squareup.javapoet.*
import java.util.*
import java.util.List
import javax.lang.model.element.Modifier.*

private const val TRANSFORM_SUFFIX = "Transform"

fun generateTransform(pojoName: String, domainFile: JavaFile, entityFile: JavaFile, packageName: String): JavaFile {
	val methodSpecs = mutableListOf<MethodSpec>()

	val domainType = ClassName.get(domainFile.packageName, domainFile.typeSpec.name)
	val entityType = ClassName.get(entityFile.packageName, entityFile.typeSpec.name)

	val fromRestSpecBuilder = MethodSpec.methodBuilder("fromRest")
			.addModifiers(PUBLIC, STATIC)
			.addParameter(domainType, "rest")
			.addStatement("if (rest == null) return null")
			.addStatement("\$T entity = new \$T()", entityType, entityType)
			.addCode("\n")
			.addFromRestCopyStatements(entityFile.typeSpec.fieldSpecs, entityFile)
			.addCode("\n")
			.addStatement("return entity")
			.returns(entityType);

	val fromEntitySpecBuilder = MethodSpec.methodBuilder("fromEntity")
			.addModifiers(PUBLIC, STATIC)
			.addParameter(entityType, "entity")
			.addStatement("if (entity == null) return null")
			.addStatement("\$T rest = new \$T()", domainType, domainType)
			.addCode("\n")
			.addFromEntityCopyStatements(entityFile.typeSpec.fieldSpecs, entityFile)
			.addCode("\n")
			.addStatement("return rest")
			.returns(domainType);

	methodSpecs.add(fromRestSpecBuilder.build())
	methodSpecs.add(fromEntitySpecBuilder.build())
	methodSpecs.addAll(complexTransformsFromDomain(entityFile.typeSpec.fieldSpecs, domainFile, entityFile, packageName))
	methodSpecs.addAll(complexTransformsFromEntity(domainFile.typeSpec.fieldSpecs, entityFile, domainFile, packageName))

	val className = "$pojoName${TRANSFORM_SUFFIX}"
	val typeSpecBuilder = TypeSpec.classBuilder(className)
			.addModifiers(PUBLIC, ABSTRACT)
			.addMethods(methodSpecs)
			.build();

	val javaFile = JavaFile.builder(packageName, typeSpecBuilder)
			.indent(INDENT)
			.skipJavaLangImports(true)
			.build()

	return javaFile
}

fun complexTransformsFromDomain(toFields: MutableList<FieldSpec>, fromFile: JavaFile, toFile: JavaFile, transformPackage: String): Collection<MethodSpec> {
	val methods = mutableListOf<MethodSpec>()

	toFields.forEach {
		val type = it.type

		if (isList(it)) {
			val fieldType = type as ParameterizedTypeName
			val fieldItemType = fieldType.typeArguments[0] as ClassName

			val listClassName = ClassName.get(List::class.java);
			val domainItemType = ClassName.get(fromFile.packageName, "${fieldItemType.simpleName()}${DOMAIN_SUFFIX}")
			val parameterType = ParameterizedTypeName.get(listClassName, domainItemType)
			val fieldTransformType = ClassName.get(transformPackage, "${fieldItemType.simpleName()}${TRANSFORM_SUFFIX}")

			val methodSpec = MethodSpec.methodBuilder("${it.name}FromRest")
					.addModifiers(PRIVATE, STATIC)
					.addParameter(parameterType, "rests")
					.returns(type)
					.addStatement("\$T entities = new \$T<>()", fieldType, ArrayList::class.java)
					.newLine()
					.beginControlFlow("if (rests != null)")
					.beginControlFlow("for (\$T rest : rests)", domainItemType)
					.addStatement("\$T entity = \$T.fromRest(rest)", fieldItemType, fieldTransformType)
					.addStatement("entities.add(entity)")
					.endControlFlow()
					.endControlFlow()
					.newLine()
					.addStatement("return entities")
					.build()

			methods.add(methodSpec)
		} else if (type is ClassName && type.packageName() == toFile.packageName) {
			val domainType = ClassName.get(fromFile.packageName, "${type.simpleName()}${DOMAIN_SUFFIX}")
			val fieldTransformType = ClassName.get(transformPackage, "${type.simpleName()}${TRANSFORM_SUFFIX}")

			val methodSpec = MethodSpec.methodBuilder("${it.name}FromRest")
					.addModifiers(PRIVATE, STATIC)
					.addParameter(domainType, "rest")
					.returns(type)
					.addStatement("if (rest == null) return null")
					.addStatement("return \$T.fromRest(rest)", fieldTransformType)
					.build()

			methods.add(methodSpec)
		}
	}

	return methods;
}

fun complexTransformsFromEntity(toFields: MutableList<FieldSpec>, fromFile: JavaFile, toFile: JavaFile, transformPackage: String): Collection<MethodSpec> {
	val methods = mutableListOf<MethodSpec>()

	toFields.forEach {
		val type = it.type

		if (isList(it)) {
			val fieldType = type as ParameterizedTypeName
			val fieldItemType = fieldType.typeArguments[0] as ClassName

			val listClassName = ClassName.get(List::class.java);
			val fromItemType = ClassName.get(fromFile.packageName, "${fieldItemType.simpleName().removeSuffix(DOMAIN_SUFFIX)}")
			val parameterType = ParameterizedTypeName.get(listClassName, fromItemType)
			val fieldTransformType = ClassName.get(transformPackage, "${fieldItemType.simpleName().removeSuffix(DOMAIN_SUFFIX)}${TRANSFORM_SUFFIX}")

			val methodSpec = MethodSpec.methodBuilder("${it.name}FromEntity")
					.addModifiers(PRIVATE, STATIC)
					.addParameter(parameterType, "entities")
					.returns(type)
					.addStatement("\$T rests = new \$T<>()", fieldType, ArrayList::class.java)
					.newLine()
					.beginControlFlow("if (entities != null)")
					.beginControlFlow("for (\$T entity : entities)", fromItemType)
					.addStatement("\$T rest = \$T.fromEntity(entity)", fieldItemType, fieldTransformType)
					.addStatement("rests.add(rest)")
					.endControlFlow()
					.endControlFlow()
					.newLine()
					.addStatement("return rests")
					.build()

			methods.add(methodSpec)
		} else if (type is ClassName && type.packageName() == toFile.packageName) {
			val domainType = ClassName.get(fromFile.packageName, "${type.simpleName().removeSuffix(DOMAIN_SUFFIX)}")
			val fieldTransformType = ClassName.get(transformPackage, "${type.simpleName().removeSuffix(DOMAIN_SUFFIX)}${TRANSFORM_SUFFIX}")

			val methodSpec = MethodSpec.methodBuilder("${it.name}FromEntity")
					.addModifiers(PRIVATE, STATIC)
					.addParameter(domainType, "entity")
					.returns(type)
					.addStatement("if (entity == null) return null")
					.addStatement("return \$T.fromEntity(entity)", fieldTransformType)
					.build()

			methods.add(methodSpec)
		}
	}

	return methods;
}

fun MethodSpec.Builder.addFromRestCopyStatements(fields: MutableList<FieldSpec>, entityFile: JavaFile): MethodSpec.Builder {
	fields.forEach {
		val type = it.type
		val parameter = if (isList(it)) {
			"${it.name}FromRest(rest.${it.name})"
		} else if (type is ClassName && type.packageName() == entityFile.packageName) {
			"${it.name}FromRest(rest.${it.name})"
		} else {
			"rest.${it.name}"
		}

		addStatement("entity.set${it.name.capitalize()}($parameter)")
	}
	return this
}

fun MethodSpec.Builder.addFromEntityCopyStatements(fields: kotlin.collections.List<FieldSpec>, entityFile: JavaFile): MethodSpec.Builder {
	fields.forEach {
		val type = it.type
		val parameter = if (isList(it)) {
			"${it.name}FromEntity(entity.get${it.name.capitalize()}())"
		} else if (type is ClassName && type.packageName() == entityFile.packageName) {
			"${it.name}FromEntity(entity.get${it.name.capitalize()}())"
		} else {
			"entity.get${it.name.capitalize()}()"
		}

		addStatement("rest.${it.name} = $parameter")
	}
	return this
}

private fun isList(fieldSpec: FieldSpec): Boolean {
	val type = fieldSpec.type
	return if (type is ParameterizedTypeName) {
		type.rawType == ClassName.get(List::class.java)
	} else {
		false
	}
}