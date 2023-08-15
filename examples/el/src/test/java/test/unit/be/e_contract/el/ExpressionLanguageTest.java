package test.unit.be.e_contract.el;

import javax.el.ELContext;
import javax.el.ELManager;
import javax.el.ELProcessor;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExpressionLanguageTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpressionLanguageTest.class);

    private ELContext elContext;

    private ExpressionFactory expressionFactory;

    private final int COUNT = 10000000;

    @BeforeEach
    public void beforeEach() {
        ELProcessor elProcessor = new ELProcessor();
        elProcessor.defineBean("someBean", new SomeBean());
        ELManager elManager = elProcessor.getELManager();
        this.elContext = elManager.getELContext();
        this.expressionFactory = ELManager.getExpressionFactory();
    }

    private long runTiming(Runnable runnable) {
        long t0 = System.currentTimeMillis();
        for (int idx = 0; idx < COUNT; idx++) {
            runnable.run();
        }
        long t1 = System.currentTimeMillis();
        long dt = t1 - t0;
        LOGGER.debug("dt: {} ms", dt);
        return dt;
    }

    private void comparePerformance(Runnable original, Runnable optimized) {
        double dtOriginal = runTiming(original);
        double dtOptimized = runTiming(optimized);
        double speedGain = (dtOriginal - dtOptimized) / dtOptimized * 100;
        LOGGER.debug("speed gain: {} %", speedGain);
    }

    private void comparePerformance(String originalEL, String optimizedEL) {
        ValueExpression originalValueExpression
                = this.expressionFactory.createValueExpression(
                        this.elContext,
                        originalEL,
                        Integer.class);
        ValueExpression optimizedValueExpression
                = this.expressionFactory.createValueExpression(
                        this.elContext,
                        optimizedEL,
                        Integer.class);
        comparePerformance(
                () -> originalValueExpression.getValue(this.elContext),
                () -> optimizedValueExpression.getValue(this.elContext)
        );
    }

    @Test
    public void testPerformanceMethods() throws Exception {
        comparePerformance(
                "#{someBean.someMethod() + someBean.anotherMethod()}",
                "#{someBean.someMethodCombinedWithAnotherMethod()}");
    }

    @Test
    public void testPerformanceDots() throws Exception {
        comparePerformance(
                "#{someBean.someProperty.anotherProperty}",
                "#{someBean.someOtherProperty}");
    }

    public static class SomeBean {

        private final SomeOtherBean someOtherBean = new SomeOtherBean();

        public SomeOtherBean getSomeProperty() {
            return this.someOtherBean;
        }

        public int someMethod() {
            return 5;
        }

        public int anotherMethod() {
            return 10;
        }

        public int someMethodCombinedWithAnotherMethod() {
            return this.someMethod() + this.anotherMethod();
        }

        public int getSomeOtherProperty() {
            return this.someOtherBean.getAnotherProperty();
        }
    }

    public static class SomeOtherBean {

        public int getAnotherProperty() {
            return 1;
        }
    }
}
