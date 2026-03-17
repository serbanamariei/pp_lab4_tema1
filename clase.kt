import org.jsoup.Jsoup
import org.json.JSONObject
import org.jsoup.Connection
import org.jsoup.parser.Parser as JsoupParser

interface Parser
{
    fun parse(text: String): Map<String, Any?>
}

class JsonParser : Parser
{
    override fun parse(text: String): Map<String, Any?>
    {
        val json=JSONObject(text)
        return json.toMap()
    }
}

class XmlParser : Parser
{
    override fun parse(text: String): Map<String, Any?>
    {
        val doc=Jsoup.parse(text, "", JsoupParser.xmlParser())
        val rezultat=mutableMapOf<String, Any?>()

        doc.children().first()?.children()?.forEach { element ->
            rezultat[element.tagName()]=element.text()
        }
        return rezultat
    }
}

class YamlParser : Parser
{
    override fun parse(text: String): Map<String, Any?>
    {
        val rezultat=mutableMapOf<String, Any?>()
        text.lines().forEach { linie ->
            if(linie.contains(":"))
            {
                val parts=linie.split(":",limit=2)
                rezultat[parts[0].trim()]=parts[1].trim()
            }
        }
        return rezultat
    }
}

class HttpResponse(
    val body: String,
    val statusCode: Int
)

class Crawler(private val url: String)
{
    fun getResource(): HttpResponse {
        return try {
            val response=Jsoup.connect(url)
                .ignoreContentType(true)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .timeout(10000)
                .execute()

            HttpResponse(
                body=response.body(),
                statusCode=response.statusCode()
            )
        } catch (e: Exception) {
            println("eroare la $url: ${e.message}")
            HttpResponse("", statusCode = 0)
        }
    }

    fun processContent(contentType: String)
    {
        val response=getResource()

        if(response.statusCode!=200)
        {
            println("eroare la conexiunea cu serverul: ${response.statusCode}")
            return
        }

        val parser: Parser=when(contentType.lowercase())
        {
            "json" -> JsonParser()
            "xml" -> XmlParser()
            "yaml" -> YamlParser()
            else -> throw IllegalArgumentException("tip necunoscut: $contentType")
        }

        val rezultat=parser.parse(response.body)

        println("am procesat continutul $contentType si am obtinut : $rezultat")
    }
}

