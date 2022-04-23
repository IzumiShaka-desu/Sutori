package com.darkshandev.sutori

import androidx.test.espresso.IdlingRegistry
import com.darkshandev.sutori.utils.EspressoIdlingResource
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class IdlingResourceRule : TestRule {

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                try {
                    IdlingRegistry.getInstance().register(EspressoIdlingResource.idlingResource)
                    base.evaluate()
                } finally {
                    IdlingRegistry.getInstance().unregister(EspressoIdlingResource.idlingResource)
                }
            }
        }
    }
}