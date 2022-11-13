package arzaq.azmi.moviecatalogue.ui.detail

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertNotNull

open class DetailViewModelTest {
    lateinit var detailViewModel: DetailViewModel

    @Before
    open fun setData() {
        detailViewModel = DetailViewModel()
        detailViewModel.setData(0, "movie", "en")
    }

    @Test
    open fun getData() {
        val data = detailViewModel.getData()
        assertNotNull(data)
        assertEquals(11, data.value?.id)
    }
}