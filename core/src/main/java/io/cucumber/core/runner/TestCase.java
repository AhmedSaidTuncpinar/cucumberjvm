package io.cucumber.core.runner;

import io.cucumber.core.api.event.Result;
import io.cucumber.core.api.event.TestStep;
import io.cucumber.core.api.event.TestCaseFinished;
import io.cucumber.core.api.event.TestCaseStarted;
import gherkin.events.PickleEvent;
import gherkin.pickles.PickleTag;
import io.cucumber.core.eventbus.EventBus;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

final class TestCase implements io.cucumber.core.api.event.TestCase {
    private final PickleEvent pickleEvent;
    private final List<PickleStepTestStep> testSteps;
    private final boolean dryRun;
    private final List<HookTestStep> beforeHooks;
    private final List<HookTestStep> afterHooks;

    public TestCase(List<PickleStepTestStep> testSteps,
                    List<HookTestStep> beforeHooks,
                    List<HookTestStep> afterHooks,
                    PickleEvent pickleEvent,
                    boolean dryRun) {
        this.testSteps = testSteps;
        this.beforeHooks = beforeHooks;
        this.afterHooks = afterHooks;
        this.pickleEvent = pickleEvent;
        this.dryRun = dryRun;
    }

    void run(EventBus bus) {
        boolean skipNextStep = this.dryRun;
        Instant startTimeInstant = bus.getInstant();
        bus.send(new TestCaseStarted(startTimeInstant, this));
        Scenario scenario = new Scenario(bus, this);

        for (HookTestStep before : beforeHooks) {
            skipNextStep |= before.run(this, bus, scenario, dryRun);
        }

        for (PickleStepTestStep step : testSteps) {
            skipNextStep |= step.run(this, bus, scenario, skipNextStep);
        }

        for (HookTestStep after : afterHooks) {
            after.run(this, bus, scenario, dryRun);
        }

        Instant stopTimeInstant = bus.getInstant();
        bus.send(new TestCaseFinished(stopTimeInstant, this, new Result(scenario.getStatus(), Duration.between(startTimeInstant, stopTimeInstant), scenario.getError())));
    }

    @Override
    public List<TestStep> getTestSteps() {
        List<TestStep> testSteps = new ArrayList<TestStep>(beforeHooks);
        for (PickleStepTestStep step : this.testSteps) {
            testSteps.addAll(step.getBeforeStepHookSteps());
            testSteps.add(step);
            testSteps.addAll(step.getAfterStepHookSteps());
        }
        testSteps.addAll(afterHooks);
        return testSteps;
    }

    @Override
    public String getName() {
        return pickleEvent.pickle.getName();
    }

    @Override
    public String getScenarioDesignation() {
        return fileColonLine(getLine()) + " # " + getName();
    }

    @Override
    public String getUri() {
        return pickleEvent.uri;
    }

    public Integer getLine() {
        return pickleEvent.pickle.getLocations().get(0).getLine();
    }

    private String fileColonLine(Integer line) {
        return URI.create(pickleEvent.uri).getSchemeSpecificPart() + ":" + line;
    }

    @Override
    public List<PickleTag> getTags() {
        return pickleEvent.pickle.getTags();
    }
}