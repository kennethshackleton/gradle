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

package org.gradle.workers.internal

import org.gradle.api.JavaVersion
import org.gradle.integtests.fixtures.AbstractIntegrationSpec
import org.gradle.integtests.fixtures.AvailableJavaHomes
import org.gradle.internal.jvm.Jvm
import org.gradle.workers.fixtures.WorkerExecutorFixture
import org.junit.Assume

class WorkerExecutorProblemsApiIntegrationTest extends AbstractIntegrationSpec {

    def setupBuild(Jvm javaVersion) {
        file('buildSrc/build.gradle') << """
            plugins {
                id 'java'
            }

            dependencies {
                implementation(gradleApi())
            }

            tasks.withType(JavaCompile) {
                options.fork = true
                // We don't use toolchains here for consistency with the rest of the test suite
                options.forkOptions.javaHome = file('${javaVersion.javaHome}')
            }
        """
        file('buildSrc/src/main/java/org/gradle/test/ProblemsWorkerTaskParameter.java') << """
            package org.gradle.test;

            import org.gradle.workers.WorkParameters;

            public interface ProblemsWorkerTaskParameter extends WorkParameters { }
        """

        file('buildSrc/src/main/java/org/gradle/test/ProblemWorkerTask.java') << """
            package org.gradle.test;

            import org.gradle.api.problems.ProblemBuilder;
            import org.gradle.api.problems.ProblemBuilderDefiningLabel;
            import org.gradle.api.problems.ProblemBuilderSpec;
            import org.gradle.api.problems.Problems;
            import org.gradle.api.problems.ReportableProblem;
            import org.gradle.workers.WorkAction;

            import javax.inject.Inject;

            public abstract class ProblemWorkerTask implements WorkAction<ProblemsWorkerTaskParameter> {

                @Inject
                public abstract Problems getProblems();

                @Override
                public void execute() {
                    getProblems().create(
                        new ProblemBuilderSpec() {
                            @Override
                            public ProblemBuilder apply(ProblemBuilderDefiningLabel builder) {
                                return builder
                                    .label("label")
                                    .undocumented()
                                    .stackLocation()
                                    .category("type");
                            }
                        }
                    ).report();
                }
            }
        """
    }

    def "problems are logged worker lifecycle is logged in #isolationMode"() {
        enableProblemsApiCheck()

        given:
        buildFile << """
            import javax.inject.Inject
            import org.gradle.test.ProblemWorkerTask


            abstract class ProblemTask extends DefaultTask {
                @Inject
                abstract WorkerExecutor getWorkerExecutor();

                @TaskAction
                void executeTask() {
                    getWorkerExecutor().${isolationMode}().submit(ProblemWorkerTask.class) {}
                }
            }

            tasks.register("reportProblem", ProblemTask)
        """

        when:
        run("reportProblem")

        then:
        collectedProblems.size() == 1

        where:
        isolationMode << WorkerExecutorFixture.ISOLATION_MODES
    }

    def "problems can be logged, when using process isolation with different Java versions"() {
        Assume.assumeNotNull(javaVersion)
        setupBuild(javaVersion)
        enableProblemsApiCheck()

        given:
        buildFile << """
            import javax.inject.Inject
            import org.gradle.test.ProblemWorkerTask


            abstract class ProblemTask extends DefaultTask {
                @Inject
                abstract WorkerExecutor getWorkerExecutor();

                @TaskAction
                void executeTask() {
                    getWorkerExecutor().processIsolation({
                        it.forkOptions.executable = "${javaVersion.javaExecutable}"
                    }).submit(ProblemWorkerTask.class) {}
                }
            }

            tasks.register("reportProblem", ProblemTask)
        """

        when:
        run("reportProblem")

        then:
        collectedProblems.size() == 1

        where:
        javaVersion << [
            AvailableJavaHomes.getJdk(JavaVersion.VERSION_1_6),
            AvailableJavaHomes.getJdk(JavaVersion.VERSION_1_7),
            AvailableJavaHomes.getJdk(JavaVersion.VERSION_1_8),
        ]
    }

}
