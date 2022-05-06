package com.sohohouse.seven.common.dagger.scope

import javax.inject.Scope

/**
 * Analagous to [@Singleton].  Not using it because, this is a little more clear and I'd like to use
 * [@Singleton] in a situation where creating a scope is not possible (e.g. which might happen
 * with cross gradle module projects
 */
@Scope
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class AppScope