package bayesball;

import inference.Variable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BayesBallTest {

    private Variable E;
    private Variable B;
    private Variable A;
    private Variable J;
    private Variable M;

    @BeforeEach
    public void setUpAlarmNetwork() {
        E = new Variable("E", List.of("T", "F"));
        B = new Variable("B", List.of("T", "F"));
        A = new Variable("A", List.of("T", "F"));
        J = new Variable("J", List.of("T", "F"));
        M = new Variable("M", List.of("T", "F"));

        A.addParent(E);
        A.addParent(B);
        E.addChild(A);
        B.addChild(A);
        J.addParent(A);
        A.addChild(J);
        M.addParent(A);
        A.addChild(M);
    }

    @Test
    public void testJAndMAreIndependentGivenA() {
        BayesBallQuery query = new BayesBallQuery();
        query.setStartVariable(J);
        query.setEndVariable(M);
        query.addEvidenceVariable(A);
        BayesBall bayesBall = new BayesBall(query);
        bayesBall.findAllPaths();
        assertTrue(bayesBall.isIndependent(), "J and M should be independent given A");
    }

    @Test
    public void testJAndMAreNotIndependent() {
        BayesBallQuery query = new BayesBallQuery();
        query.setStartVariable(J);
        query.setEndVariable(M);
        BayesBall bayesBall = new BayesBall(query);
        bayesBall.findAllPaths();
        assertFalse(bayesBall.isIndependent(), "J and M should not be independent");
    }

    @Test
    public void testJAndEAreIndependentGivenA() {
        BayesBallQuery query = new BayesBallQuery();
        query.setStartVariable(J);
        query.setEndVariable(E);
        query.addEvidenceVariable(A);
        BayesBall bayesBall = new BayesBall(query);
        bayesBall.findAllPaths();
        assertTrue(bayesBall.isIndependent(), "J and E should be independent given A");
    }

    @Test
    public void testJAndEAreIndependentGivenB() {
        BayesBallQuery query = new BayesBallQuery();
        query.setStartVariable(J);
        query.setEndVariable(E);
        query.addEvidenceVariable(B);
        BayesBall bayesBall = new BayesBall(query);
        bayesBall.findAllPaths();
        assertFalse(bayesBall.isIndependent(), "J and E should be independent given B");
    }

    @Test
    public void testAAndBAreNotIndependentGivenE() {
        BayesBallQuery query = new BayesBallQuery();
        query.setStartVariable(A);
        query.setEndVariable(B);
        query.addEvidenceVariable(E);
        BayesBall bayesBall = new BayesBall(query);
        bayesBall.findAllPaths();
        assertFalse(bayesBall.isIndependent(), "A and B should not be independent given E");
    }

    @Test
    public void testJAndBAreNotIndependentGivenEAndA() {
        BayesBallQuery query = new BayesBallQuery();
        query.setStartVariable(J);
        query.setEndVariable(B);
        query.addEvidenceVariable(E);
        query.addEvidenceVariable(A);
        BayesBall bayesBall = new BayesBall(query);
        bayesBall.findAllPaths();
        assertTrue(bayesBall.isIndependent(), "J and B should not be independent given E and A");
    }

    @Test
    public void testJAndJAreNotIndependent() {
        BayesBallQuery query = new BayesBallQuery();
        query.setStartVariable(J);
        query.setEndVariable(J);
        BayesBall bayesBall = new BayesBall(query);
        bayesBall.findAllPaths();
        assertFalse(bayesBall.isIndependent(), "J and J should not be independent");
    }

    @Test
    public void testEAndAAreNotIndependent() {
        BayesBallQuery query = new BayesBallQuery();
        query.setStartVariable(E);
        query.setEndVariable(A);
        BayesBall bayesBall = new BayesBall(query);
        bayesBall.findAllPaths();
        assertFalse(bayesBall.isIndependent(), "E and A should not be independent");
    }

    @Test
    public void testAAndEAreNotIndependentGivenA() {
        BayesBallQuery query = new BayesBallQuery();
        query.setStartVariable(A);
        query.setEndVariable(E);
        query.addEvidenceVariable(A);
        BayesBall bayesBall = new BayesBall(query);
        bayesBall.findAllPaths();
        assertFalse(bayesBall.isIndependent(), "A and E should not be independent given A");
    }
}
