package com.hako.userlist.feature

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.platform.app.InstrumentationRegistry
import com.hako.base.domain.database.dao.UserDao
import com.hako.testing.isTextDisplayed
import com.hako.userlist.domain.datasource.UserlistRemoteApi
import com.hako.userlist.domain.usecase.GetFavoriteUsers
import com.hako.userlist.domain.usecase.GetUsers
import com.hako.userlist.domain.usecase.SetFavoriteStatus
import com.hako.userlist.model.User
import com.hako.userlist.model.toUserEntity
import com.hako.userlist.viewmodel.UserlistViewmodel
import org.junit.Before
import org.junit.Test
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.dsl.module

class UserlistFragmentTest {

    @Before
    fun setupKoin() {
        startKoin {
            InstrumentationRegistry.getInstrumentation().targetContext
            modules(module {
                factory { GetUsers(get()) }
                factory { GetFavoriteUsers(get()) }
                factory { SetFavoriteStatus(get()) }
                viewModel { UserlistViewmodel() }
            })
        }
    }

    @Test
    fun shouldShowUserlist_withAllUsers() {
        userlist {
            initialState()
        } should {
            showTwoBasicUsers()
        }
    }

    private fun userlist(func: UserlistRobot.() -> Unit) =
        UserlistRobot().apply {
            func()
        }
}

class UserlistRobot {

    fun initialState() {
        loadKoinModules(
            module {
                factory<UserDao> { MockUserDao(loadTwoBasicUsers().map { it.toUserEntity() }) }
                factory<UserlistRemoteApi> { MockUserApi(loadTwoBasicUsers()) }
            }
        )
        launchFragmentInContainer<UserlistFragment>()
    }

    infix fun should(func: UserlistResult.() -> Unit) {
        UserlistResult().apply { func() }
    }

    private fun loadTwoBasicUsers() = listOf(
        User(
            1,
            "Marian Arriaga",
            "mariancita",
            "test@gmail.com",
            "+56873912",
            "www.test.com"
        ),
        User(
            2,
            "Carlos Martinez",
            "carlitos",
            "test2@gmail.com",
            "+56873912",
            "www.test2.com"
        )
    )
}

class UserlistResult {
    fun showTwoBasicUsers() {
        "Marian Arriaga".isTextDisplayed()
        "mariancita".isTextDisplayed()
        "Carlos Martinez".isTextDisplayed()
        "carlitos".isTextDisplayed()
    }
}