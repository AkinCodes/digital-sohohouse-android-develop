package com.sohohouse.seven.common.apihelpers

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.base.model.error
import com.sohohouse.seven.network.base.model.value
import com.sohohouse.seven.network.core.models.Meta
import com.sohohouse.seven.network.core.request.CoreAPIRequestPagable
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class PagedRequestHelperTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var request: CoreAPIRequestPagable<List<Any>>

    private lateinit var pageHelper: PagedRequestHelper<List<Any>, CoreAPIRequestPagable<List<Any>>>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        pageHelper = PagedRequestHelper()
    }

    @After
    fun tearDown() {
        Mockito.reset(request)
    }

    @Test
    fun `when request is formed for page one, page and page count are set`() {
        // GIVEN request available for paging
        `when`(request.page).thenReturn(1)
        `when`(request.perPage).thenReturn(10)

        // WHEN request is prepared
        pageHelper.prepareForPageOne(request)

        // THEN page and pageCount of pageHelper is initialized
        assertThat(pageHelper.areMorePageAvailable).isTrue()
    }

    @Test
    fun `on success with a response that has another page remaining, page is incremented`() {
        // GIVEN that the stream of the request returns successfully (no server error)
        val successResponse: List<Any> = listOf(mock())
        val success: Either<ServerError, List<Any>> = value(successResponse)
        val stream = Single.just(success)
        val currentPage = 1

        `when`(request.page).thenReturn(currentPage, currentPage + 1)
        `when`(request.getMeta(any())).thenReturn(
            Meta(
                totalItems = 10,
                page = currentPage,
                perPage = 6,
                totalPages = 2,
                10
            )
        )

        // AND it's meta has another page remaining
        pageHelper.prepareForPageOne(request)

        // WHEN the stream is tracked, and subscribed
        pageHelper.trackPaging(stream).test()

        // THEN the page will increment
        verify(request).page = currentPage + 1
        assertThat(pageHelper.areMorePageAvailable).isEqualTo(true)
    }

    @Test
    fun `on success with a response that has a null meta, areMorePageAvailable flag is false`() {
        // GIVEN that the stream of the request returns successfully (no server error)
        val successResponse: List<Any> = listOf(mock())
        val success: Either<ServerError, List<Any>> = value(successResponse)
        val stream = Single.just(success)
        val currentPage = 1

        `when`(request.page).thenReturn(currentPage, currentPage + 1)
        // BUT the meta is null
        `when`(request.getMeta(any())).thenReturn(null)

        pageHelper.prepareForPageOne(request)

        // WHEN the stream is tracked, and subscribed
        pageHelper.trackPaging(stream).test()

        // THEN the flag is false
        assertThat(pageHelper.areMorePageAvailable).isEqualTo(false)
    }


    @Test
    fun `on success with a response that has no pages remaining, areMorePageAvailable flag is false`() {
        // GIVEN that the stream of the request returns successfully (no server error)
        val successResponse: List<Any> = listOf(mock())
        val success: Either<ServerError, List<Any>> = value(successResponse)
        val stream = Single.just(success)
        val currentPage = 1

        `when`(request.page).thenReturn(currentPage, currentPage + 1)
        // AND it's meta has no other page remaining
        `when`(request.getMeta(any())).thenReturn(
            Meta(
                totalItems = 5,
                page = currentPage,
                perPage = 6,
                totalPages = 1,
                5
            )
        )

        pageHelper.prepareForPageOne(request)

        // WHEN the stream is tracked, and subscribed
        pageHelper.trackPaging(stream).test()

        // THEN the flag is false
        assertThat(pageHelper.areMorePageAvailable).isEqualTo(false)
    }

    @Test
    fun `on success with a empty response, areMorePageAvailable flag is false`() {
        // GIVEN that the stream of the request returns successfully (no server error)
        // BUT the response is an empty list
        val success: Either<ServerError, List<Any>> = value(emptyList())
        val stream = Single.just(success)
        val currentPage = 1

        `when`(request.page).thenReturn(currentPage, currentPage + 1)

        pageHelper.prepareForPageOne(request)

        // WHEN the stream is tracked, and subscribed
        pageHelper.trackPaging(stream).test()

        // THEN the flag is false
        assertThat(pageHelper.areMorePageAvailable).isEqualTo(false)
    }

    @Test
    fun `on success, downstream gets it`() {
        // GIVEN that the stream of the request returns successfully (no server error)
        pageHelper.prepareForPageOne(request)
        val success: Either<ServerError, List<Any>> = value(listOf())
        val stream = Single.just(success)

        // WHEN the stream is tracked, and subscribed
        val testObserver = pageHelper.trackPaging(stream).test()

        // THEN down stream will recieve the output of the request
        testObserver.assertNoErrors()
        testObserver.assertValue { it == success }
    }

    @Test
    fun `on server error, page stays the same`() {
        // GIVEN that the stream of the request returns with a server error
        val serverError: Either<ServerError, List<Any>> = error(ServerError.NO_INTERNET)
        val stream = Single.just(serverError)

        pageHelper.prepareForPageOne(request)

        // WHEN the stream is tracked, and subscribed
        val testObserver = pageHelper.trackPaging(stream).test()

        // THEN the page value will not change
        testObserver.assertNoErrors()
    }

    @Test
    fun `on server error, downstream gets it`() {
        // GIVEN that the stream of the request returns with a server error
        val serverError: Either<ServerError, List<Any>> = error(ServerError.NO_INTERNET)
        val stream = Single.just(serverError)

        pageHelper.prepareForPageOne(request)

        // WHEN the stream is tracked, and subscribed
        val testObserver = pageHelper.trackPaging(stream).test()

        // THEN the downstream will receive it
        testObserver.assertNoErrors()
        testObserver.assertValue { it == serverError }
    }

    @Test
    fun `on stream error, page stays the same`() {
        // GIVEN that the stream of the request throws an exception
        pageHelper.prepareForPageOne(request)
        val exception = RuntimeException()
        val stream = Single.error<Either<ServerError, List<Any>>>(exception)
        val currentPage = pageHelper.request.page

        // WHEN the stream is tracked and subscribed
        pageHelper.trackPaging(stream).test()

        // THEN the page stays the same
        assertThat(pageHelper.request.page).isEqualTo(currentPage)
    }

    @Test
    fun `on stream error, downstream gets it`() {
        // GIVEN that the stream of the request throws an exception
        val exception = RuntimeException()
        val stream = Single.error<Either<ServerError, List<Any>>>(exception)

        // WHEN the stream is tracked and subscribed
        val testObserver = pageHelper.trackPaging(stream).test()

        // THEN downstream will receive it
        testObserver.assertError { it == exception }
    }
}