package com.example.quickyscan

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class FileModelTest {

    @Test
    fun testFileModelInitialization() {
        // Arrange
        val fileName = "example.txt"
        val selected = true
        val path = "/path/to/file"
        val creationDate = "2023-11-20"

        // Act
        val fileModel = FileModel(fileName = fileName, selected = selected, path = path, creationDate = creationDate)

        // Assert
        Assertions.assertEquals(fileName, fileModel.fileName)
        Assertions.assertTrue(fileModel.selected)
        Assertions.assertEquals(path, fileModel.path)
        Assertions.assertEquals(creationDate, fileModel.creationDate)
    }

    @Test
    fun getId() {
        // Arrange
        val fileModel = FileModel(fileName = "example.txt", selected = true, path = "/path/to/file", creationDate = "2023-11-20")

        // Act
        val id = fileModel.id

        // Assert
        Assertions.assertNotNull(id)
    }

    @Test
    fun setId() {
        // Arrange
        val fileModel = FileModel(fileName = "example.txt", selected = true, path = "/path/to/file", creationDate = "2023-11-20")

        // Act
        fileModel.id = 42

        // Assert
        Assertions.assertEquals(42, fileModel.id)
    }

    @Test
    fun getFileName() {
        // Arrange
        val fileName = "example.txt"
        val fileModel = FileModel(fileName = fileName, selected = true, path = "/path/to/file", creationDate = "2023-11-20")

        // Act
        val resultFileName = fileModel.fileName

        // Assert
        Assertions.assertEquals(fileName, resultFileName)
    }

    @Test
    fun setFileName() {
        // Arrange
        val fileModel = FileModel(fileName = "example.txt", selected = true, path = "/path/to/file", creationDate = "2023-11-20")

        // Act
        fileModel.fileName = "newFileName.txt"

        // Assert
        Assertions.assertEquals("newFileName.txt", fileModel.fileName)
    }

    @Test
    fun getSelected() {
        // Arrange
        val fileModel = FileModel(fileName = "example.txt", selected = true, path = "/path/to/file", creationDate = "2023-11-20")

        // Act
        val selected = fileModel.selected

        // Assert
        Assertions.assertTrue(selected)
    }

    @Test
    fun setSelected() {
        // Arrange
        val fileModel = FileModel(fileName = "example.txt", selected = true, path = "/path/to/file", creationDate = "2023-11-20")

        // Act
        fileModel.selected = false

        // Assert
        Assertions.assertFalse(fileModel.selected)
    }

    @Test
    fun getPath() {
        // Arrange
        val path = "/path/to/file"
        val fileModel = FileModel(fileName = "example.txt", selected = true, path = path, creationDate = "2023-11-20")

        // Act
        val resultPath = fileModel.path

        // Assert
        Assertions.assertEquals(path, resultPath)
    }

    @Test
    fun setPath() {
        // Arrange
        val fileModel = FileModel(fileName = "example.txt", selected = true, path = "/path/to/file", creationDate = "2023-11-20")

        // Act
        fileModel.path = "/new/path"

        // Assert
        Assertions.assertEquals("/new/path", fileModel.path)
    }

    @Test
    fun getCreationDate() {
        // Arrange
        val creationDate = "2023-11-20"
        val fileModel = FileModel(fileName = "example.txt", selected = true, path = "/path/to/file", creationDate = creationDate)

        // Act
        val resultCreationDate = fileModel.creationDate

        // Assert
        Assertions.assertEquals(creationDate, resultCreationDate)
    }

    @Test
    fun setCreationDate() {
        // Arrange
        val fileModel = FileModel(fileName = "example.txt", selected = true, path = "/path/to/file", creationDate = "2023-11-20")

        // Act
        fileModel.creationDate = "2023-11-21"

        // Assert
        Assertions.assertEquals("2023-11-21", fileModel.creationDate)
    }
}