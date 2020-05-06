/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.internal.concurrent;

import org.gradle.concurrent.ParallelismConfiguration;
import org.gradle.internal.service.scopes.EventScope;
import org.gradle.internal.service.scopes.Scopes;

/**
 * A listener that is notified when the parallelism configuration changes.
 */
@EventScope(Scopes.Global)
public interface ParallelismConfigurationListener {
    /**
     * Called when the parallelism configuration is changed during the build lifecycle.
     *
     * @param parallelismConfiguration - the new parallelism configuration
     */
    void onParallelismConfigurationChange(ParallelismConfiguration parallelismConfiguration);
}
