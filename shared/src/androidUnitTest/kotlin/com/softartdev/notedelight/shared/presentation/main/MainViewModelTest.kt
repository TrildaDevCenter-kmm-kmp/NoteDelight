package com.softartdev.notedelight.shared.presentation.main

import android.database.sqlite.SQLiteException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.softartdev.notedelight.shared.CoroutineDispatchersStub
import com.softartdev.notedelight.shared.db.Note
import com.softartdev.notedelight.shared.db.NoteDAO
import com.softartdev.notedelight.shared.db.SafeRepo
import com.softartdev.notedelight.shared.navigation.AppNavGraph
import com.softartdev.notedelight.shared.navigation.Router
import com.softartdev.notedelight.shared.presentation.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mockSafeRepo = Mockito.mock(SafeRepo::class.java)
    private val mockRouter = Mockito.mock(Router::class.java)
    private val mockNoteDAO = Mockito.mock(NoteDAO::class.java)
    private val coroutineDispatchers = CoroutineDispatchersStub(testDispatcher = mainDispatcherRule.testDispatcher)
    private var mainViewModel: MainViewModel = MainViewModel(mockSafeRepo, mockRouter, coroutineDispatchers)

    @Before
    fun setUp() {
        Mockito.`when`(mockSafeRepo.noteDAO).thenReturn(mockNoteDAO)
        mainViewModel = MainViewModel(mockSafeRepo, mockRouter, coroutineDispatchers)
    }

    @Test
    fun success() = runTest {
        mainViewModel.stateFlow.test {
            assertEquals(NoteListResult.Loading, awaitItem())

            val notes = emptyList<Note>()
            Mockito.`when`(mockNoteDAO.listFlow).thenReturn(flowOf(notes))
            mainViewModel.updateNotes()
            assertEquals(NoteListResult.Success(notes), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun navMain() = runTest {
        mainViewModel.stateFlow.test {
            assertEquals(NoteListResult.Loading, awaitItem())

            Mockito.`when`(mockNoteDAO.listFlow).thenReturn(flow { throw SQLiteException() })
            mainViewModel.updateNotes()
            assertEquals(NoteListResult.Error(null), awaitItem())
            Mockito.verify(mockRouter).navigateClearingBackStack(route = AppNavGraph.SignIn)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onNoteClicked() {
        mainViewModel.onNoteClicked(1)
        Mockito.verify(mockRouter).navigate(route = AppNavGraph.Details(noteId = 1))
    }

    @Test
    fun onSettingsClicked() {
        mainViewModel.onSettingsClicked()
        Mockito.verify(mockRouter).navigate(route = AppNavGraph.Settings)
    }

    @Test
    fun error() = runTest {
        mainViewModel.stateFlow.test {
            assertEquals(NoteListResult.Loading, awaitItem())

            Mockito.`when`(mockNoteDAO.listFlow).thenReturn(flow { throw Throwable() })
            mainViewModel.updateNotes()
            assertEquals(NoteListResult.Error(null), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}