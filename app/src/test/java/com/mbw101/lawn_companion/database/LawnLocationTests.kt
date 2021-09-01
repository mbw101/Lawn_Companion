package com.mbw101.lawn_companion.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.runner.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
class LawnLocationTests {

    private lateinit var db: AppDatabase
    private lateinit var lawnLocationDAO: LawnLocationDAO

    @Before
    fun setUp() {
        val context: Context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        lawnLocationDAO = db.lawnLocationDao()
    }

    @Test
    @Throws(Exception::class)
    //  Tests the get all cuts by adding 3 CutEntries and ensuring a list with 3 entries are returned
    fun insertAndGetLocation() = runBlocking {
        lawnLocationDAO.insertAll(
            LawnLocation(43.22, 17.2),
            LawnLocation(43.22223, 17.21)
        )

        val returnedList = lawnLocationDAO.getAllLocations()
        Assert.assertEquals(lawnLocationDAO.getNumEntries(), 2)
        Assert.assertEquals(returnedList!!.size, 2)
    }

    @Test
    @Throws(Exception::class)
    //  Tests the get all cuts by adding 3 CutEntries and ensuring a list with 3 entries are returned
    fun insertAndDeleteLocations() = runBlocking {
        lawnLocationDAO.insertAll(
            LawnLocation(43.22, 17.2),
            LawnLocation(43.22223, 17.21)
        )

        lawnLocationDAO.deleteAll()
        var location = lawnLocationDAO.getFirstLocation()
        Assert.assertEquals(lawnLocationDAO.getNumEntries(), 0)
        Assert.assertEquals(location, null)
    }

    @Test
    @Throws(Exception::class)
    //  Tests the get all cuts by adding 3 CutEntries and ensuring a list with 3 entries are returned
    fun insertAndDeleteSpecificLocation() = runBlocking {
        lawnLocationDAO.insertAll(
            LawnLocation(43.22, 17.2),
            LawnLocation(43.22223, 17.21)
        )

        lawnLocationDAO.deleteLocations(LawnLocation(43.22223, 17.21))
        var location = lawnLocationDAO.getFirstLocation()
        Assert.assertEquals(lawnLocationDAO.getNumEntries(), 1)
        Assert.assertEquals(location, LawnLocation(43.22, 17.2))

        // delete one that doesn't exist
        lawnLocationDAO.deleteLocations(LawnLocation(1.2, 2.1))
        location = lawnLocationDAO.getFirstLocation()
        Assert.assertEquals(lawnLocationDAO.getNumEntries(), 1)
        Assert.assertEquals(location, LawnLocation(43.22, 17.2))
    }

    @Test
    @Throws(Exception::class)
    //  Tests the get all cuts by adding 3 CutEntries and ensuring a list with 3 entries are returned
    fun insertOneLocation() = runBlocking {
        lawnLocationDAO.insertLocation(LawnLocation(43.22, 17.2))

        var location = lawnLocationDAO.getFirstLocation()
        Assert.assertEquals(lawnLocationDAO.getNumEntries(), 1)
        Assert.assertEquals(location, LawnLocation(43.22, 17.2))

        lawnLocationDAO.insertLocation(LawnLocation(44.22, 19.21))

        Assert.assertEquals(lawnLocationDAO.getNumEntries(), 2)
    }

    @After
    fun tearDown() {
        db.close()
    }
}