package be.e_contract.jsf.taglib;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

public class ExamplePhaseListener implements PhaseListener {

    private static final Logger LOGGER = Logger.getLogger(ExamplePhaseListener.class.getName());

    @Override
    public void afterPhase(PhaseEvent phaseEvent) {
        LOGGER.log(Level.FINE, "afterPhase: {0}", phaseEvent.getPhaseId());
    }

    @Override
    public void beforePhase(PhaseEvent phaseEvent) {
        LOGGER.log(Level.FINE, "beforePhase: {0}", phaseEvent.getPhaseId());
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }
}
