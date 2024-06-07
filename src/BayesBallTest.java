import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BayesBallTest {

    private Variable L;
    private Variable R;
    private Variable D;
    private Variable T;
    private Variable B;
    private Variable Tprime;

    private Variable B_alarm;
    private Variable E_alarm;
    private Variable A_alarm;
    private Variable J_alarm;
    private Variable M_alarm;

    @BeforeEach
    public void setUpGivenNetwork() {
        L = new Variable("L", List.of("T", "F"));
        R = new Variable("R", List.of("T", "F"));
        D = new Variable("D", List.of("T", "F"));
        T = new Variable("T", List.of("T", "F"));
        B = new Variable("B", List.of("T", "F"));
        Tprime = new Variable("Tprime'", List.of("T", "F"));

        R.addParent(L);
        L.addChild(R);
        D.addParent(R);
        R.addChild(D);
        R.addChild(T);
        B.addChild(T);
        T.addParent(B);
        T.addParent(R);
        Tprime.addParent(T);
        T.addChild(Tprime);
    }

    @BeforeEach
    public void setUpAlarmNetwork() {
        B_alarm = new Variable("Burglary", List.of("T", "F"));
        E_alarm = new Variable("Earthquake", List.of("T", "F"));
        A_alarm = new Variable("Alarm", List.of("T", "F"));
        J_alarm = new Variable("JohnCalls", List.of("T", "F"));
        M_alarm = new Variable("MaryCalls", List.of("T", "F"));

        A_alarm.addParent(B_alarm);
        A_alarm.addParent(E_alarm);
        B_alarm.addChild(A_alarm);
        E_alarm.addChild(A_alarm);
        J_alarm.addParent(A_alarm);
        A_alarm.addChild(J_alarm);
        M_alarm.addParent(A_alarm);
        A_alarm.addChild(M_alarm);
    }

    // Tests for the given network
    @Test
    public void testLAndTprimeAreIndependentGivenT() {
        T.setEvidence(true);
        BayesBall bayesBall = new BayesBall(L, Tprime);
        bayesBall.findAllPaths();
        assertTrue(bayesBall.isIndependent(), "L and Tprime should be independent given T");
    }

    @Test
    public void testLAndBAreIndependent() {
        BayesBall bayesBall = new BayesBall(L, B);
        bayesBall.findAllPaths();
        assertTrue(bayesBall.isIndependent(), "L and B should be independent without evidence");
    }

    @Test
    public void testLAndBAreNotIndependentGivenT() {
        T.setEvidence(true);
        BayesBall bayesBall = new BayesBall(L, B);
        bayesBall.findAllPaths();
        assertFalse(bayesBall.isIndependent(), "L and B should not be independent given T");
    }

    @Test
    public void testLAndBAreNotIndependentGivenTprime() {
        Tprime.setEvidence(true);
        BayesBall bayesBall = new BayesBall(L, B);
        bayesBall.findAllPaths();
        assertFalse(bayesBall.isIndependent(), "L and B should not be independent given Tprime");
    }

    @Test
    public void testLAndBAreIndependentGivenTAndR() {
        T.setEvidence(true);
        R.setEvidence(true);
        BayesBall bayesBall = new BayesBall(L, B);
        bayesBall.findAllPaths();
        assertTrue(bayesBall.isIndependent(), "L and B should be independent given T and R");
    }

    @Test
    public void testLAndDAreIndependentGivenT() {
        T.setEvidence(true);
        BayesBall bayesBall = new BayesBall(L, D);
        bayesBall.findAllPaths();
        assertFalse(bayesBall.isIndependent(), "should not be independent given T");
    }

    @Test
    public void testLAndTprimeAreNotIndependent() {
        BayesBall bayesBall = new BayesBall(L, Tprime);
        bayesBall.findAllPaths();
        assertFalse(bayesBall.isIndependent(), "L and Tprime should not be independent without evidence");
    }

    @Test
    public void testRAndDAreNotIndependent() {
        BayesBall bayesBall = new BayesBall(R, D);
        bayesBall.findAllPaths();
        assertFalse(bayesBall.isIndependent(), "R and D should not be independent");
    }

    @Test
    public void testDAndTprimeAreIndependentGivenT() {
        T.setEvidence(true);
        BayesBall bayesBall = new BayesBall(D, Tprime);
        bayesBall.findAllPaths();
        assertTrue(bayesBall.isIndependent(), "D and Tprime should be independent given T");
    }

    // Tests for the alarm network
    @Test
    public void testBAndEAreIndependent() {
        BayesBall bayesBall = new BayesBall(B_alarm, E_alarm);
        bayesBall.findAllPaths();
        assertTrue(bayesBall.isIndependent(), "Burglary and Earthquake should be independent");
    }

    @Test
    public void testBAndJAreNotIndependent() {
        BayesBall bayesBall = new BayesBall(B_alarm, J_alarm);
        bayesBall.findAllPaths();
        assertFalse(bayesBall.isIndependent(), "Burglary and JohnCalls should not be independent");
    }

    @Test
    public void testBAndMAreNotIndependent() {
        BayesBall bayesBall = new BayesBall(B_alarm, M_alarm);
        bayesBall.findAllPaths();
        assertFalse(bayesBall.isIndependent(), "Burglary and MaryCalls should not be independent");
    }

    @Test
    public void testBAndMAreIndependentGivenA() {
        A_alarm.setEvidence(true);
        BayesBall bayesBall = new BayesBall(B_alarm, M_alarm);
        bayesBall.findAllPaths();
        assertTrue(bayesBall.isIndependent(), "Burglary and MaryCalls should be independent given Alarm");
    }

    @Test
    public void testEAndJAreIndependentGivenA() {
        A_alarm.setEvidence(true);
        BayesBall bayesBall = new BayesBall(E_alarm, J_alarm);
        bayesBall.findAllPaths();
        assertTrue(bayesBall.isIndependent(), "Earthquake and JohnCalls should be independent given Alarm");
    }

    @Test
    public void testJAndMAreNotIndependent() {
        BayesBall bayesBall = new BayesBall(J_alarm, M_alarm);
        bayesBall.findAllPaths();
        assertFalse(bayesBall.isIndependent(), "JohnCalls and MaryCalls should not be independent");
    }

    @Test
    public void testJAndMAreIndependentGivenA() {
        A_alarm.setEvidence(true);
        BayesBall bayesBall = new BayesBall(J_alarm, M_alarm);
        bayesBall.findAllPaths();
        assertTrue(bayesBall.isIndependent(), "JohnCalls and MaryCalls should be independent given Alarm");
    }
}
