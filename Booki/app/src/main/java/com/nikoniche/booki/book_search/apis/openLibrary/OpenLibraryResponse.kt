data class OpenLibraryResponse(
    val records: Map<String, BookRecord>,
    val items: List<Any> // Assuming `items` is always an empty list or an array of unknown type
)

data class BookRecord(
    val isbns: List<String>,
    val issns: List<String>,
    val lccns: List<String>,
    val oclcs: List<String>,
    val olids: List<String>,
    val publishDates: List<String>,
    val recordURL: String,
    val data: BookData,
    val details: BookDetails
)

data class BookData(
    val url: String,
    val key: String,
    val title: String,
    val authors: List<Author>,
    val number_of_pages: Int,
    val identifiers: Identifiers,
    val classifications: Classifications,
    val publishers: List<Publisher>,
    val publish_date: String,
    val subjects: List<Subject>,
    val subject_people: List<SubjectPerson>,
    val excerpts: List<Excerpt>?,
    val cover: Cover?
)

data class Author(
    val url: String,
    val name: String
)

data class Identifiers(
    val librarything: List<String>,
    val goodreads: List<String>,
    val isbn_10: List<String>?,
    val isbn_13: List<String>?,
    val oclc: List<String>,
    val openlibrary: List<String>
)

data class Classifications(
    val lc_classifications: List<String>
)

data class Publisher(
    val name: String
)

data class Subject(
    val name: String,
    val url: String
)

data class SubjectPerson(
    val name: String,
    val url: String
)

data class Excerpt(
    val text: String,
    val comment: String,
    val first_sentence: Boolean
)

data class Cover(
    val small: String,
    val medium: String,
    val large: String
)

data class BookDetails(
    val bib_key: String,
    val info_url: String,
    val preview: String,
    val preview_url: String,
    val thumbnail_url: String,
    val details: BookDetailInfo
)

data class BookDetailInfo(
    val publishers: List<String>,
    val number_of_pages: Int,
    val covers: List<Int>,
    val lc_classifications: List<String>,
    val key: String,
    val authors: List<DetailAuthor>,
    val classifications: Map<String, Any>,
    val source_records: List<String>,
    val title: String,
    val identifiers: DetailIdentifiers,
    val isbn_13: List<String>,
    val isbn_10: List<String>,
    val publish_date: String,
    val oclc_numbers: List<String>,
    val works: List<Work>,
    val type: Type,
    val latest_revision: Int,
    val revision: Int,
    val created: DateTime,
    val last_modified: DateTime
)

data class DetailAuthor(
    val key: String,
    val name: String
)

data class DetailIdentifiers(
    val librarything: List<String>,
    val goodreads: List<String>
)

data class Work(
    val key: String
)

data class Type(
    val key: String
)

data class DateTime(
    val type: String,
    val value: String
)
