import org.dizitart.kno2.*
import org.dizitart.kno2.filters.*
import org.dizitart.no2.IndexType
import org.dizitart.no2.event.ChangeListener
import org.dizitart.no2.objects.Id
import org.dizitart.no2.objects.Index
import org.dizitart.no2.objects.Indices
import org.junit.After
import org.junit.Test
import java.io.File
import java.time.LocalDateTime
import org.hamcrest.CoreMatchers.`is` as Is
import org.junit.Assert.assertThat

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class NitriteTest {

    val testDbPath = "src/test/resources/testDb"

    val db = nitrite {
        file = File(testDbPath)
        autoCommitBufferSize = 2048
        compress = true
        autoCompact = false
    }

    @After
    fun cleanUp(){
        File(testDbPath).delete()
    }

    @Test
    fun insertObjectIntoKNO2(){
        val time = LocalDateTime.now()
        val testData = TestData("::id::", "::data and more in house cooldata::", time)

        val collection = db.getRepository(TestData::class.java)

        collection.register { changeInfo -> println(changeInfo.changedItems.first()) }

        collection.insert(testData)

        val cursor = collection.find(TestData::data text "cool*")
        val retrievedTestData = cursor.first()
        assertThat(retrievedTestData, Is(testData))
    }

}

@Indices(Index(value= "data", type = IndexType.Fulltext))
data class TestData(@Id val identity: String, val data: String, val time: LocalDateTime)