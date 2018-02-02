package com.hendraanggrian.buildconfig

import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec.constructorBuilder
import com.squareup.javapoet.TypeSpec.classBuilder
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import java.nio.file.Files.deleteIfExists
import java.nio.file.Paths.get
import java.time.LocalDateTime.now
import java.time.format.DateTimeFormatter.ofPattern
import javax.lang.model.element.Modifier.*

class BuildConfigPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val ext = project.extensions.create("buildconfig", BuildConfigExtension::class.java)
        project.afterEvaluate {
            val outputDir = project.projectDir.resolve(ext.srcDir)
            project.task("buildconfig").apply {
                doFirst {
                    check(ext.packageName.isNotBlank()) { "Package name must not be blank." }
                    deleteIfExists(get(outputDir.absolutePath, *ext.packageName.split('.').toTypedArray(), "BuildConfig.java"))
                }
                doLast {
                    generateClass(ext.packageName, ext.fieldMap, outputDir)
                }
            }
        }
    }

    companion object {
        private fun generateClass(packageName: String, map: Map<String, Pair<Class<*>, Any>>, outputDir: File) = JavaFile
                .builder(packageName, classBuilder("BuildConfig")
                        .addModifiers(PUBLIC, FINAL)
                        .addMethod(constructorBuilder().addModifiers(PRIVATE).build())
                        .apply {
                            map.keys.forEach { name ->
                                val (type, value) = map[name]!!
                                addField(FieldSpec.builder(type, name, PUBLIC, STATIC, FINAL)
                                        .initializer(when (type) {
                                            String::class.java -> "\$S"
                                            Char::class.java -> "'\$L'"
                                            else -> "\$L"
                                        }, value)
                                        .build())
                            }
                        }
                        .build())
                .addFileComment("buildconfig generated this class at ${now().format(ofPattern("MM-dd-yyyy 'at' h.mm.ss a"))}")
                .build()
                .writeTo(outputDir)
    }
}