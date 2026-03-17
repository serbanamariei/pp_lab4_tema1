fun main() {

    val crawlerJson = Crawler("https://jsonplaceholder.typicode.com/todos/1")
    crawlerJson.processContent("json")

    val crawlerXml = Crawler("https://www.w3schools.com/xml/note.xml")
    crawlerXml.processContent("xml")

    val crawlerYaml = Crawler("https://raw.githubusercontent.com/prometheus/prometheus/main/documentation/examples/prometheus.yml")
    crawlerYaml.processContent("yaml")
}