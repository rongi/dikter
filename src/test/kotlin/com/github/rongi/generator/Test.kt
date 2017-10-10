package com.github.rongi.generator

import org.junit.Test
import kotlin.test.assertEquals

private const val DOMAIN_PACKAGE = "com.example.test.domain"
private const val ENTITY_PACKAGE = "com.example.test.entity"
private const val TRANSFORM_PACKAGE = "com.example.test.transform"

class Test {

	@Test
	fun response_file_names_render_correct() {
		val json = getFile("test.json")

		val generated = generate(json, DOMAIN_PACKAGE, ENTITY_PACKAGE, TRANSFORM_PACKAGE)

		val domain = generated.domain
		val entity = generated.entity
		val transform = generated.transform
		assertEquals("com/example/test/domain/ResponseRest.java", domain.toJavaFileObject().name)
		assertEquals("com/example/test/entity/Response.java", entity.toJavaFileObject().name)
		assertEquals("com/example/test/transform/ResponseTransform.java", transform.toJavaFileObject().name)
	}

	@Test
	fun response_renders_correct() {
		val json = getFile("test.json")

		val generated = generate(json, DOMAIN_PACKAGE, ENTITY_PACKAGE, TRANSFORM_PACKAGE)

		val domain = generated.domain
		val entity = generated.entity
		val expectedDomain = getFile("ResponseRest.java")
		val expectedEntity = getFile("Response.java")
		assertEquals(expectedDomain, domain.toString())
		assertEquals(expectedEntity, entity.toString())
	}

	@Test
	fun fields_render_correct() {
		val json = getFile("test.json")

		val generated = generate(json, DOMAIN_PACKAGE, ENTITY_PACKAGE, TRANSFORM_PACKAGE)

		assertEquals(1, generated.fromChildren.size)
		val expectedDomain = getFile("UserRest.java")
		val expectedEntity = getFile("User.java")
		val child = generated.fromChildren[0]
		val domain = child.domain
		val entity = child.entity
		assertEquals(expectedDomain, domain.toString())
		assertEquals(expectedEntity, entity.toString())
	}

	@Test
	fun transform_renders_correct() {
		val json = getFile("test.json")

		val generated = generate(json, DOMAIN_PACKAGE, ENTITY_PACKAGE, TRANSFORM_PACKAGE)

		assertEquals(1, generated.fromChildren.size)
		val expectedTransform = getFile("UserTransform.java")
		val child = generated.fromChildren[0]
		val transform = child.transform
		assertEquals(expectedTransform, transform.toString())
	}

	private fun getFile(name: String) = getFile(name, javaClass)

}