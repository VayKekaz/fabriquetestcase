package studio.fabrique.backend.testcase.configuration.jackson

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.module.kotlin.addDeserializer
import com.fasterxml.jackson.module.kotlin.addSerializer
import org.springframework.data.domain.*


/**
 * https://github.com/spring-cloud/spring-cloud-openfeign/blob/main/spring-cloud-openfeign-core/src/main/java/org/springframework/cloud/openfeign/support/PageJacksonModule.java
 */
object SpringDataDomainModule : SimpleModule() {

    init {
        setMixInAnnotation(Page::class.java, PageMixIn::class.java)
        addDeserializer(Sort::class, SortDeserializer)
        addSerializer(Sort::class, SortSerializer)
    }

    /**
     * https://github.com/spring-cloud/spring-cloud-openfeign/blob/main/spring-cloud-openfeign-core/src/main/java/org/springframework/cloud/openfeign/support/SortJsonComponent.java
     */
    private object SortDeserializer : JsonDeserializer<Sort>() {
        override fun deserialize(
            jsonParser: JsonParser,
            deserializationContext: DeserializationContext,
        ): Sort? {
            val treeNode: TreeNode = jsonParser.codec.readTree(jsonParser)
            if (!treeNode.isArray)
                return null
            val orders: MutableList<Sort.Order> = ArrayList()
            for (jsonNode in treeNode as ArrayNode) {
                orders.add(Sort.Order(
                    Sort.Direction.valueOf(jsonNode["direction"].textValue()),
                    jsonNode["property"].textValue()
                ))
            }
            return Sort.by(orders)
        }
    }
    private object SortSerializer : JsonSerializer<Sort>() {
        override fun serialize(
            value: Sort,
            gen: JsonGenerator,
            serializers: SerializerProvider,
        ) {
            gen.writeStartArray()
            value.iterator().forEachRemaining { v ->
                gen.writeObject(v)
            }
            gen.writeEndArray()
        }
    }

    @JsonDeserialize(`as` = PageImplWrapper::class) @JsonIgnoreProperties(ignoreUnknown = true)
    private interface PageMixIn

    internal class PageImplWrapper<T>(
        @JsonProperty("content") content: List<T>,
        @JsonProperty("number") number: Int,
        @JsonProperty("size") size: Int,
        @JsonProperty("totalElements")
        @JsonAlias("total-elements", "total_elements", "totalelements", "TotalElements")
        totalElements: Long,
        @JsonProperty("sort") sort: Sort?,
    ) : Page<T> {

        private var delegate: Page<T> = if (size > 0) {
            PageImpl(
                content,
                if (sort != null) {
                    PageRequest.of(number, size, sort)
                } else {
                    PageRequest.of(number, size)
                },
                totalElements
            )
        } else {
            PageImpl(content)
        }

        @JsonProperty override fun getTotalPages(): Int =
            delegate.totalPages

        @JsonProperty override fun getTotalElements(): Long =
            delegate.totalElements

        @JsonProperty override fun getNumber(): Int =
            delegate.number

        @JsonProperty override fun getSize(): Int =
            delegate.size

        @JsonProperty override fun getNumberOfElements(): Int =
            delegate.numberOfElements

        @JsonProperty override fun getContent(): List<T> =
            delegate.content

        @JsonProperty override fun hasContent(): Boolean =
            delegate.hasContent()

        @JsonIgnore override fun getSort(): Sort =
            delegate.sort

        @JsonProperty override fun isFirst(): Boolean =
            delegate.isFirst

        @JsonProperty override fun isLast(): Boolean =
            delegate.isLast

        @JsonIgnore override fun hasNext(): Boolean =
            delegate.hasNext()

        @JsonIgnore override fun hasPrevious(): Boolean =
            delegate.hasPrevious()

        @JsonIgnore override fun nextPageable(): Pageable =
            delegate.nextPageable()

        @JsonIgnore override fun previousPageable(): Pageable =
            delegate.previousPageable()

        @JsonIgnore override fun <S> map(converter: java.util.function.Function<in T, out S>): Page<S> =
            delegate.map(converter)

        @JsonIgnore override fun iterator(): MutableIterator<T> =
            delegate.iterator()

        @JsonIgnore override fun getPageable(): Pageable =
            delegate.pageable

        @JsonIgnore override fun isEmpty(): Boolean =
            delegate.isEmpty

        override fun hashCode(): Int =
            delegate.hashCode()

        override fun equals(obj: Any?): Boolean =
            delegate == obj

        override fun toString(): String =
            delegate.toString()
    }
}