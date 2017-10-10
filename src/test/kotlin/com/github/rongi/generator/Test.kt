package com.github.rongi.generator

import org.junit.Assert
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
		assertEquals("com/example/test/domain/ApiResponseRest.java", domain.toJavaFileObject().name)
		assertEquals("com/example/test/entity/ApiResponse.java", entity.toJavaFileObject().name)
		assertEquals("com/example/test/transform/ApiResponseTransform.java", transform.toJavaFileObject().name)
	}

	@Test
	fun response_creates_proper_child_classes() {
		val json = getFile("test.json")

		val generated = generate(json, DOMAIN_PACKAGE, ENTITY_PACKAGE, TRANSFORM_PACKAGE)

		val user = generated.fromChildren[0]
		val userChildren = user.fromChildren
		userChildren.map { it.entity.toJavaFileObject().name }.assertEquals(listOf(
			"com/example/test/entity/Subscriber.java",
			"com/example/test/entity/Status.java",
			"com/example/test/entity/Comment.java",
			"com/example/test/entity/Image.java"
		))
		userChildren.map { it.domain.toJavaFileObject().name }.assertEquals(listOf(
			"com/example/test/domain/SubscriberRest.java",
			"com/example/test/domain/StatusRest.java",
			"com/example/test/domain/CommentRest.java",
			"com/example/test/domain/ImageRest.java"
		))
	}

	@Test
	fun response_renders_correct() {
		val json = getFile("test.json")

		val generated = generate(json, DOMAIN_PACKAGE, ENTITY_PACKAGE, TRANSFORM_PACKAGE)

		val domain = generated.domain
		val entity = generated.entity
		val expectedDomain = getFile("ApiResponseRest.java")
		val expectedEntity = getFile("ApiResponse.java")
		assertEquals(expectedDomain, domain.toString())
		assertEquals(expectedEntity, entity.toString())
	}

	@Test
	fun fields_renders_correct() {
		val json = getFile("test.json")

		val generated = generate(json, DOMAIN_PACKAGE, ENTITY_PACKAGE, TRANSFORM_PACKAGE)

		assertEquals(1, generated.fromChildren.size)
		val expectedDomain = getFile("UserRest.java")
		val expectedEntity = getFile("User.java")
		val user = generated.fromChildren[0]
		val domain = user.domain
		val entity = user.entity
		assertEquals(expectedDomain, domain.toString())
		assertEquals(expectedEntity, entity.toString())
	}

	@Test
	fun class_from_null_renders_correct() {
		val json = getFile("test.json")

		val generated = generate(json, DOMAIN_PACKAGE, ENTITY_PACKAGE, TRANSFORM_PACKAGE)

		assertEquals(1, generated.fromChildren.size)
		val expectedDomain = getFile("ImageRest.java")
		val expectedEntity = getFile("Image.java")
		val user = generated.fromChildren[0]
		val image = user.fromChildren[3]
		assertEquals(expectedDomain, image.domain.toString())
		assertEquals(expectedEntity, image.entity.toString())
	}

	@Test
	fun class_from_empty_array_renders_correct() {
		val json = getFile("test.json")

		val generated = generate(json, DOMAIN_PACKAGE, ENTITY_PACKAGE, TRANSFORM_PACKAGE)

		assertEquals(1, generated.fromChildren.size)
		val expectedDomain = getFile("CommentRest.java")
		val expectedEntity = getFile("Comment.java")
		val user = generated.fromChildren[0]
		val image = user.fromChildren[2]
		assertEquals(expectedDomain, image.domain.toString())
		assertEquals(expectedEntity, image.entity.toString())
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

	// TODO-DMITRY handle non object arrays

	private fun getFile(name: String) = getFile(name, javaClass)

}

private fun Any.assertEquals(other: Any) {
	Assert.assertEquals(other, this)
}
