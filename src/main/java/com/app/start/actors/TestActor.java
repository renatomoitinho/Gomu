package com.app.start.actors;

import akka.actor.UntypedActor;
import akka.japi.Creator;

/**
 * Created with IntelliJ IDEA.
 *
 * @author renatomoitinhodias@gmail.com
 * @since 17/11/14 18:02
 */
public class TestActor extends UntypedActor {

    @Override
    public void onReceive(Object msg) {
        if (msg == "Test!!!") {
            getSender().tell("Ok", getSelf());
        } else {
            unhandled(msg);
        }
    }

    public static class TestActorCreator implements Creator<TestActor> {
        @Override
        public TestActor create() {
                return new TestActor();
        }
    }
}
