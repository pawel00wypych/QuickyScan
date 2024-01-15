package com.example.quickyscan

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.quickyscan.services.SQLiteHelper
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SQLiteHelperInstrumentedTest {

    private lateinit var dbHelper: SQLiteHelper
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        dbHelper = SQLiteHelper(context)
        dbHelper.writableDatabase.execSQL("DELETE FROM ${"tbl_files"}")
    }

    @After
    fun tearDown() {
        dbHelper.close()
    }

    @Test
    fun testInsertAndGetFile() {
        val fileModel = FileModel(
            fileName = "test.txt",
            selected = true,
            path = "/test/path",
            content = "Test content",
            creationDate = "2022-01-01"
        )

        val insertResult = dbHelper.insertFile(fileModel)
        assertTrue(insertResult > 0)

        val files = dbHelper.getAllFiles()

        val retrievedFile = files[0]
        assertEquals(fileModel.fileName, retrievedFile.fileName)
        assertEquals(fileModel.selected, retrievedFile.selected)
        assertEquals(fileModel.path, retrievedFile.path)
        assertEquals(fileModel.content, retrievedFile.content)
        assertEquals(fileModel.creationDate, retrievedFile.creationDate)
    }

    @Test
    fun testFindFile() {
        val fileModel = FileModel(
            fileName = "test.txt",
            selected = true,
            path = "/test/path",
            content = "Test content",
            creationDate = "2022-01-01"
        )

        dbHelper.insertFile(fileModel)

        val foundFiles = dbHelper.findFile("test")

        val foundFile = foundFiles[0]
        assertEquals(fileModel.fileName, foundFile.fileName)
        assertEquals(fileModel.selected, foundFile.selected)
        assertEquals(fileModel.path, foundFile.path)
        assertEquals(fileModel.content, foundFile.content)
        assertEquals(fileModel.creationDate, foundFile.creationDate)
    }

    @Test
    fun testDeleteFileByName() {
        val fileModel = FileModel(
            fileName = "test.txt",
            selected = true,
            path = "/test/path",
            content = "Test content",
            creationDate = "2022-01-01"
        )

        dbHelper.insertFile(fileModel)

        val deleteResult = dbHelper.deleteFileByName("test.txt")
        assertTrue(deleteResult > 0)

        val files = dbHelper.getAllFiles()
        assertTrue(files.isEmpty())
    }

    @Test
    fun testUpdateFileOnName() {
        val fileModel = FileModel(
            fileName = "test.txt",
            selected = true,
            path = "/test/path",
            content = "Test content",
            creationDate = "2022-01-01"
        )

        dbHelper.insertFile(fileModel)

        val updatedFileModel = FileModel(
            fileName = "updated_test.txt",
            selected = false,
            path = "/updated/test/path",
            content = "Updated test content",
            creationDate = "2022-02-02"
        )

        val updateResult = dbHelper.updateFileOnName(updatedFileModel, "test.txt")
        assertTrue(updateResult > 0)

        val updatedFiles = dbHelper.getAllFiles()

        val retrievedUpdatedFile = updatedFiles[0]
        assertEquals(updatedFileModel.fileName, retrievedUpdatedFile.fileName)
        assertEquals(updatedFileModel.selected, retrievedUpdatedFile.selected)
        assertEquals(updatedFileModel.path, retrievedUpdatedFile.path)
        assertEquals(updatedFileModel.content, retrievedUpdatedFile.content)
        assertEquals(updatedFileModel.creationDate, retrievedUpdatedFile.creationDate)
    }
}